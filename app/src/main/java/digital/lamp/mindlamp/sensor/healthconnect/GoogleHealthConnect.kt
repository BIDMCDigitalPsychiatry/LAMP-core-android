package digital.lamp.mindlamp.sensor.healthconnect

import android.content.Context
import android.util.Log
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.BloodGlucoseRecord
import androidx.health.connect.client.records.BloodPressureRecord
import androidx.health.connect.client.records.BodyFatRecord
import androidx.health.connect.client.records.BodyTemperatureRecord
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.HydrationRecord
import androidx.health.connect.client.records.NutritionRecord
import androidx.health.connect.client.records.OxygenSaturationRecord
import androidx.health.connect.client.records.Record
import androidx.health.connect.client.records.RespiratoryRateRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.SpeedRecord
import androidx.health.connect.client.records.StepsCadenceRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.lifecycle.MutableLiveData
import digital.lamp.lamp_kotlin.lamp_core.models.BloodPressure
import digital.lamp.lamp_kotlin.lamp_core.models.BloodPressureData
import digital.lamp.lamp_kotlin.lamp_core.models.GoogleHealthConnectData
import digital.lamp.lamp_kotlin.lamp_core.models.NutritionData
import digital.lamp.lamp_kotlin.lamp_core.models.SensorEvent
import digital.lamp.lamp_kotlin.lamp_core.models.SleepData
import digital.lamp.lamp_kotlin.lamp_core.models.StepsData
import digital.lamp.mindlamp.R
import digital.lamp.mindlamp.appstate.AppState
import digital.lamp.mindlamp.database.entity.SensorSpecs
import digital.lamp.mindlamp.sensor.SensorListener
import digital.lamp.mindlamp.sensor.healthconnect.model.SleepSessionData
import digital.lamp.mindlamp.utils.DebugLogs
import digital.lamp.mindlamp.utils.LampLog
import digital.lamp.mindlamp.utils.Sensors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.Calendar
import java.util.Collections
import java.util.Date

class GoogleHealthConnect(
    private val context: Context,
    private var sensorListener: SensorListener? = null,
    private var oSensorSpecList: java.util.ArrayList<SensorSpecs>? = null
) {

    companion object {
        private val TAG = GoogleHealthConnect::class.java.simpleName
    }

    private var sensorEventDataList: java.util.ArrayList<SensorEvent> = arrayListOf()
    private val healthConnectClient by lazy { HealthConnectClient.getOrCreate(context) }
    private val sdkAvailability = MutableLiveData<Int>()
    val SLEEP_STAGES = arrayOf(
        context.getString(R.string.unknown),
        context.getString(R.string.awake_during_sleep),
        context.getString(R.string.sleep),
        context.getString(R.string.out_of_bed),
        context.getString(R.string.light_sleep),
        context.getString(R.string.deep_sleep),
        context.getString(R.string.rem_sleep)
    )

    init {
        CoroutineScope(Dispatchers.IO).launch {
            Thread.sleep(5000)
            if (oSensorSpecList?.isNotEmpty() == true) {
                readOtherSensorDataRecords()
                readSleepSessions()
                readStepCount()
                readBloodSugar()
                readRespiratoryRate()
                sensorListener?.getGoogleHealthConnect(sensorEventDataList)

            }
        }
    }

    private suspend fun readRespiratoryRate() {
        try {
            if (AppState.session.lastRespiratoryTimestamp == 1L)
                AppState.session.lastRespiratoryTimestamp = System.currentTimeMillis() - 60000

            val timeRangeFilter = getTimeRangeFilter(AppState.session.lastRespiratoryTimestamp)
            val respiratoryRecord = readData<RespiratoryRateRecord>(timeRangeFilter)
            if (respiratoryRecord.isNotEmpty()) {
                val endTimeList = respiratoryRecord.map { it.time.toEpochMilli() }
                AppState.session.lastRespiratoryTimestamp = Collections.max(endTimeList) + 1
                respiratoryRecord.forEach { records ->
                    val sensorEvenData: SensorEvent = getRespiratoryRate(
                        records.rate,
                        records.metadata.dataOrigin.packageName,
                        records.time.toEpochMilli().toDouble()
                    )
                    sensorEventDataList.add(sensorEvenData)
                }
            }

        } catch (e: Exception) {
            LampLog.e(e.message)

        }
    }

    private suspend fun readBloodSugar() {
        try {
            if (AppState.session.lastGlucoseTimestamp == 1L)
                AppState.session.lastGlucoseTimestamp = System.currentTimeMillis() - 60000
            val startTime: Long = AppState.session.lastGlucoseTimestamp
            val endTime = System.currentTimeMillis()
            val sensorList: ArrayList<SensorEvent> = arrayListOf()
            val request = ReadRecordsRequest(
                recordType = BloodGlucoseRecord::class,
                timeRangeFilter = TimeRangeFilter.between(Instant.ofEpochMilli(startTime), Instant.ofEpochMilli(endTime))
            )
            val response = healthConnectClient.readRecords(request)

            if (response.records.isNotEmpty()) {
                val endTimeList =
                    response.records.map { it.time.toEpochMilli() }
                AppState.session.lastGlucoseTimestamp = Collections.max(endTimeList) + 1
                response.records.forEachIndexed { index, glucoseLevel ->
                    val sensorEvenData: SensorEvent = getBloodGlucose(
                        glucoseLevel.level.inMillimolesPerLiter,
                        glucoseLevel.metadata.dataOrigin.packageName,
                        glucoseLevel.time.toEpochMilli().toDouble()
                    )
                    sensorList.add(sensorEvenData)
                }
            }

            if (sensorList.isNotEmpty()) {
                sensorListener?.getGoogleHealthConnect(sensorList)
            }
        } catch (e: Exception) {
            Log.e("exception::", "${e.message}")
        }
    }

    suspend fun readStepCount() {
        try {
            if (AppState.session.lastStepDataTimestamp == 1L)
                AppState.session.lastStepDataTimestamp = System.currentTimeMillis() - 60000
            val startTime: Long = AppState.session.lastStepDataTimestamp
            val endTime = System.currentTimeMillis()
            val sensorList: ArrayList<SensorEvent> = arrayListOf()
            val response = readData<StepsRecord>(
                TimeRangeFilter.between(
                    Instant.ofEpochMilli(startTime),
                    Instant.ofEpochMilli(endTime)
                )
            )

            if (response.isNotEmpty()) {
                val endTimeList =
                    response.map { it.endTime.toEpochMilli() }
                AppState.session.lastStepDataTimestamp =
                    Collections.max(endTimeList) + 1
                response.forEachIndexed { index, stepsRecord ->
                    val sensorEvenData: SensorEvent = getStepsData(
                        stepsRecord.count,
                        stepsRecord.metadata.dataOrigin.packageName,
                        stepsRecord.endTime.toEpochMilli()
                    )
                    sensorList.add(sensorEvenData)
                }
            }

            if (sensorList.isNotEmpty()) {
                sensorListener?.getGoogleHealthConnect(sensorList)
            }
        } catch (e: Exception) {
            Log.e("exception::", "${e.message}")
        }
    }

    suspend fun readSleepSessions() {
        try {
            val calendar = Calendar.getInstance()
            val now = Date()
            calendar.time = now
            val endTime = System.currentTimeMillis()
            if (AppState.session.lastSleepDataTimestamp == 1L)
                AppState.session.lastSleepDataTimestamp =
                    System.currentTimeMillis() - 60000  //initialize with a timestamp of 1 minute prior to current time

            val startTime: Long = AppState.session.lastSleepDataTimestamp


            val sessions = mutableListOf<SleepSessionData>()

            val sleepSessions = readData<SleepSessionRecord>(
                TimeRangeFilter.between(
                    Instant.ofEpochMilli(startTime),
                    Instant.ofEpochMilli(endTime)
                )
            )
            if (sleepSessions.isNotEmpty()) {
                val endTimeList = sleepSessions.map { it.endTime.toEpochMilli() }
                AppState.session.lastSleepDataTimestamp = Collections.max(endTimeList) + 1
                sleepSessions.forEach { session ->
                    val sessionTimeFilter =
                        TimeRangeFilter.between(session.startTime, session.endTime)
                    val durationAggregateRequest = AggregateRequest(
                        metrics = setOf(SleepSessionRecord.SLEEP_DURATION_TOTAL),
                        timeRangeFilter = sessionTimeFilter
                    )

                    val source = session.metadata.dataOrigin.packageName
                    if (session.stages.isNotEmpty()) {
                        session.stages.forEach { sleepSessions ->
                            val sleepStage = SLEEP_STAGES[sleepSessions.stage]
                            val sensorEvenData: SensorEvent =
                                getSleepData(
                                    sleepSessions.stage,
                                    source,
                                    sleepStage,
                                    calculateSessionDuration(session),
                                    sleepSessions.endTime.toEpochMilli().toDouble()
                                )
                            sensorEventDataList.add(sensorEvenData)

                        }
                    } else {
                        val sensorEvenData: SensorEvent =
                            getSleepData(
                                null,
                                source,
                                null,
                                calculateSessionDuration(session),
                                session.endTime.toEpochMilli().toDouble()
                            )
                        sensorEventDataList.add(sensorEvenData)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("Exception in time::", "${e.message}")
        }

    }

    suspend fun readOtherSensorDataRecords() {
        try {

            oSensorSpecList?.forEach { sensorSpecs ->
                if (sensorSpecs.spec == Sensors.NUTRITION.sensor_name) {
                    if (AppState.session.lastTotalCaloriesBurnedDataTimestamp == 1L)
                        AppState.session.lastTotalCaloriesBurnedDataTimestamp =
                            System.currentTimeMillis() - 60000

                    val timeRangeFilter =
                        getTimeRangeFilter(AppState.session.lastTotalCaloriesBurnedDataTimestamp)
                    val calorieData = readData<TotalCaloriesBurnedRecord>(timeRangeFilter)
                    if (calorieData.isNotEmpty()) {
                        val endTimeList = calorieData.map { it.endTime.toEpochMilli() }
                        AppState.session.lastTotalCaloriesBurnedDataTimestamp =
                            Collections.max(endTimeList) + 1
                        calorieData.forEach {
                            val sensorEvenData: SensorEvent =
                                getCalorieData(
                                    it.energy.inKilocalories,
                                    it.metadata.dataOrigin.packageName,
                                    it.endTime.toEpochMilli().toDouble()
                                )
                            sensorEventDataList.add(sensorEvenData)
                        }
                    }

                    if (AppState.session.lastBodyFatDataTimestamp == 1L)
                        AppState.session.lastBodyFatDataTimestamp =
                            System.currentTimeMillis() - 60000

                    val timeRangeFilterBodyFat =
                        getTimeRangeFilter(AppState.session.lastBodyFatDataTimestamp)
                    val bodyFatData = readData<BodyFatRecord>(timeRangeFilterBodyFat)
                    if (bodyFatData.isNotEmpty()) {
                        val endTimeList = bodyFatData.map { it.time.toEpochMilli() }
                        AppState.session.lastBodyFatDataTimestamp = Collections.max(endTimeList) + 1
                        bodyFatData.forEach { bodyFatRecord ->
                            val sensorEvenData: SensorEvent =
                                getBodyFatPercentage(
                                    bodyFatRecord.percentage.value,
                                    bodyFatRecord.metadata.dataOrigin.packageName,
                                    bodyFatRecord.time.toEpochMilli().toDouble()
                                )
                            sensorEventDataList.add(sensorEvenData)
                        }
                    }

                    if (AppState.session.lastHydrationTimestamp == 1L)
                        AppState.session.lastHydrationTimestamp = System.currentTimeMillis() - 60000


                    val timeRangeFilterHydration =
                        getTimeRangeFilter(AppState.session.lastHydrationTimestamp)
                    val hydrationRecord = readData<HydrationRecord>(timeRangeFilterHydration)
                    if (hydrationRecord.isNotEmpty()) {
                        val endTimeList = hydrationRecord.map { it.endTime.toEpochMilli() }
                        AppState.session.lastHydrationTimestamp = Collections.max(endTimeList) + 1
                        hydrationRecord.forEach { hydrationRecord ->
                            val sensorEvenData: SensorEvent = getHydrationData(
                                hydrationRecord.volume.inLiters,
                                hydrationRecord.metadata.dataOrigin.packageName,
                                hydrationRecord.endTime.toEpochMilli().toDouble()
                            )
                            sensorEventDataList.add(sensorEvenData)
                        }
                    }
                    if (AppState.session.lastNutritionTimestamp == 1L)
                        AppState.session.lastNutritionTimestamp = System.currentTimeMillis() - 60000


                    val timeRangeFilterNutrition =
                        getTimeRangeFilter(AppState.session.lastNutritionTimestamp)
                    val nutritionDataRecord = readData<NutritionRecord>(timeRangeFilterNutrition)
                    if (nutritionDataRecord.isNotEmpty()) {
                        val endTimeList = nutritionDataRecord.map { it.endTime.toEpochMilli() }
                        AppState.session.lastNutritionTimestamp = Collections.max(endTimeList) + 1
                        nutritionDataRecord.forEach { nutritionRecord ->
                            val sensorEvenData: SensorEvent = getNutritionData(
                                nutritionRecord.name ?: "",
                                nutritionRecord.metadata.dataOrigin.packageName,
                                nutritionRecord.endTime.toEpochMilli().toDouble()
                            )
                            sensorEventDataList.add(sensorEvenData)
                        }
                    }

                }

                if (sensorSpecs.spec == Sensors.STEPS.sensor_name) {
                    if (AppState.session.lastDistanceDataTimestamp == 1L)
                        AppState.session.lastDistanceDataTimestamp =
                            System.currentTimeMillis() - 60000

                    val timeRangeFilter =
                        getTimeRangeFilter(AppState.session.lastDistanceDataTimestamp)
                    val readDistanceDataRecords = readData<DistanceRecord>(timeRangeFilter)
                    if (readDistanceDataRecords.isNotEmpty()) {
                        val endTimeList = readDistanceDataRecords.map { it.endTime.toEpochMilli() }
                        AppState.session.lastDistanceDataTimestamp =
                            Collections.max(endTimeList) + 1
                        readDistanceDataRecords.forEach { distanceRecord ->
                            val sensorEvenData: SensorEvent = getDistanceData(
                                distanceRecord.distance.inMeters,
                                distanceRecord.metadata.dataOrigin.packageName,
                                distanceRecord.endTime.toEpochMilli().toDouble()
                            )
                            sensorEventDataList.add(sensorEvenData)
                        }
                    }

                    if (AppState.session.lastSpeedDataTimestamp == 1L)
                        AppState.session.lastSpeedDataTimestamp = System.currentTimeMillis() - 60000


                    val timeRangeFilterSpeed =
                        getTimeRangeFilter(AppState.session.lastSpeedDataTimestamp)
                    val readSpeedRecord = readData<SpeedRecord>(timeRangeFilterSpeed)
                    if (readSpeedRecord.isNotEmpty()) {
                        val endTimeList = readSpeedRecord.map { it.endTime.toEpochMilli() }
                        AppState.session.lastSpeedDataTimestamp = Collections.max(endTimeList) + 1
                        readSpeedRecord.forEach { speedRecord ->
                            speedRecord.samples.forEach { sample ->
                                val sensorEvenData: SensorEvent = getSpeedData(
                                    sample.speed.inMetersPerSecond,
                                    speedRecord.metadata.dataOrigin.packageName,
                                    sample.time.toEpochMilli().toDouble()
                                )
                                sensorEventDataList.add(sensorEvenData)
                            }
                        }
                    }

                    if (AppState.session.lastStepsCadenceTimestamp == 1L)
                        AppState.session.lastStepsCadenceTimestamp =
                            System.currentTimeMillis() - 60000


                    val timeRangeFilterCadence =
                        getTimeRangeFilter(AppState.session.lastStepsCadenceTimestamp)
                    val stepsCadenceRecord = readData<StepsCadenceRecord>(timeRangeFilterCadence)
                    if (stepsCadenceRecord.isNotEmpty()) {
                        val endTimeList = stepsCadenceRecord.map { it.endTime.toEpochMilli() }
                        AppState.session.lastStepsCadenceTimestamp =
                            Collections.max(endTimeList) + 1
                        stepsCadenceRecord.forEach { stepsCadenceRecord ->
                            stepsCadenceRecord.samples.forEach { sample ->
                                val sensorEvenData: SensorEvent =
                                    getStepCountCadenceData(
                                        sample.rate,
                                        stepsCadenceRecord.metadata.dataOrigin.packageName,
                                        sample.time.toEpochMilli().toDouble()
                                    )
                                sensorEventDataList.add(sensorEvenData)
                            }

                        }
                    }
                }

               /* if (sensorSpecs.spec == Sensors.HEART_RATE.sensor_name) {
                    if (AppState.session.lastHeartRateTimestamp == 1L)
                        AppState.session.lastHeartRateTimestamp = System.currentTimeMillis() - 60000

                    val timeRangeFilter =
                        getTimeRangeFilter(AppState.session.lastHeartRateTimestamp)
                    val heartRateRecord = readData<HeartRateRecord>(timeRangeFilter)
                    if (heartRateRecord.isNotEmpty()) {
                        val endTimeList = heartRateRecord.map { it.endTime.toEpochMilli() }
                        AppState.session.lastHeartRateTimestamp = Collections.max(endTimeList) + 1
                        heartRateRecord.forEach { heartRateRecord ->
                            heartRateRecord.samples.forEach { sample ->
                                val sensorEvenData: SensorEvent = getHeartRateData(
                                    sample.beatsPerMinute,
                                    heartRateRecord.metadata.dataOrigin.packageName,
                                    sample.time.toEpochMilli().toDouble()
                                )
                                sensorEventDataList.add(sensorEvenData)
                            }

                        }
                    }
                }*/
                if (sensorSpecs.spec == Sensors.BLOOD_GLUCOSE.sensor_name) {
                    if (AppState.session.lastGlucoseTimestamp == 1L)
                        AppState.session.lastGlucoseTimestamp = System.currentTimeMillis() - 60000


                    val timeRangeFilter = getTimeRangeFilter(AppState.session.lastGlucoseTimestamp)
                    val glucoseRecord = readData<BloodGlucoseRecord>(timeRangeFilter)
                    if (glucoseRecord.isNotEmpty()) {
                        val endTimeList = glucoseRecord.map { it.time.toEpochMilli() }
                        AppState.session.lastGlucoseTimestamp = Collections.max(endTimeList) + 1
                        glucoseRecord.forEach { records ->
                            val sensorEvenData: SensorEvent = getBloodGlucose(
                                records.level.inMillimolesPerLiter,
                                records.metadata.dataOrigin.packageName,
                                records.time.toEpochMilli().toDouble()
                            )
                            sensorEventDataList.add(sensorEvenData)
                        }
                    }
                }

                if (sensorSpecs.spec == Sensors.RESPIRATORY_RATE.sensor_name) {
                    if (AppState.session.lastRespiratoryTimestamp == 1L)
                        AppState.session.lastRespiratoryTimestamp =
                            System.currentTimeMillis() - 60000


                    val timeRangeFilter =
                        getTimeRangeFilter(AppState.session.lastRespiratoryTimestamp)
                    val respiratoryRecord = readData<RespiratoryRateRecord>(timeRangeFilter)
                    if (respiratoryRecord.isNotEmpty()) {
                        val endTimeList = respiratoryRecord.map { it.time.toEpochMilli() }
                        AppState.session.lastRespiratoryTimestamp = Collections.max(endTimeList) + 1
                        respiratoryRecord.forEach { records ->
                            val sensorEvenData: SensorEvent = getRespiratoryRate(
                                records.rate,
                                records.metadata.dataOrigin.packageName,
                                records.time.toEpochMilli().toDouble()
                            )
                            sensorEventDataList.add(sensorEvenData)
                        }
                    }
                }

                if (sensorSpecs.spec == Sensors.BLOOD_PRESSURE.sensor_name) {
                    if (AppState.session.lastPressureTimestamp == 1L)
                        AppState.session.lastPressureTimestamp = System.currentTimeMillis() - 60000


                    val timeRangeFilter = getTimeRangeFilter(AppState.session.lastPressureTimestamp)
                    val bloodPressureRecord = readData<BloodPressureRecord>(timeRangeFilter)
                    if (bloodPressureRecord.isNotEmpty()) {
                        val endTimeList = bloodPressureRecord.map { it.time.toEpochMilli() }
                        AppState.session.lastPressureTimestamp = Collections.max(endTimeList) + 1
                        bloodPressureRecord.forEach { bloodPressureRecord ->
                            val sensorEvenData: SensorEvent = getBloodPressure(
                                bloodPressureRecord.systolic.inMillimetersOfMercury,
                                bloodPressureRecord.diastolic.inMillimetersOfMercury,
                                bloodPressureRecord.metadata.dataOrigin.packageName,
                                bloodPressureRecord.time.toEpochMilli().toDouble()
                            )
                            sensorEventDataList.add(sensorEvenData)
                        }
                    }
                }
                if (sensorSpecs.spec == Sensors.OXYGEN_SATURATION.sensor_name) {
                    if (AppState.session.lastOxygenSaturationTimestamp == 1L)
                        AppState.session.lastOxygenSaturationTimestamp =
                            System.currentTimeMillis() - 60000


                    val timeRangeFilter =
                        getTimeRangeFilter(AppState.session.lastOxygenSaturationTimestamp)
                    val oxygenSaturationRecord = readData<OxygenSaturationRecord>(timeRangeFilter)
                    if (oxygenSaturationRecord.isNotEmpty()) {
                        val endTimeList = oxygenSaturationRecord.map { it.time.toEpochMilli() }
                        AppState.session.lastOxygenSaturationTimestamp =
                            Collections.max(endTimeList) + 1
                        oxygenSaturationRecord.forEach { oxygenSaturationRecord ->
                            val sensorEvenData: SensorEvent =
                                getOxygenSaturation(
                                    oxygenSaturationRecord.percentage.value,
                                    oxygenSaturationRecord.metadata.dataOrigin.packageName,
                                    oxygenSaturationRecord.time.toEpochMilli().toDouble()
                                )
                            sensorEventDataList.add(sensorEvenData)
                        }
                    }
                }

                if (sensorSpecs.spec == Sensors.BODY_TEMPERATURE.sensor_name) {
                    if (AppState.session.lastBodyTemperatureTimestamp == 1L)
                        AppState.session.lastBodyTemperatureTimestamp =
                            System.currentTimeMillis() - 60000


                    val timeRangeFilter =
                        getTimeRangeFilter(AppState.session.lastBodyTemperatureTimestamp)
                    val bodyTemperatureRecord = readData<BodyTemperatureRecord>(timeRangeFilter)
                    if (bodyTemperatureRecord.isNotEmpty()) {
                        val endTimeList = bodyTemperatureRecord.map { it.time.toEpochMilli() }
                        AppState.session.lastBodyTemperatureTimestamp =
                            Collections.max(endTimeList) + 1
                        bodyTemperatureRecord.forEach { bodyTemperatureRecord ->
                            val sensorEvenData: SensorEvent = getBodyTemperature(
                                bodyTemperatureRecord.temperature.inCelsius,
                                bodyTemperatureRecord.metadata.dataOrigin.packageName,
                                bodyTemperatureRecord.time.toEpochMilli().toDouble()
                            )
                            sensorEventDataList.add(sensorEvenData)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("Exception ::", "${e.message}")
            DebugLogs.writeToFile("Exception ::" + "${e.message}")
        }

    }

    private fun getTimeRangeFilter(startTime: Long): TimeRangeFilter {
        try {
            return TimeRangeFilter.between(
                Instant.ofEpochMilli(startTime),
                Instant.ofEpochMilli(System.currentTimeMillis())
            )
        } catch (e: Exception) {
            LampLog.e(e.message)
            DebugLogs.writeToFile("Exception in timerange filter ${e.message}")
            return TimeRangeFilter.between(
                Instant.ofEpochMilli(System.currentTimeMillis() - 1),
                Instant.ofEpochMilli(System.currentTimeMillis())
            )
        }
    }

    /**
     * Creates a SensorEvent for bodyTemperature-related data.
     *
     * @param bodyTemperature The value representing the bodyTemperature related data.
     * @param source The source of the bodyTemperature data.
     * @return A SensorEvent representing bodyTemperature-related data.
     */
    //19
    private fun getBodyTemperature(
        bodyTemperature: Double,
        source: String?,
        timeStamp: Double
    ): SensorEvent {
        val dimensionData =
            GoogleHealthConnectData(
                "celsius",
                bodyTemperature.toFloat(), source
            )
        return SensorEvent(
            dimensionData,
            Sensors.BODY_TEMPERATURE.sensor_name,
            timeStamp
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
    private fun getOxygenSaturation(
        oxygenSaturation: Double,
        source: String?,
        timeStamp: Double
    ): SensorEvent {
        val dimensionData =
            GoogleHealthConnectData(

                "percentage",
                oxygenSaturation.toFloat(), source
            )
        return SensorEvent(
            dimensionData,
            Sensors.OXYGEN_SATURATION.sensor_name,
            timeStamp
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
        bpSystolic: Double,
        bpDiastolic: Double,
        source: String?,
        timeStamp: Double
    ): SensorEvent {
        val dimensionData =
            BloodPressure(
                BloodPressureData(bpSystolic.toFloat(), "mmHg", source),
                BloodPressureData(bpDiastolic.toFloat(), "mmHg", source),

                )
        return SensorEvent(
            dimensionData,
            Sensors.BLOOD_PRESSURE.sensor_name,
            timeStamp
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
    private fun getBloodGlucose(
        bloodGlucose: Double,
        source: String?,
        timeStamp: Double
    ): SensorEvent {
        val dimensionData =
            GoogleHealthConnectData(
                "mmol/L",
                bloodGlucose.toFloat(), source
            )
        return SensorEvent(
            dimensionData,
            Sensors.BLOOD_GLUCOSE.sensor_name,
            timeStamp
        )
    }

    private fun getRespiratoryRate(
        respiratoryRate: Double,
        source: String?,
        timeStamp: Double
    ): SensorEvent {
        val dimensionData =
            GoogleHealthConnectData(
                "rpm",
                respiratoryRate.toLong(), source
            )
        return SensorEvent(
            dimensionData,
            Sensors.RESPIRATORY_RATE.sensor_name,
            timeStamp
        )
    }

   /* *//**
     * Creates a SensorEvent for heart rate-related data.
     *
     * @param heart_rate The value representing the heart rate related data.
     * @param source The source of the heart_rate data.
     * @return A SensorEvent representing heart_rate-related data.
     *//*
    //9
    private fun getHeartRateData(
        heart_rate: Long,
        source: String?,
        timeStamp: Double
    ): SensorEvent {
        val dimensionData =
            GoogleHealthConnectData(
                "bpm",
                heart_rate, source
            )
        return SensorEvent(
            dimensionData,
            Sensors.HEART_RATE.sensor_name,
            timeStamp
        )
    }
*/
    /**
     * Creates a SensorEvent for step count-related data.
     *
     * @param count The value representing the step_count related data.
     * @param source The source of the step_count data.
     * @return A SensorEvent representing step_count-related data.
     */
    //25
    private fun getStepCountCadenceData(
        count: Double,
        source: String?,
        timeStamp: Double
    ): SensorEvent {
        val dimensionData =
            StepsData(
                "steps/minute",
                count.toLong(), "cadence", source
            )
        return SensorEvent(
            dimensionData,
            Sensors.STEPS.sensor_name,
            timeStamp
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
    private fun getSpeedData(speed: Double, source: String?, timeStamp: Double): SensorEvent {
        val dimensionData =
            StepsData(
                "meters per second",
                speed.toFloat(), "speed", source
            )
        return SensorEvent(
            dimensionData,
            Sensors.STEPS.sensor_name,
            timeStamp
        )
    }

    /**
     * Creates a SensorEvent for distance-related data.
     *
     * @param distanceDelta The value representing the change in distance.
     * @param source The source of the distance data.
     * @return A SensorEvent representing distance-related data.
     */
    //6
    private fun getDistanceData(
        distanceDelta: Double,
        source: String?,
        timeStamp: Double
    ): SensorEvent {
        val dimensionData =
            StepsData(
                "meter",
                distanceDelta.toFloat(), "distance", source
            )
        return SensorEvent(
            dimensionData,
            Sensors.STEPS.sensor_name,
            timeStamp
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
    private fun getNutritionData(nutrition: Any, source: String?, timeStamp: Double): SensorEvent {
        val dimensionData =
            NutritionData(
                "enum",
                nutrition, "nutrition", source
            )
        return SensorEvent(
            dimensionData,
            Sensors.NUTRITION.sensor_name,
            timeStamp
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
    private fun getHydrationData(
        hydration: Double,
        source: String?,
        timeStamp: Double
    ): SensorEvent {
        val dimensionData =
            NutritionData(
                "liters",
                hydration.toFloat(), "hydration", source
            )
        return SensorEvent(
            dimensionData,
            Sensors.NUTRITION.sensor_name,
            timeStamp
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
    private fun getBodyFatPercentage(
        body_fat_percentage: Double,
        source: String?,
        timeStamp: Double
    ): SensorEvent {
        val dimensionData =
            NutritionData(
                "Percentage",
                body_fat_percentage.toFloat(), "body_fat_percentage", source
            )
        return SensorEvent(
            dimensionData,
            Sensors.NUTRITION.sensor_name,
            timeStamp
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
    private fun getCalorieData(calories: Double, source: String?, timeStamp: Double): SensorEvent {
        val dimensionData =
            NutritionData(
                "kcal",
                calories.toFloat(), "calories", source
            )
        return SensorEvent(
            dimensionData,
            Sensors.NUTRITION.sensor_name,
            timeStamp
        )
    }


    /**
     * Calculates the duration of a session in minutes.
     *
     * @param session
     * @return The duration in minutes.
     */
    private fun calculateSessionDuration(session: SleepSessionRecord): Long {
        val total =
            session.endTime.toEpochMilli() - session.startTime.toEpochMilli()
        return total
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
        duration: Long,
        timeStamp: Double
    ): SensorEvent {
        val dimensionData =
            SleepData(
                representation,
                sleep, source, duration
            )
        return SensorEvent(
            dimensionData,
            Sensors.SLEEP.sensor_name,
            timeStamp
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
    private fun getStepsData(steps: Long, source: Any?, timeStamp: Long): SensorEvent {
        val dimensionData =
            StepsData(
                "count", steps, "step_count", source
            )
        return SensorEvent(dimensionData, Sensors.STEPS.sensor_name, timeStamp.toDouble())
    }

    /**
     * Convenience function to reuse code for reading data.
     */
    private suspend inline fun <reified T : Record> readData(
        timeRangeFilter: TimeRangeFilter,

        ): List<T> {
        val request = ReadRecordsRequest(
            recordType = T::class,
            timeRangeFilter = timeRangeFilter
        )
        return healthConnectClient.readRecords(request).records
    }

}