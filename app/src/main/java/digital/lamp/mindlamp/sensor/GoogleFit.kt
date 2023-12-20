package digital.lamp.mindlamp.sensor

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.*
import com.google.android.gms.fitness.data.DataType.TYPE_STEP_COUNT_DELTA
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.fitness.request.SessionReadRequest
import com.google.android.gms.fitness.result.SessionReadResponse
import digital.lamp.lamp_kotlin.lamp_core.models.*
import digital.lamp.mindlamp.R
import digital.lamp.mindlamp.appstate.AppState
import digital.lamp.mindlamp.database.entity.SensorSpecs
import digital.lamp.mindlamp.utils.DebugLogs
import digital.lamp.mindlamp.utils.LampLog
import digital.lamp.mindlamp.utils.Sensors
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Represents a Google Fit related  sensor data.
 *
 * @param sensorListener The listener for Google Fit sensor events.
 * @param context The application context.
 * @param oSensorSpecList A list of various sensors.
 */
class GoogleFit constructor(
    private var sensorListener: SensorListener,
    context: Context,
    oSensorSpecList: ArrayList<SensorSpecs>
) {

    companion object {
        private val TAG = GoogleFit::class.java.simpleName
    }

    private var sensorEventDataList: ArrayList<SensorEvent> = arrayListOf()
    val SLEEP_STAGES = arrayOf(
        context.getString(R.string.unused),
        context.getString(R.string.awake_during_sleep),
        context.getString(R.string.sleep),
        context.getString(R.string.out_of_bed),
        context.getString(R.string.light_sleep),
        context.getString(R.string.deep_sleep),
        context.getString(R.string.rem_sleep)
    )

    init {
        //Fitness options
        val fitnessOptions: FitnessOptions by lazy {
            FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_DISTANCE_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_MOVE_MINUTES, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_CALORIES_EXPENDED, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_ACTIVITY_SEGMENT, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_BASAL_METABOLIC_RATE, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_BODY_FAT_PERCENTAGE, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_SPEED, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_HYDRATION, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_NUTRITION, FitnessOptions.ACCESS_READ)
                .addDataType(HealthDataTypes.TYPE_BLOOD_GLUCOSE, FitnessOptions.ACCESS_READ)
                .addDataType(HealthDataTypes.TYPE_BLOOD_PRESSURE, FitnessOptions.ACCESS_READ)
                .addDataType(HealthDataTypes.TYPE_OXYGEN_SATURATION, FitnessOptions.ACCESS_READ)
                .addDataType(HealthDataTypes.TYPE_BODY_TEMPERATURE, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_STEP_COUNT_CADENCE, FitnessOptions.ACCESS_READ)
                .build()
        }

        //Read History Data
        val readRequest = queryFitnessData()
        Fitness.getHistoryClient(
            context,
            GoogleSignIn.getAccountForExtension(context, fitnessOptions)
        )
            .readData(readRequest)
            .addOnSuccessListener { dataReadResponse ->
                if (dataReadResponse.dataSets.isNotEmpty()) {
                    dataReadResponse.dataSets.forEach {
                        dumpDataSet(it, oSensorSpecList)

                    }
                }

                readStepCount(context, oSensorSpecList)

                readSleepSessions(context, oSensorSpecList)

                sensorListener.getGoogleFitData(sensorEventDataList)

            }
            .addOnFailureListener { exception ->
                LampLog.e(TAG, "Problem in reading data : $exception")
            }

    }

    @SuppressLint("SuspiciousIndentation")
    private fun readStepCount(context: Context, oSensorSpecList: ArrayList<SensorSpecs>) {
        val fitnessOptionsStep: GoogleSignInOptionsExtension = FitnessOptions.builder()
            .addDataType(TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ).build()
        if (AppState.session.lastStepDataTimestamp == 1L)
            AppState.session.lastStepDataTimestamp = System.currentTimeMillis() - 60000
        val startTime: Long = AppState.session.lastStepDataTimestamp
        val endTime = System.currentTimeMillis()

        Fitness.getHistoryClient(
            context,
            GoogleSignIn.getAccountForExtension(context, fitnessOptionsStep)
        )
            .readData(
                DataReadRequest.Builder()
                    .read(TYPE_STEP_COUNT_DELTA)
                    .setTimeRange(
                        startTime,
                        endTime,
                        TimeUnit.MILLISECONDS
                    )
                    .build()
            ).addOnSuccessListener { result ->
                val sensorList: ArrayList<SensorEvent> = arrayListOf()
                if (result.dataSets.isNotEmpty()) {
                    result.dataSets.forEach { dataSet ->
                        val dataPoints = dataSet.dataPoints
                        if (dataPoints.size > 0) {
                            val endTimeList =
                                dataSet.dataPoints.map { it.getEndTime(TimeUnit.MILLISECONDS) }
                            AppState.session.lastStepDataTimestamp =
                                Collections.max(endTimeList) + 1

                            for (i in 0 until dataPoints.size) {
                                val dataSource = dataPoints[i].originalDataSource
                                var source = dataSource.appPackageName
                                if (source == null) {
                                    source = dataPoints[i].dataSource.appPackageName
                                }
                                val steps = dataPoints[i].getValue(Field.FIELD_STEPS)
                                steps?.let { steps ->
                                    val sensorEvenData: SensorEvent = getStepsData(
                                        steps,
                                        source,
                                        dataPoints[i].getEndTime(TimeUnit.MILLISECONDS)
                                    )
                                    oSensorSpecList.forEach {
                                        if (it.spec == Sensors.STEPS.sensor_name) {
                                            sensorList.add(sensorEvenData)
                                        }
                                    }

                                    Log.e(TAG, " Steps : $steps")
                                    DebugLogs.writeToFile("Step Count : $steps")
                                }

                            }
                            if (sensorList.isNotEmpty()) {
                                sensorListener.getGoogleFitData(sensorList)
                            }

                        }
                    }
                }
            }
    }


    /**
     * Reads sleep sessions from the Fit API, including any sleep {@code DataSet}s.
     */
    private fun readSleepSessions(context: Context, oSensorSpecList: ArrayList<SensorSpecs>) {

        val calendar = Calendar.getInstance()
        val now = Date()
        calendar.time = now
        val endTime = System.currentTimeMillis()
        if (AppState.session.lastSleepDataTimestamp == 1L)
            AppState.session.lastSleepDataTimestamp =
                System.currentTimeMillis() - 60000  //initialize with a timestamp of 1 minute prior to current time

        val startTime: Long = AppState.session.lastSleepDataTimestamp


        //Set fitnessOptions
        val fitnessOptionssleep = FitnessOptions.builder()
            .accessSleepSessions(FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_SLEEP_SEGMENT, FitnessOptions.ACCESS_READ)
            .build()
        val client = Fitness.getSessionsClient(
            context,
            GoogleSignIn.getAccountForExtension(context, fitnessOptionssleep)
        )

        val sessionReadRequest = SessionReadRequest.Builder()
            .read(DataType.TYPE_SLEEP_SEGMENT)
            // By default, only activity sessions are included, not sleep sessions. Specifying
            // includeSleepSessions also sets the behaviour to *exclude* activity sessions.
            .includeSleepSessions()
            .readSessionsFromAllApps()
            .enableServerQueries()
            .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        client.readSession(sessionReadRequest)
            .addOnSuccessListener { dumpSleepSessions(it, oSensorSpecList) }
            .addOnFailureListener { LampLog.e(TAG, "Unable to read sleep sessions", it) }
    }


    /**
     * Filters a response form the Sessions client to contain only sleep sessions, then writes to
     * the log.
     *
     * @param response Response from the Sessions client.
     */
    private fun dumpSleepSessions(
        response: SessionReadResponse,
        oSensorSpecList: ArrayList<SensorSpecs>
    ) {
        if (response.sessions.isNotEmpty()) {
            val endTimeList = response.sessions.map { it.getEndTime(TimeUnit.MILLISECONDS) }
            AppState.session.lastSleepDataTimestamp = Collections.max(endTimeList)
            for (session in response.sessions) {
                dumpSleepSession(session, response.getDataSet(session), oSensorSpecList)
            }
        }
    }

    private fun dumpSleepSession(
        session: Session,
        dataSets: List<DataSet>,
        oSensorSpecList: ArrayList<SensorSpecs>
    ) {
        dumpSleepDataSets(dataSets, session, oSensorSpecList)
    }


    /**
     * Calculates the duration of a session in minutes.
     *
     * @param session
     * @return The duration in minutes.
     */
    private fun calculateSessionDuration(session: Session): Long {
        val total =
            session.getEndTime(TimeUnit.MILLISECONDS) - session.getStartTime(TimeUnit.MILLISECONDS)
        return total
    }


    private fun dumpSleepDataSets(
        dataSets: List<DataSet>,
        session: Session,
        oSensorSpecList: ArrayList<SensorSpecs>
    ) {
        for (dataSet in dataSets) {
            val dataSource = dataSet.dataSource
            val source = dataSource.appPackageName
            if (dataSet.dataPoints.isEmpty()) {
                val sensorEvenData: SensorEvent =
                    getSleepData(null, source, null, calculateSessionDuration(session))
                oSensorSpecList.forEach {
                    if (it.spec == Sensors.SLEEP.sensor_name) {
                        sensorEventDataList.add(sensorEvenData)
                    }
                }

            } else {
                for (dataPoint in dataSet.dataPoints) {
                    val sleepStageOrdinal =
                        dataPoint.getValue(Field.FIELD_SLEEP_SEGMENT_TYPE).asInt()
                    val sleepStage = SLEEP_STAGES[sleepStageOrdinal]

                    val durationMillis =
                        dataPoint.getEndTime(TimeUnit.MILLISECONDS) - dataPoint.getStartTime(
                            TimeUnit.MILLISECONDS
                        )

                    val sensorEvenData: SensorEvent =
                        getSleepData(sleepStageOrdinal, source, sleepStage, durationMillis)
                    oSensorSpecList.forEach {
                        if (it.spec == Sensors.SLEEP.sensor_name) {
                            sensorEventDataList.add(sensorEvenData)
                        }
                    }
                }
            }
        }
    }

    private fun dumpDataSet(dataSet: DataSet, oSensorSpecList: ArrayList<SensorSpecs>) {
        LampLog.e(TAG, "Data returned for Data type: ${dataSet.dataType.name}")
        for (dp in dataSet.dataPoints) {
            LampLog.e(TAG, "Type: ${dp.dataType.name}")
            val dataSource = dp.originalDataSource
            val source = dataSource.appPackageName
            //Checking with Data Type
            when (dp.dataType.name) {

                "com.google.calories.expended" -> {
                    val field = dp.dataType.fields[0]
                    LampLog.e(TAG, "Calories Expended : " + dp.getValue(field))
                    val sensorEvenData: SensorEvent = getCalorieData(dp.getValue(field), source)
                    oSensorSpecList.forEach {
                        if (it.spec == Sensors.NUTRITION.sensor_name) {
                            sensorEventDataList.add(sensorEvenData)
                        }
                    }
                }
                "com.google.activity.segment" -> {
                    val field = dp.dataType.fields[0]
                    LampLog.e(TAG, "Activity Segment : " + dp.getValue(field))
                }

                "com.google.distance.delta" -> {
                    val field = dp.dataType.fields[0]
                    LampLog.e(TAG, "Distance : " + dp.getValue(field))
                    val sensorEvenData: SensorEvent = getDistanceData(dp.getValue(field), source)
                    oSensorSpecList.forEach {
                        if (it.spec == Sensors.STEPS.sensor_name) {
                            sensorEventDataList.add(sensorEvenData)
                        }
                    }
                }
                "com.google.calories.bmr" -> {
                    val field = dp.dataType.fields[0]
                    LampLog.e(TAG, "bmr : " + dp.getValue(field))
                    val sensorEvenData: SensorEvent = getBmrData(dp.getValue(field), source)
                    oSensorSpecList.forEach {
                        if (it.spec == Sensors.NUTRITION.sensor_name) {
                            sensorEventDataList.add(sensorEvenData)
                        }
                    }
                }
                "com.google.heart_rate.bpm" -> {
                    LampLog.e(TAG, "bpm")
                    val field = dp.dataType.fields[0]
                    val sensorEvenData: SensorEvent = getHeartRateData(dp.getValue(field), source)
                    oSensorSpecList.forEach {
                        if (it.spec == Sensors.HEART_RATE.sensor_name) {
                            sensorEventDataList.add(sensorEvenData)
                        }
                    }
                }
                "com.google.body.fat.percentage" -> {
                    val field = dp.dataType.fields[0]
                    LampLog.e(TAG, "Body Fat Percentage : " + dp.getValue(field))
                    val sensorEvenData: SensorEvent =
                        getBodyFatPercentage(dp.getValue(field), source)
                    oSensorSpecList.forEach {
                        if (it.spec == Sensors.NUTRITION.sensor_name) {
                            sensorEventDataList.add(sensorEvenData)
                        }
                    }
                }

                "com.google.speed" -> {
                    val field = dp.dataType.fields[0]
                    LampLog.e(TAG, "Speed : " + dp.getValue(field))
                    val sensorEvenData: SensorEvent = getSpeedData(dp.getValue(field), source)
                    oSensorSpecList.forEach {
                        if (it.spec == Sensors.STEPS.sensor_name) {
                            sensorEventDataList.add(sensorEvenData)
                        }
                    }
                }
                "com.google.hydration" -> {
                    val field = dp.dataType.fields[0]
                    LampLog.e(TAG, "Hydration : " + dp.getValue(field))
                    val sensorEvenData: SensorEvent = getHydrationData(dp.getValue(field), source)
                    oSensorSpecList.forEach {
                        if (it.spec == Sensors.NUTRITION.sensor_name) {
                            sensorEventDataList.add(sensorEvenData)
                        }
                    }
                }
                "com.google.nutrition" -> {
                    val field = dp.dataType.fields[0]
                    LampLog.e(TAG, "Nutrition : " + dp.getValue(field))
                    val sensorEvenData: SensorEvent = getNutritionData(dp.getValue(field), source)
                    oSensorSpecList.forEach {
                        if (it.spec == Sensors.NUTRITION.sensor_name) {
                            sensorEventDataList.add(sensorEvenData)
                        }
                    }

                }
                "com.google.blood_glucose" -> {
                    val field = dp.dataType.fields[0]
                    LampLog.e(TAG, "Blood Glucose : " + dp.getValue(field))
                    val sensorEvenData: SensorEvent = getBloodGlucose(dp.getValue(field), source)
                    oSensorSpecList.forEach {
                        if (it.spec == Sensors.BLOOD_GLUCOSE.sensor_name) {
                            sensorEventDataList.add(sensorEvenData)
                        }
                    }
                }
                "com.google.blood_pressure" -> {
                    val field1 = dp.dataType.fields[0]
                    val field2 = dp.dataType.fields[1]
                    val sensorEvenData: SensorEvent = getBloodPressure(
                        dp.getValue(field1).asFloat(),
                        dp.getValue(field2).asFloat(),
                        source
                    )
                    oSensorSpecList.forEach {
                        if (it.spec == Sensors.BLOOD_PRESSURE.sensor_name) {
                            sensorEventDataList.add(sensorEvenData)
                        }
                    }
                }
                "com.google.oxygen_saturation" -> {
                    val field = dp.dataType.fields[0]
                    LampLog.e(TAG, "Oxygen Saturation : " + dp.getValue(field))
                    val sensorEvenData: SensorEvent =
                        getOxygenSaturation(dp.getValue(field), source)
                    oSensorSpecList.forEach {
                        if (it.spec == Sensors.OXYGEN_SATURATION.sensor_name) {
                            sensorEventDataList.add(sensorEvenData)
                        }
                    }
                }
                "com.google.body.temperature" -> {
                    val field = dp.dataType.fields[0]
                    LampLog.e(TAG, "Body Temperature: " + dp.getValue(field))
                    val sensorEvenData: SensorEvent = getBodyTemperature(dp.getValue(field), source)
                    oSensorSpecList.forEach {
                        if (it.spec == Sensors.BODY_TEMPERATURE.sensor_name) {
                            sensorEventDataList.add(sensorEvenData)
                        }
                    }
                }

                "com.google.step_count.cadence" -> {
                    val field = dp.dataType.fields[0]
                    val sensorEvenData: SensorEvent =
                        getStepCountCadenceData(dp.getValue(field), source)
                    oSensorSpecList.forEach {
                        if (it.spec == Sensors.STEPS.sensor_name) {
                            sensorEventDataList.add(sensorEvenData)
                        }
                    }
                }
            }
        }
    }

    /**
     * Constructs a Google Fit data read request for various fitness data types.
     *
     * @return A DataReadRequest for querying fitness data.
     */
    private fun queryFitnessData(): DataReadRequest {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        val now = Date()
        calendar.time = now
        val endTime = System.currentTimeMillis()
        if (AppState.session.lastGooglefitDataTimestamp == 1L)
            AppState.session.lastGooglefitDataTimestamp =
                System.currentTimeMillis() - 60000  //initialize with a timestamp of 1 minute prior to current time

        val startTime: Long = AppState.session.lastGooglefitDataTimestamp
        AppState.session.lastGooglefitDataTimestamp = endTime
        DebugLogs.writeToFile("queryFitnessData StartTime $startTime EndTime $endTime")

        return DataReadRequest.Builder()
            .read(DataType.TYPE_STEP_COUNT_DELTA)
            .read(DataType.TYPE_DISTANCE_DELTA)
            .read(DataType.TYPE_HEART_RATE_BPM)
            .read(DataType.TYPE_MOVE_MINUTES)
            .read(DataType.TYPE_CALORIES_EXPENDED)
            .read(DataType.TYPE_ACTIVITY_SEGMENT)
            .read(DataType.TYPE_BASAL_METABOLIC_RATE)
            .read(DataType.TYPE_BODY_FAT_PERCENTAGE)
            .read(DataType.TYPE_SPEED)
            .read(DataType.TYPE_HYDRATION)
            .read(DataType.TYPE_NUTRITION)
            .read(HealthDataTypes.TYPE_BLOOD_GLUCOSE)
            .read(HealthDataTypes.TYPE_BLOOD_PRESSURE)
            .read(HealthDataTypes.TYPE_OXYGEN_SATURATION)
            .read(HealthDataTypes.TYPE_BODY_TEMPERATURE)
            .read(DataType.TYPE_STEP_COUNT_CADENCE)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .setLimit(1)
            .build()
    }

    /**
     * Creates a SensorEvent for sleep-related data.
     *
     * @param sleep The sleep-related data value.
     * @param source The source of the sleep data.
     * @param representation The representation of the sleep data.
     * @param duration The duration of the sleep event.
     * @return A SensorEvent representing sleep-related data.
     */
    //3
    private fun getSleepData(
        sleep: Int?,
        source: String?,
        representation: String?,
        duration: Long
    ): SensorEvent {
        val dimensionData =
            SleepData(
                representation,
                sleep, source, duration
            )
        return SensorEvent(
            dimensionData,
            Sensors.SLEEP.sensor_name,
            System.currentTimeMillis().toDouble()
        )
    }

    /**
     * Creates a Calorie  data.
     *
     * @param calories The calorie-related data value.
     * @param source The source of the calorie data.
     * @return A SensorEvent representing calorie-related data.
     */
    //4
    private fun getCalorieData(calories: Value, source: String?): SensorEvent {
        val dimensionData =
            NutritionData(
                "kcal",
                calories.asFloat(), "calories", source
            )
        return SensorEvent(
            dimensionData,
            Sensors.NUTRITION.sensor_name,
            System.currentTimeMillis().toDouble()
        )
    }

    /**
     * Creates a SensorEvent for steps-related data.
     *
     * @param steps The steps-related data value.
     * @param source The source of the steps data.
     * @param timeStamp The duration of the steps event.
     * @return A SensorEvent representing sleep-related data.
     */
    //5
    private fun getStepsData(steps: Value, source: Any?, timeStamp: Long): SensorEvent {
        val dimensionData =
            StepsData(
                "count", steps.asInt(), "step_count", source
            )
        return SensorEvent(dimensionData, Sensors.STEPS.sensor_name, timeStamp.toDouble())
    }

    /**
     * Creates a SensorEvent for distance-related data.
     *
     * @param distanceDelta The value representing the change in distance.
     * @param source The source of the distance data.
     * @return A SensorEvent representing distance-related data.
     */
    //6
    private fun getDistanceData(distanceDelta: Value, source: String?): SensorEvent {
        val dimensionData =
            StepsData(
                "meter",
                distanceDelta.asFloat(), "distance", source
            )
        return SensorEvent(
            dimensionData,
            Sensors.STEPS.sensor_name,
            System.currentTimeMillis().toDouble()
        )
    }

    /**
     * Creates a SensorEvent for bmr-related data.
     *
     * @param bmrCalorie The value representing the bmr calorie data
     * @param source The source of the bmr calorie data.
     * @return A SensorEvent representing distance-related data.
     */

    //8
    private fun getBmrData(bmrCalorie: Value, source: String?): SensorEvent {
        val dimensionData =
            NutritionData(
                "Kcal",
                bmrCalorie.asFloat(), "calories_bmr", source
            )
        return SensorEvent(
            dimensionData,
            Sensors.NUTRITION.sensor_name,
            System.currentTimeMillis().toDouble()
        )
    }

    /**
     * Creates a SensorEvent for heart rate-related data.
     *
     * @param heart_rate The value representing the heart rate related data.
     * @param source The source of the heart_rate data.
     * @return A SensorEvent representing heart_rate-related data.
     */
    //9
    private fun getHeartRateData(heart_rate: Value, source: String?): SensorEvent {
        val dimensionData =
            GoogleFitData(
                "bpm",
                heart_rate.asFloat(), source
            )
        return SensorEvent(
            dimensionData,
            Sensors.HEART_RATE.sensor_name,
            System.currentTimeMillis().toDouble()
        )
    }

    /**
     * Creates a SensorEvent for body_fat_percentage-related data.
     *
     * @param body_fat_percentage The value representing the change in body_fat_percentage.
     * @param source The source of the body_fat_percentage data.
     * @return A SensorEvent representing body_fat_percentage-related data.
     */
    //10
    private fun getBodyFatPercentage(body_fat_percentage: Value, source: String?): SensorEvent {
        val dimensionData =
            NutritionData(
                "Percentage",
                body_fat_percentage.asFloat(), "body_fat_percentage", source
            )
        return SensorEvent(
            dimensionData,
            Sensors.NUTRITION.sensor_name,
            System.currentTimeMillis().toDouble()
        )
    }

    /**
     * Creates a SensorEvent for speed-related data.
     *
     * @param speed The value representing the speed related data.
     * @param source The source of the speed data.
     * @return A SensorEvent representing speed-related data.
     */
    //13
    private fun getSpeedData(speed: Value, source: String?): SensorEvent {
        val dimensionData =
            StepsData(
                "meters per second",
                speed.asFloat(), "speed", source
            )
        return SensorEvent(
            dimensionData,
            Sensors.STEPS.sensor_name,
            System.currentTimeMillis().toDouble()
        )
    }

    /**
     * Creates a SensorEvent for hydration-related data.
     *
     * @param hydration The value representing the hydration related data.
     * @param source The source of the hydration data.
     * @return A SensorEvent representing hydration-related data.
     */
    //14
    private fun getHydrationData(hydration: Value, source: String?): SensorEvent {
        val dimensionData =
            NutritionData(
                "liters",
                hydration.asFloat(), "hydration", source
            )
        return SensorEvent(
            dimensionData,
            Sensors.NUTRITION.sensor_name,
            System.currentTimeMillis().toDouble()
        )
    }

    /**
     * Creates a SensorEvent for nutrition-related data.
     *
     * @param nutrition The value representing the nutrition related data.
     * @param source The source of the nutrition data.
     * @return A SensorEvent representing nutrition-related data.
     */
    //15
    private fun getNutritionData(nutrition: Value, source: String?): SensorEvent {
        val dimensionData =
            NutritionData(
                "enum",
                nutrition.asInt(), "nutrition", source
            )
        return SensorEvent(
            dimensionData,
            Sensors.NUTRITION.sensor_name,
            System.currentTimeMillis().toDouble()
        )
    }

    /**
     * Creates a SensorEvent for bloodGlucose-related data.
     *
     * @param bloodGlucose The value representing the bloodGlucose related data.
     * @param source The source of the bloodGlucose data.
     * @return A SensorEvent representing bloodGlucose-related data.
     */
    //16
    private fun getBloodGlucose(bloodGlucose: Value, source: String?): SensorEvent {
        val dimensionData =
            GoogleFitData(
                "mmol/L",
                bloodGlucose.asFloat(), source
            )
        return SensorEvent(
            dimensionData,
            Sensors.BLOOD_GLUCOSE.sensor_name,
            System.currentTimeMillis().toDouble()
        )
    }

    /**
     * Creates a SensorEvent for blood pressure-related data.
     *
     * @param bpSystolic The systolic blood pressure value.
     * @param bpDiastolic The diastolic blood pressure value.
     * @param source The source of the blood pressure data.
     * @return A SensorEvent representing blood pressure-related data.
     */
    //17
    private fun getBloodPressure(
        bpSystolic: Float,
        bpDiastolic: Float,
        source: String?
    ): SensorEvent {
        val dimensionData =
            BloodPressure(
                BloodPressureData(bpSystolic, "mmHg", source),
                BloodPressureData(bpDiastolic, "mmHg", source),

                )
        return SensorEvent(
            dimensionData,
            Sensors.BLOOD_PRESSURE.sensor_name,
            System.currentTimeMillis().toDouble()
        )
    }

    /**
     * Creates a SensorEvent for oxygenSaturation-related data.
     *
     * @param oxygenSaturation The value representing the oxygenSaturation related data.
     * @param source The source of the oxygenSaturation data.
     * @return A SensorEvent representing oxygenSaturation-related data.
     */
    //18
    private fun getOxygenSaturation(oxygenSaturation: Value, source: String?): SensorEvent {
        val dimensionData =
            GoogleFitData(

                "percentage",
                oxygenSaturation.asFloat(), source
            )
        return SensorEvent(
            dimensionData,
            Sensors.OXYGEN_SATURATION.sensor_name,
            System.currentTimeMillis().toDouble()
        )
    }

    /**
     * Creates a SensorEvent for bodyTemperature-related data.
     *
     * @param bodyTemperature The value representing the bodyTemperature related data.
     * @param source The source of the bodyTemperature data.
     * @return A SensorEvent representing bodyTemperature-related data.
     */
    //19
    private fun getBodyTemperature(bodyTemperature: Value, source: String?): SensorEvent {
        val dimensionData =
            GoogleFitData(
                "celsius",
                bodyTemperature.asFloat(), source
            )
        return SensorEvent(
            dimensionData,
            Sensors.BODY_TEMPERATURE.sensor_name,
            System.currentTimeMillis().toDouble()
        )
    }

    /**
     * Creates a SensorEvent for step count-related data.
     *
     * @param count The value representing the step_count related data.
     * @param source The source of the step_count data.
     * @return A SensorEvent representing step_count-related data.
     */
    //25
    private fun getStepCountCadenceData(count: Value, source: String?): SensorEvent {
        val dimensionData =
            StepsData(
                "steps/minute",
                count.asFloat(), "cadence", source
            )
        return SensorEvent(
            dimensionData,
            Sensors.STEPS.sensor_name,
            System.currentTimeMillis().toDouble()
        )
    }

}