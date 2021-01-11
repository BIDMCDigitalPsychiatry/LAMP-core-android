package digital.lamp.mindlamp.sensor

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.*
import com.google.android.gms.fitness.request.DataReadRequest
import digital.lamp.mindlamp.appstate.AppState
import digital.lamp.mindlamp.utils.LampLog
import digital.lamp.lamp_core.models.DimensionData
import digital.lamp.lamp_core.models.SensorEvent
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class GoogleFit constructor(sensorListener: SensorListener, context: Context) {

    companion object {
        private val TAG = GoogleFit::class.java.simpleName
    }

    private var sensorEventDataList: ArrayList<SensorEvent> = arrayListOf()

    init {

        //Fitness options
        val fitnessOptions: FitnessOptions by lazy {
            FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_DISTANCE_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_WEIGHT, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_HEIGHT, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_MOVE_MINUTES, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_CALORIES_EXPENDED, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_ACTIVITY_SEGMENT, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_BASAL_METABOLIC_RATE, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_BODY_FAT_PERCENTAGE, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_CYCLING_WHEEL_RPM, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_CYCLING_PEDALING_CUMULATIVE, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_SPEED, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_HYDRATION, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_NUTRITION, FitnessOptions.ACCESS_READ)
                .addDataType(HealthDataTypes.TYPE_BLOOD_GLUCOSE, FitnessOptions.ACCESS_READ)
                .addDataType(HealthDataTypes.TYPE_BLOOD_PRESSURE, FitnessOptions.ACCESS_READ)
                .addDataType(HealthDataTypes.TYPE_OXYGEN_SATURATION, FitnessOptions.ACCESS_READ)
                .addDataType(HealthDataTypes.TYPE_BODY_TEMPERATURE, FitnessOptions.ACCESS_READ)
                .addDataType(HealthDataTypes.TYPE_MENSTRUATION, FitnessOptions.ACCESS_READ)
                .addDataType(HealthDataTypes.TYPE_VAGINAL_SPOTTING, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_CYCLING_PEDALING_CADENCE, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_HEART_POINTS, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_POWER_SAMPLE, FitnessOptions.ACCESS_READ)
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
                LampLog.e(TAG, "Success : ${dataReadResponse.dataSets.size}")
                if (dataReadResponse.dataSets.isNotEmpty()) {
                    dataReadResponse.dataSets.forEach {
                        dumpDataSet(it)
                    }
                }
                sensorListener.getGoogleFitData(sensorEventDataList)
            }
            .addOnFailureListener { exception ->
                LampLog.e(TAG, "Problem in reading data : $exception")
            }
    }

    private fun dumpDataSet(dataSet: DataSet) {
        LampLog.e(TAG, "Data returned for Data type: ${dataSet.dataType.name}")
        for (dp in dataSet.dataPoints) {
            LampLog.e(TAG, "Type: ${dp.dataType.name}")

            //Checking with Data Type
            when (dp.dataType.name) {
                "com.google.weight" -> {
                    val field = dp.dataType.fields[0]
                    LampLog.e(TAG, "Weight : " + dp.getValue(field))
                    val sensorEvenData: SensorEvent = getWeightData(dp.getValue(field))
                    sensorEventDataList.add(sensorEvenData)
                }
                "com.google.height" -> {
                    val field = dp.dataType.fields[0]
                    LampLog.e(TAG, "Weight : " + dp.getValue(field))
                    val sensorEvenData: SensorEvent = getHeightData(dp.getValue(field))
                    sensorEventDataList.add(sensorEvenData)
                }
                "com.google.active_minutes" -> {
                    val field = dp.dataType.fields[0]
                    LampLog.e(TAG, "Active Minutes : " + dp.getValue(field))
                    val sensorEvenData: SensorEvent = getActiveMinuteData(dp.getValue(field))
                    sensorEventDataList.add(sensorEvenData)
                }
                "com.google.calories.expended" -> {
                    val field = dp.dataType.fields[0]
                    LampLog.e(TAG, "Calories Expended : " + dp.getValue(field))
                    val sensorEvenData: SensorEvent = getCalorieData(dp.getValue(field))
                    sensorEventDataList.add(sensorEvenData)
                }
                "com.google.activity.segment" -> {
                    val field = dp.dataType.fields[0]
                    LampLog.e(TAG, "Activity Segment : " + dp.getValue(field))
                    val sensorEvenData: SensorEvent = getSleepData(dp.getValue(field))
                    sensorEventDataList.add(sensorEvenData)
                }
                "com.google.step_count.delta" -> {
                    val field = dp.dataType.fields[0]
                    LampLog.e(TAG, "Step Count : " + dp.getValue(field))
                    val sensorEvenData: SensorEvent = getStepsData(dp.getValue(field))
                    sensorEventDataList.add(sensorEvenData)
                }
                "com.google.distance.delta" -> {
                    val field = dp.dataType.fields[0]
                    LampLog.e(TAG, "Distance : " + dp.getValue(field))
                    val sensorEvenData: SensorEvent = getDistanceData(dp.getValue(field))
                    sensorEventDataList.add(sensorEvenData)
                }
                "com.google.calories.bmr" -> {
                    val field = dp.dataType.fields[0]
                    LampLog.e(TAG, "bmr : " + dp.getValue(field))
                    val sensorEvenData: SensorEvent = getBmrData(dp.getValue(field))
                    sensorEventDataList.add(sensorEvenData)
                }
                "com.google.heart_rate.bpm" -> {
                    LampLog.e(TAG, "bpm")
                    val field = dp.dataType.fields[0]
                    val sensorEvenData: SensorEvent = getHeartRateData(dp.getValue(field))
                    sensorEventDataList.add(sensorEvenData)
                }
                "com.google.body.fat.percentage" -> {
                    val field = dp.dataType.fields[0]
                    LampLog.e(TAG, "Body Fat Percentage : " + dp.getValue(field))
                    val sensorEvenData: SensorEvent = getBodyFatPercentage(dp.getValue(field))
                    sensorEventDataList.add(sensorEvenData)
                }
                "com.google.cycling.wheel_revolution.rpm" -> {
                    val field = dp.dataType.fields[0]
                    LampLog.e(TAG, "Cycling Wheel rpm: " + dp.getValue(field))
                    val sensorEvenData: SensorEvent = getCyclingWheelRpm(dp.getValue(field))
                    sensorEventDataList.add(sensorEvenData)
                }
                "com.google.cycling.wheel_revolution.cumulative" -> {
                    val field = dp.dataType.fields[0]
                    LampLog.e(TAG, "Cycling Pedaling Cumulative: " + dp.getValue(field))
                    val sensorEvenData: SensorEvent = getCyclingPedalingCumulative(dp.getValue(field))
                    sensorEventDataList.add(sensorEvenData)
                }
                "com.google.speed" -> {
                    val field = dp.dataType.fields[0]
                    LampLog.e(TAG, "Speed : " + dp.getValue(field))
                    val sensorEvenData: SensorEvent = getSpeedData(dp.getValue(field))
                    sensorEventDataList.add(sensorEvenData)
                }
                "com.google.hydration" -> {
                    val field = dp.dataType.fields[0]
                    LampLog.e(TAG, "Hydration : " + dp.getValue(field))
                    val sensorEvenData: SensorEvent = getHydrationData(dp.getValue(field))
                    sensorEventDataList.add(sensorEvenData)
                }
                "com.google.nutrition" -> {
                    val field = dp.dataType.fields[0]
                    LampLog.e(TAG, "Nutrition : " + dp.getValue(field))
                    val sensorEvenData: SensorEvent = getNutritionData(dp.getValue(field))
                    sensorEventDataList.add(sensorEvenData)
                }
                "com.google.blood_glucose" -> {
                    val field = dp.dataType.fields[0]
                    LampLog.e(TAG, "Blood Glucose : " + dp.getValue(field))
                    val sensorEvenData: SensorEvent = getBloodGlucose(dp.getValue(field))
                    sensorEventDataList.add(sensorEvenData)
                }
                "com.google.blood_pressure" -> {
                    val field1 = dp.dataType.fields[0]
                    val field2 = dp.dataType.fields[1]
                    val sensorEvenData: SensorEvent = getBloodPressure(dp.getValue(field1).asFloat(),dp.getValue(field2).asFloat())
                    sensorEventDataList.add(sensorEvenData)
                }
                "com.google.oxygen_saturation" -> {
                    val field = dp.dataType.fields[0]
                    LampLog.e(TAG, "Oxygen Saturation : " + dp.getValue(field))
                    val sensorEvenData: SensorEvent = getOxygenSaturation(dp.getValue(field))
                    sensorEventDataList.add(sensorEvenData)
                }
                "com.google.body.temperature" -> {
                    val field = dp.dataType.fields[0]
                    LampLog.e(TAG, "Body Temperature: " + dp.getValue(field))
                    val sensorEvenData: SensorEvent = getBodyTemperature(dp.getValue(field))
                    sensorEventDataList.add(sensorEvenData)
                }
                "com.google.menstruation" -> {
                    val field = dp.dataType.fields[0]
                    LampLog.e(TAG, "Menstruation Data: " + dp.getValue(field))
                    val sensorEvenData: SensorEvent = getMenstruationData(dp.getValue(field))
                    sensorEventDataList.add(sensorEvenData)
                }
                "com.google.vaginal_spotting" -> {
                    val field = dp.dataType.fields[0]
                    LampLog.e(TAG, "Vaginal Spotting: " + dp.getValue(field))
                    val sensorEvenData: SensorEvent = getVaginalSpottingData(dp.getValue(field))
                    sensorEventDataList.add(sensorEvenData)
                }
                "com.google.cycling.pedaling.cadence" -> {
                    val field = dp.dataType.fields[0]
                    val sensorEvenData: SensorEvent = getCyclingPedalingCadenceData(dp.getValue(field))
                    sensorEventDataList.add(sensorEvenData)
                }
                "com.google.heart_minutes" -> {
                    val field = dp.dataType.fields[0]
                    val sensorEvenData: SensorEvent = getHeartMinuteData(dp.getValue(field))
                    sensorEventDataList.add(sensorEvenData)
                }
                "com.google.power.sample" -> {
                    val field = dp.dataType.fields[0]
                    val sensorEvenData: SensorEvent = getPowerData(dp.getValue(field))
                    sensorEventDataList.add(sensorEvenData)
                }
                "com.google.step_count.cadence" -> {
                    val field = dp.dataType.fields[0]
                    val sensorEvenData: SensorEvent = getStepCountCadenceData(dp.getValue(field))
                    sensorEventDataList.add(sensorEvenData)
                }
            }
        }
    }

    private fun queryFitnessData(): DataReadRequest {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        val now = Date()
        calendar.time = now
        val endTime = calendar.timeInMillis
//        calendar.add(Calendar.DAY_OF_WEEK, -1)
        val startTime : Long = AppState.session.lastAnalyticsTimestamp

        return DataReadRequest.Builder()
            .read(DataType.TYPE_STEP_COUNT_DELTA)
            .read(DataType.TYPE_DISTANCE_DELTA)
            .read(DataType.TYPE_WEIGHT)
            .read(DataType.TYPE_HEIGHT)
            .read(DataType.TYPE_HEART_RATE_BPM)
            .read(DataType.TYPE_MOVE_MINUTES)
            .read(DataType.TYPE_CALORIES_EXPENDED)
            .read(DataType.TYPE_ACTIVITY_SEGMENT)
            .read(DataType.TYPE_BASAL_METABOLIC_RATE)
            .read(DataType.TYPE_BODY_FAT_PERCENTAGE)
            .read(DataType.TYPE_SPEED)
            .read(DataType.TYPE_CYCLING_WHEEL_RPM)
            .read(DataType.TYPE_CYCLING_PEDALING_CUMULATIVE)
            .read(DataType.TYPE_HYDRATION)
            .read(DataType.TYPE_NUTRITION)
            .read(HealthDataTypes.TYPE_BLOOD_GLUCOSE)
            .read(HealthDataTypes.TYPE_BLOOD_PRESSURE)
            .read(HealthDataTypes.TYPE_OXYGEN_SATURATION)
            .read(HealthDataTypes.TYPE_BODY_TEMPERATURE)
            .read(HealthDataTypes.TYPE_MENSTRUATION)
            .read(HealthDataTypes.TYPE_VAGINAL_SPOTTING)
            .read(DataType.TYPE_CYCLING_PEDALING_CADENCE)
            .read(DataType.TYPE_HEART_POINTS)
            .read(DataType.TYPE_POWER_SAMPLE)
            .read(DataType.TYPE_STEP_COUNT_CADENCE)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .setLimit(1)
            .build()
    }

    //1
    private fun getWeightData(weight: Value): SensorEvent {
        val dimensionData =
            DimensionData(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null, null,
                "Kg",
                weight.asFloat(),null
            )
        return SensorEvent(dimensionData, "lamp.weight",System.currentTimeMillis().toDouble())
    }

    //2
    private fun getHeightData(height: Value): SensorEvent {
        val dimensionData =
            DimensionData(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,null,
                "meter",
                height.asFloat(),null
            )
        return SensorEvent(dimensionData, "lamp.height",System.currentTimeMillis().toDouble())
    }

    //3
    private fun getSleepData(sleep: Value): SensorEvent {
        val dimensionData =
            DimensionData(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,null,null,
                null,
                sleep.asInt(),null
            )
        return SensorEvent(dimensionData, "lamp.sleep",System.currentTimeMillis().toDouble())
    }

    //4
    private fun getCalorieData(calories: Value): SensorEvent {
        val dimensionData =
            DimensionData(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,null,null,
                "kcal",
                calories.asFloat(),null
            )
        return SensorEvent(dimensionData, "lamp.calories",System.currentTimeMillis().toDouble())
    }

    //5
    private fun getStepsData(steps: Value): SensorEvent {
        val dimensionData =
            DimensionData(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                steps.asInt(),
                null,
                null,null, null,null
            )
        return SensorEvent(dimensionData, "lamp.steps",System.currentTimeMillis().toDouble())
    }

    //6
    private fun getDistanceData(distanceDelta: Value): SensorEvent {
        val dimensionData =
            DimensionData(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,null,null,
                "meter",
                distanceDelta.asFloat(),null
            )
        return SensorEvent(dimensionData, "lamp.distance",System.currentTimeMillis().toDouble())
    }

    //7
    private fun getActiveMinuteData(minute: Value): SensorEvent {
        val dimensionData =
            DimensionData(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,null,minute.asInt(),null
            )
        return SensorEvent(dimensionData, "lamp.active_minute",System.currentTimeMillis().toDouble())
    }

    //8
    private fun getBmrData(bmrCalorie: Value): SensorEvent {
        val dimensionData =
            DimensionData(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,null,null,
                "Kcal",
                bmrCalorie.asFloat(),null
            )
        return SensorEvent(dimensionData, "lamp.calories_bmr",System.currentTimeMillis().toDouble())
    }

    //9
    private fun getHeartRateData(heart_rate: Value): SensorEvent {
        val dimensionData =
            DimensionData(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,null,null,
                "bpm",
                heart_rate.asFloat(),null
            )
        return SensorEvent(dimensionData, "lamp.heart_rate",System.currentTimeMillis().toDouble())
    }

    //10
    private fun getBodyFatPercentage(body_fat_percentage: Value): SensorEvent {
        val dimensionData =
            DimensionData(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,null,null,
                "Percentage",
                body_fat_percentage.asFloat(),null
            )
        return SensorEvent(dimensionData, "lamp.body_fat_percentage",System.currentTimeMillis().toDouble())
    }

    //11
    private fun getCyclingWheelRpm(cycling_wheel_rpm: Value): SensorEvent {
        val dimensionData =
            DimensionData(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,null,null,
                "rpm",
                cycling_wheel_rpm.asFloat(),null
            )
        return SensorEvent(dimensionData, "lamp.cycling_wheel_rpm",System.currentTimeMillis().toDouble())
    }

    //12
    private fun getCyclingPedalingCumulative(pedalingCumulative: Value): SensorEvent {
        val dimensionData =
            DimensionData(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,null,null,
                "count",
                pedalingCumulative.asInt(),null
            )
        return SensorEvent(dimensionData, "lamp.wheel_revolution_cumulative",System.currentTimeMillis().toDouble())
    }

    //13
    private fun getSpeedData(speed: Value): SensorEvent {
        val dimensionData =
            DimensionData(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,null,null,
                "meters per second",
                speed.asFloat(),null
            )
        return SensorEvent(dimensionData, "lamp.speed",System.currentTimeMillis().toDouble())
    }

    //14
    private fun getHydrationData(hydration: Value): SensorEvent {
        val dimensionData =
            DimensionData(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,null,null,
                "liters",
                hydration.asFloat(),null
            )
        return SensorEvent(dimensionData, "lamp.hydration",System.currentTimeMillis().toDouble())
    }

    //15
    private fun getNutritionData(nutrition: Value): SensorEvent {
        val dimensionData =
            DimensionData(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,null,null,
                "enum",
                nutrition.asInt(),null
            )
        return SensorEvent(dimensionData, "lamp.nutrition",System.currentTimeMillis().toDouble())
    }

    //16
    private fun getBloodGlucose(bloodGlucose: Value): SensorEvent {
        val dimensionData =
            DimensionData(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,null,null,
                "mmol/L",
                bloodGlucose.asFloat(),null
            )
        return SensorEvent(dimensionData, "lamp.blood_glucose",System.currentTimeMillis().toDouble())
    }

    //17
    private fun getBloodPressure(bpSystolic: Float, bpDiastolic: Float): SensorEvent {
        val dimensionData =
            DimensionData(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,bpSystolic,bpDiastolic,
                "mmHg",
                null,null
            )
        return SensorEvent(dimensionData, "lamp.blood_pressure",System.currentTimeMillis().toDouble())
    }

    //18
    private fun getOxygenSaturation(oxygenSaturation: Value): SensorEvent {
        val dimensionData =
            DimensionData(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,null,null,
                "percentage",
                oxygenSaturation.asFloat(),null
            )
        return SensorEvent(dimensionData, "lamp.oxygen_saturation",System.currentTimeMillis().toDouble())
    }

    //19
    private fun getBodyTemperature(bodyTemperature: Value): SensorEvent{
        val dimensionData =
            DimensionData(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null, null,null,
                "celsius",
                bodyTemperature.asFloat(),null
            )
        return SensorEvent(dimensionData, "lamp.body_temperature",System.currentTimeMillis().toDouble())
    }
    //20
    private fun getMenstruationData(mentruationData: Value): SensorEvent {
        val dimensionData =
            DimensionData(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null, null,null,
                "enum",
                mentruationData.asInt(),null
            )
        return SensorEvent(dimensionData, "lamp.type_menstruation",System.currentTimeMillis().toDouble())
    }
    //21
    private fun getVaginalSpottingData(vaginalSpotting: Value): SensorEvent {
        val dimensionData =
            DimensionData(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null, null,null,
                "count",
                vaginalSpotting.asInt(),null
            )
        return SensorEvent(dimensionData, "lamp.type_vaginal_spotting",System.currentTimeMillis().toDouble())
    }
//22
    private fun getCyclingPedalingCadenceData(cyclingPedalingCadence: Value): SensorEvent {
    val dimensionData =
        DimensionData(
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null, null,null,
            "rpm",
            cyclingPedalingCadence.asFloat(),null
        )
    return SensorEvent(dimensionData, "lamp.cycling_pedaling_cadence",System.currentTimeMillis().toDouble())
    }
//23
    private fun getHeartMinuteData(heartMinute: Value): SensorEvent {
    val dimensionData =
        DimensionData(
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null, null,null,
            "heart points",
            heartMinute.asFloat(),null
        )
        return SensorEvent(dimensionData, "lamp.heart_minutes",System.currentTimeMillis().toDouble())
    }
//24
    private fun getPowerData(powerData: Value): SensorEvent {
    val dimensionData =
        DimensionData(
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null, null,null,
            "watts",
            powerData.asFloat(),null
        )
    return SensorEvent(dimensionData, "lamp.power",System.currentTimeMillis().toDouble())    }
//25
    private fun getStepCountCadenceData(count: Value): SensorEvent {
    val dimensionData =
        DimensionData(
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null, null,null,
            "steps/minute",
            count.asFloat(),null
        )
    return SensorEvent(dimensionData, "lamp.step_count_cadence",System.currentTimeMillis().toDouble())
    }
}