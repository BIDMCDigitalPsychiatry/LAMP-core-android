package digital.lamp.mindlamp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.gson.Gson
import digital.lamp.mindlamp.databinding.ActivityVideoDiaryBinding
import digital.lamp.mindlamp.sheduleing.VideoUploadWorker
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class VideoDiaryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVideoDiaryBinding

    private var elapsedSeconds = 0
    private var maxDurationSec = 240
    private var countUpTimer: CountDownTimer? = null

    // CameraX
    private var videoCapture: VideoCapture<Recorder>? = null
    private var activeRecording: Recording? = null

    // File
    private var savedVideoFile: File? = null

    // Intent extras
    private var activityId: String = ""
    private var participantId: String = ""
    private var resolution = 0
    private var frameRate = 0
    private var maxBitrateMbps = 0

    private var activityName: String = "Video Recording"

    private val gson = Gson()
    private val uploadHttpClient = OkHttpClient()

    private var currentCamera = CameraSelector.DEFAULT_FRONT_CAMERA

    // Set to true while we send the user to the system app-settings screen so
    // that onResume() knows to re-check permissions when they come back.
    private var awaitingSettingsReturn = false

    companion object {
        private const val TAG = "VideoDiaryActivity"
    }

    // Replace single permission launcher with multi-permission launcher
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val cameraGranted = permissions[Manifest.permission.CAMERA] == true
        val audioGranted = permissions[Manifest.permission.RECORD_AUDIO] == true

        if (cameraGranted && audioGranted) {
            startCamera()
        } else {
            handlePermissionsDenied()
        }
    }

    // Enum to track UI state clearly.
    // STARTING is an intermediate state between the user tapping "Start Recording"
    // and the camera firing VideoRecordEvent.Start — the click listener uses it to
    // ignore double-taps that would otherwise spawn an orphan recording + timer.
    enum class RecordingState { IDLE, STARTING, RECORDING, STOPPED }

    private var recordingState = RecordingState.IDLE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoDiaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Match the system status bar to the blue header so the top of the screen
        // looks like a single continuous bar instead of two stacked colors.
        applyStatusBarColor()

        // Prevent the system from dimming/turning off the screen while the
        // user is on the camera preview or actively recording. The flag is
        // window-scoped and is automatically dropped when this activity is no
        // longer in the foreground, so no manual cleanup is needed.
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        activityId = intent.getStringExtra("ACTIVITY_ID").orEmpty()
        participantId = intent.getStringExtra("PARTICIPANT_ID").orEmpty()
        maxDurationSec = intent.getIntExtra("MAX_DURATION", 240)
        resolution = intent.getIntExtra("RESOLUTION", 0)
        frameRate = intent.getIntExtra("FRAME_RATE", 0)
        maxBitrateMbps = intent.getIntExtra("MAX_BITRATE", 0)
        activityName = intent.getStringExtra("ACTIVITY_NAME").orEmpty()

        binding.tvHeader.text = activityName

        updateTimerText(0)

        binding.btnRecord.setOnClickListener {
            when (recordingState) {
                RecordingState.IDLE -> startRecording()
                RecordingState.RECORDING -> stopRecording()
                RecordingState.STOPPED -> recordAgain()
                // Ignore taps during the brief STARTING window so we don't spawn
                // a duplicate recording + an orphan CountDownTimer.
                RecordingState.STARTING -> Unit
            }
        }

        binding.btnSubmit.setOnClickListener {
            submitVideo()
        }

        // ✅ Only check permission once — startCamera() called inside
        val cameraGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        val audioGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (cameraGranted && audioGranted) {
            startCamera()
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO
                )
            )
        }
        binding.btnFlipCamera.setOnClickListener {
            flipCamera()
        }

        binding.btnClose.setOnClickListener {
            handleCloseRequested()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleCloseRequested()
            }
        })
    }

    /**
     * Tint the system status bar to match the blue header (`@color/video_diary_header`)
     * so the top of the screen looks like a single continuous bar.
     * Safe to call repeatedly — only mutates window flags + statusBarColor.
     */
    private fun applyStatusBarColor() {
        // Make sure the system draws the status-bar background, otherwise
        // statusBarColor is ignored. Also clear any inherited translucent flag.
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.video_diary_header)
    }

    /**
     * Decide what to do after the user denies CAMERA and/or RECORD_AUDIO.
     *
     * Android exposes "Don't ask again" / permanent denial only indirectly:
     * after the system dialog has been shown at least once,
     * `shouldShowRequestPermissionRationale` returns `false` for a permission
     * that the user has permanently denied (and `true` if they merely tapped
     * "Deny" once and we can prompt them again).
     *
     * - Permanent denial → re-prompting does nothing, so we send the user to
     *   the system app-settings screen via [showOpenSettingsDialog].
     * - First-time denial → show a short rationale and let them retry the
     *   system dialog.
     */
    private fun handlePermissionsDenied() {
        val cameraPermanentlyDenied = isPermissionPermanentlyDenied(Manifest.permission.CAMERA)
        val audioPermanentlyDenied = isPermissionPermanentlyDenied(Manifest.permission.RECORD_AUDIO)

        if (cameraPermanentlyDenied || audioPermanentlyDenied) {
            showOpenSettingsDialog()
        } else {
            showPermissionRationaleDialog()
        }
    }

    private fun isPermissionPermanentlyDenied(permission: String): Boolean {
        val granted = ContextCompat.checkSelfPermission(this, permission) ==
                PackageManager.PERMISSION_GRANTED
        // Only meaningful AFTER the system dialog has been shown at least once.
        // If granted, this method returns false (not permanently denied).
        return !granted && !shouldShowRequestPermissionRationale(permission)
    }

    private fun showPermissionRationaleDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.dialog_permissions_needed_title)
            .setMessage(R.string.dialog_permissions_needed_message)
            .setPositiveButton(R.string.ok) { _, _ ->
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO
                    )
                )
            }
            .setNegativeButton(R.string.cancel) { _, _ -> finish() }
            .setCancelable(false)
            .show()
    }

    private fun showOpenSettingsDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.dialog_permissions_blocked_title)
            .setMessage(R.string.dialog_permissions_blocked_message)
            .setPositiveButton(R.string.dialog_open_settings) { _, _ ->
                openAppSettings()
            }
            .setNegativeButton(R.string.cancel) { _, _ -> finish() }
            .setCancelable(false)
            .show()
    }

    private fun openAppSettings() {
        awaitingSettingsReturn = true
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Unable to open app settings", e)
            awaitingSettingsReturn = false
            Toast.makeText(this, "Unable to open settings", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun handleCloseRequested() {
        when (recordingState) {
            RecordingState.IDLE -> finish()

            // Treat the brief STARTING window the same way as RECORDING so the
            // user always gets a confirmation prompt instead of dropping data.
            RecordingState.STARTING,
            RecordingState.RECORDING -> {
                AlertDialog.Builder(this)
                    .setTitle(R.string.dialog_leave_activity_title)
                    .setMessage(R.string.dialog_leave_recording_message)
                    .setPositiveButton(R.string.dialog_discard) { _, _ ->
                        try {
                            cancelTimer()
                            activeRecording?.stop()
                            activeRecording = null
                        } catch (e: Exception) {
                            Log.e(TAG, "Error stopping recording on close", e)
                        }
                        savedVideoFile?.delete()
                        savedVideoFile = null
                        finish()
                    }
                    .setNegativeButton(R.string.cancel, null)
                    .show()
            }

            RecordingState.STOPPED -> {
                AlertDialog.Builder(this)
                    .setTitle(R.string.dialog_leave_activity_title)
                    .setMessage(R.string.dialog_leave_stopped_message)
                    .setPositiveButton(R.string.dialog_discard) { _, _ ->
                        savedVideoFile?.delete()
                        savedVideoFile = null
                        finish()
                    }
                    .setNegativeButton(R.string.cancel, null)
                    .show()
            }
        }
    }

    private fun flipCamera() {
        // Disable flip while recording
        if (recordingState == RecordingState.RECORDING) {
            Toast.makeText(this, "Cannot flip camera while recording", Toast.LENGTH_SHORT).show()
            return
        }

        // Toggle camera
        currentCamera = if (currentCamera == CameraSelector.DEFAULT_FRONT_CAMERA) {
            CameraSelector.DEFAULT_BACK_CAMERA
        } else {
            CameraSelector.DEFAULT_FRONT_CAMERA
        }

        // Restart camera with new selector
        startCamera()

        // Animate the button
        binding.btnFlipCamera.animate()
            .rotationBy(180f)
            .setDuration(300)
            .start()
    }

    private fun setStateIdle() {
        recordingState = RecordingState.IDLE

        // Hide submit
        binding.btnSubmit.visibility = View.GONE

        // Outlined red + dot → "Start Recording"
        binding.btnRecord.text = getString(R.string.txt_start_recording)
        binding.btnRecord.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
        binding.btnRecord.background =
            ContextCompat.getDrawable(this, R.drawable.bg_button_outlined_red)
        binding.imgRedRotIndicator.background =
            ContextCompat.getDrawable(this, R.drawable.ic_record_dot)

        // Defensive: any leftover timer must die when we return to idle, so a
        // late tick can't overwrite the freshly-cleared timer text.
        cancelTimer()
        elapsedSeconds = 0
        updateTimerText(0)
        binding.progressBar.progress = 0
    }

    private fun setStateRecording() {
        recordingState = RecordingState.RECORDING

        // Hide submit
        binding.btnSubmit.visibility = View.GONE

        // Solid red + square → "Stop Recording"
        binding.btnRecord.text = getString(R.string.txt_stop_recording)
        binding.btnRecord.setTextColor(ContextCompat.getColor(this, android.R.color.white))
        binding.btnRecord.background =
            ContextCompat.getDrawable(this, R.drawable.bg_button_solid_red)
        binding.imgRedRotIndicator.background =
            ContextCompat.getDrawable(this, R.drawable.ic_stop_square)

        // Start the count-up timer here (not in startRecording()) so it's tied to
        // the actual VideoRecordEvent.Start callback. Guarantees: exactly one
        // active timer, and only while the camera is truly recording.
        cancelTimer()
        elapsedSeconds = 0
        updateTimerText(0)
        binding.progressBar.progress = 0
        countUpTimer = object : CountDownTimer(maxDurationSec * 1000L, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                elapsedSeconds++
                updateTimerText(elapsedSeconds)
                binding.progressBar.progress = (elapsedSeconds * 100) / maxDurationSec
            }

            override fun onFinish() {
                stopRecording()
            }
        }.start()
    }

    private fun setStateStopped() {
        recordingState = RecordingState.STOPPED

        // Defensive: even though stopRecording() cancels the timer, a late
        // VideoRecordEvent.Finalize from a previous attempt could still bring us
        // here — make sure no timer is left ticking by the time we show "Stopped".
        cancelTimer()

        // Show Submit (outlined blue)
        binding.btnSubmit.visibility = View.VISIBLE

        // Outlined red + dot → "Record Again"
        binding.btnRecord.text = getString(R.string.txt_record_again)
        binding.btnRecord.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
        binding.btnRecord.background =
            ContextCompat.getDrawable(this, R.drawable.bg_button_outlined_red)
        binding.imgRedRotIndicator.background =
            ContextCompat.getDrawable(this, R.drawable.ic_record_dot)
    }

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    private fun startRecording() {
        // Guard against double-taps + accidental re-entry. The state is flipped
        // immediately so subsequent clicks fall through the click listener.
        if (recordingState == RecordingState.STARTING ||
            recordingState == RecordingState.RECORDING
        ) {
            return
        }
        val videoCapture = videoCapture ?: return
        recordingState = RecordingState.STARTING

        val fileName = "VD_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.mp4"
        val outputFile = File(getExternalFilesDir(null), fileName)
        savedVideoFile = outputFile

        val outputOptions = FileOutputOptions.Builder(outputFile).build()

        activeRecording = videoCapture.output
            .prepareRecording(this, outputOptions)
            .withAudioEnabled()
            .start(ContextCompat.getMainExecutor(this)) { event ->
                when (event) {
                    is VideoRecordEvent.Start -> setStateRecording()
                    is VideoRecordEvent.Finalize -> {
                        if (event.hasError()) {
                            Log.e(TAG, "Recording error: ${event.error}")
                            setStateIdle()
                        } else {
                            Log.d(TAG, "Saved: ${outputFile.absolutePath}")
                            setStateStopped()
                        }
                    }
                }
            }
    }

    private fun stopRecording() {
        // Cancel + clear the timer reference FIRST so an orphan timer can never
        // outlive a stop. Recording finalize is async; we don't wait for it.
        cancelTimer()
        activeRecording?.stop()
        activeRecording = null
    }

    /** Cancels the count-up timer and clears the reference (idempotent). */
    private fun cancelTimer() {
        countUpTimer?.cancel()
        countUpTimer = null
    }

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    private fun recordAgain() {
        savedVideoFile?.delete() // discard previous recording
        savedVideoFile = null
        setStateIdle()
        startRecording()
    }

    private fun updateTimerText(elapsed: Int) {
        val elapsedFormatted = String.format("%d:%02d", elapsed / 60, elapsed % 60)
        val totalFormatted = String.format("%d:%02d", maxDurationSec / 60, maxDurationSec % 60)
        binding.tvTimer.text = "$elapsedFormatted / $totalFormatted"
    }

    private fun startCamera() {
        lifecycleScope.launch {
            val cameraProvider = ProcessCameraProvider.awaitInstance(this@VideoDiaryActivity)

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.previewView.surfaceProvider)
            }

            // ✅ Setup Recorder and assign to class-level videoCapture
            val recorder = Recorder.Builder()
                .setQualitySelector(QualitySelector.from(Quality.HD))
                .build()
            videoCapture = VideoCapture.withOutput(recorder)

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this@VideoDiaryActivity,
                    CameraSelector.DEFAULT_FRONT_CAMERA,
                    preview,
                    videoCapture // ✅ bind videoCapture too
                )
            } catch (e: Exception) {
                Log.e(TAG, "Camera binding failed", e)
            }
        }
    }

    private fun submitVideo() {
        val file = savedVideoFile
        if (file == null || !file.exists()) {
            Log.w(TAG, "Submit pressed but no recorded video file is available")
            finish()
            return
        }
        if (activityId.isEmpty() || participantId.isEmpty()) {
            Log.w(TAG, "Submit pressed but activity/participant id is missing")
            finish()
            return
        }

        enqueueVideoUpload(file)
        finish()
    }

    private fun enqueueVideoUpload(file: File) {
        val uploadRequest = OneTimeWorkRequestBuilder<VideoUploadWorker>()
            .addTag(VideoUploadWorker.TAG_VIDEO_UPLOAD)
            .setInputData(
                workDataOf(
                    VideoUploadWorker.KEY_FILE_PATH to file.absolutePath,
                    VideoUploadWorker.KEY_PARTICIPANT_ID to participantId,
                    VideoUploadWorker.KEY_ACTIVITY_ID to activityId,
                    VideoUploadWorker.KEY_RESOLUTION to resolution,
                    VideoUploadWorker.KEY_FRAME_RATE to frameRate,
                    VideoUploadWorker.KEY_MAX_BITRATE_MBPS to maxBitrateMbps,
                    VideoUploadWorker.KEY_ELAPSED_SECONDS to elapsedSeconds
                )
            )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            // Linear backoff so retry-on-network-loss waits a predictable time
            // (30s, 60s, 90s, 120s …) instead of slow exponential growth.
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                30L,
                TimeUnit.SECONDS
            )
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniqueWork(
            "video_upload_${file.nameWithoutExtension}",
            ExistingWorkPolicy.REPLACE,
            uploadRequest
        )
    }

    override fun onResume() {
        super.onResume()
        // Only re-check when we *know* we sent the user to Settings — otherwise
        // onResume() also fires right after onCreate()'s initial permission
        // check and would double-prompt.
        if (!awaitingSettingsReturn) return
        awaitingSettingsReturn = false

        val cameraGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        val audioGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        when {
            cameraGranted && audioGranted -> startCamera()
            // Still permanently denied after the Settings round-trip — bail
            // out gracefully so we don't loop the user back to the same dialog.
            else -> {
                Toast.makeText(
                    this,
                    "Camera and Audio permissions required",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelTimer()
    }
}