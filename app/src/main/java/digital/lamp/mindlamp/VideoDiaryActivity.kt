package digital.lamp.mindlamp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
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

class VideoDiaryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVideoDiaryBinding

    private var elapsedSeconds = 0
    private var maxDurationSec = 60
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
    private val gson = Gson()
    private val uploadHttpClient = OkHttpClient()

    private var currentCamera = CameraSelector.DEFAULT_FRONT_CAMERA

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
            Toast.makeText(this, "Camera and Audio permissions required", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    // Enum to track UI state clearly
    enum class RecordingState { IDLE, RECORDING, STOPPED }

    private var recordingState = RecordingState.IDLE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoDiaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        activityId = intent.getStringExtra("ACTIVITY_ID").orEmpty()
        participantId = intent.getStringExtra("PARTICIPANT_ID").orEmpty()
        maxDurationSec = intent.getIntExtra("MAX_DURATION", 60)
        resolution = intent.getIntExtra("RESOLUTION", 0)
        frameRate = intent.getIntExtra("FRAME_RATE", 0)
        maxBitrateMbps = intent.getIntExtra("MAX_BITRATE", 0)
        updateTimerText(0)

        binding.btnRecord.setOnClickListener {
            when (recordingState) {
                RecordingState.IDLE -> startRecording()
                RecordingState.RECORDING -> stopRecording()
                RecordingState.STOPPED -> recordAgain()
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
        binding.btnRecord.text = "Start Recording"
        binding.btnRecord.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
        binding.btnRecord.background =
            ContextCompat.getDrawable(this, R.drawable.bg_button_outlined_red)
        binding.imgRedRotIndicator.background =
            ContextCompat.getDrawable(this, R.drawable.ic_record_dot)

        // Reset timer and progress
        elapsedSeconds = 0
        updateTimerText(0)
        binding.progressBar.progress = 0
    }

    private fun setStateRecording() {
        recordingState = RecordingState.RECORDING

        // Hide submit
        binding.btnSubmit.visibility = View.GONE

        // Solid red + square → "Stop Recording"
        binding.btnRecord.text = "Stop Recording"
        binding.btnRecord.setTextColor(ContextCompat.getColor(this, android.R.color.white))
        binding.btnRecord.background =
            ContextCompat.getDrawable(this, R.drawable.bg_button_solid_red)
        binding.imgRedRotIndicator.background =
            ContextCompat.getDrawable(this, R.drawable.ic_stop_square)
    }

    private fun setStateStopped() {
        recordingState = RecordingState.STOPPED

        // Show Submit (outlined blue)
        binding.btnSubmit.visibility = View.VISIBLE

        // Outlined red + dot → "Record Again"
        binding.btnRecord.text = "Record Again"
        binding.btnRecord.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
        binding.btnRecord.background =
            ContextCompat.getDrawable(this, R.drawable.bg_button_outlined_red)
        binding.imgRedRotIndicator.background =
            ContextCompat.getDrawable(this, R.drawable.ic_record_dot)
    }

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    private fun startRecording() {
        val videoCapture = videoCapture ?: return

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
                            Toast.makeText(this, "Recording failed", Toast.LENGTH_SHORT).show()
                            setStateIdle()
                        } else {
                            Log.d(TAG, "Saved: ${outputFile.absolutePath}")
                            setStateStopped()
                        }
                    }
                }
            }

        elapsedSeconds = 0
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

    private fun stopRecording() {
        countUpTimer?.cancel()
        activeRecording?.stop()
        activeRecording = null
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
                    currentCamera,
                    preview,
                    videoCapture!! // ✅ bind videoCapture too
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
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniqueWork(
            "video_upload_${file.nameWithoutExtension}",
            ExistingWorkPolicy.REPLACE,
            uploadRequest
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        countUpTimer?.cancel()
    }
}