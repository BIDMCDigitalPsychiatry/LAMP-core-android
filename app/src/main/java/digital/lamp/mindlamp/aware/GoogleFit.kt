package digital.lamp.mindlamp.aware

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataSet
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Value
import com.google.android.gms.fitness.request.DataReadRequest
import digital.lamp.mindlamp.network.model.DimensionData
import digital.lamp.mindlamp.network.model.SensorEventData
import digital.lamp.mindlamp.utils.LampLog
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class GoogleFit constructor(awareListener: AwareListener,context: Context) {

    companion object {
        private val TAG = GoogleFit::class.java.simpleName
    }
    private var sensorEventDataList: ArrayList<SensorEventData> = arrayListOf()

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
                .build()
        }

        //Read History Data
        val readRequest = queryFitnessData()

        Fitness.getHistoryClient(context,GoogleSignIn.getAccountForExtension(context, fitnessOptions))
            .readData(readRequest)
            .addOnSuccessListener { dataReadResponse ->
                LampLog.e(TAG,"Success : ${dataReadResponse.dataSets.size}")
                if(dataReadResponse.dataSets.isNotEmpty()){
                    dataReadResponse.dataSets.forEach {
                        dumpDataSet(it)
                    }
                }
                awareListener.getGoogleFitData(sensorEventDataList)
            }
            .addOnFailureListener { exception ->
                LampLog.e(TAG,"Problem in reading data : $exception")
            }
    }

    private fun dumpDataSet(dataSet: DataSet) {
        LampLog.e(TAG,"Data returned for Data type: ${dataSet.dataType.name}")
        for (dp in dataSet.dataPoints){
            LampLog.e(TAG, "Type: ${dp.dataType.name}")
            dp.dataType.fields.forEach {
                LampLog.e(TAG, "Field: ${it.name} Value: ${dp.getValue(it)}")
                when(it.name){
                    "weight" -> {
                        LampLog.e(TAG,"Weight : ${dp.getValue(it)}")
                        val sensorEvenData : SensorEventData = getWeightData(dp.getValue(it))
                        sensorEventDataList.add(sensorEvenData)
                    }
                    "height" -> {
                        LampLog.e(TAG,"Height : ${dp.getValue(it)}")
                        val sensorEvenData : SensorEventData = getHeightData(dp.getValue(it))
                        sensorEventDataList.add(sensorEvenData)
                    }
                    "activity" -> {
                        LampLog.e(TAG,"Sleep : ${dp.getValue(it)}")
                        val sensorEvenData : SensorEventData = getSleepData(dp.getValue(it))
                        sensorEventDataList.add(sensorEvenData)

                    }
                    "duration" -> {
                        LampLog.e(TAG," : ${dp.getValue(it)}")
                        val sensorEvenData : SensorEventData = getActiveMinuteData(dp.getValue(it))
                        sensorEventDataList.add(sensorEvenData)

                    }
                    "calories" -> {
                        LampLog.e(TAG," : ${dp.getValue(it)}")
                        val sensorEvenData : SensorEventData = getCalorieData(dp.getValue(it))
                        sensorEventDataList.add(sensorEvenData)

                    }
                    "steps" -> {
                        LampLog.e(TAG," : ${dp.getValue(it)}")
                        val sensorEvenData : SensorEventData = getStepsData(dp.getValue(it))
                        sensorEventDataList.add(sensorEvenData)

                    }
                    "distance" -> {
                        LampLog.e(TAG," : ${dp.getValue(it)}")
                        val sensorEvenData : SensorEventData = getDistanceData(dp.getValue(it))
                        sensorEventDataList.add(sensorEvenData)

                    }
                }
            }
        }
    }

    private fun queryFitnessData(): DataReadRequest {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        val now = Date()
        calendar.time = now
        val endTime = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_WEEK, -1)
        val startTime = calendar.timeInMillis

        return DataReadRequest.Builder()
            .read(DataType.TYPE_WEIGHT)
            .read(DataType.TYPE_HEIGHT)
            .read(DataType.TYPE_ACTIVITY_SEGMENT)
            .read(DataType.TYPE_HEART_RATE_BPM)
            .read(DataType.TYPE_MOVE_MINUTES)
            .read(DataType.TYPE_CALORIES_EXPENDED)
            .read(DataType.TYPE_STEP_COUNT_DELTA)
            .read(DataType.TYPE_DISTANCE_DELTA)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .setLimit(1)
            .build()
    }

    private fun getWeightData(weight : Value): SensorEventData{
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
                "Kg",
                weight.asFloat()
            )
        return SensorEventData(dimensionData,"lamp.weight")
    }
    private fun getHeightData(height : Value): SensorEventData{
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
                "Cm",
                height.asFloat()
            )
        return SensorEventData(dimensionData,"lamp.height")
    }
    private fun getSleepData(sleep :Value): SensorEventData {
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
                sleep.asInt()
            )
        return SensorEventData(dimensionData,"lamp.sleep")
    }

    private fun getCalorieData(calories : Value): SensorEventData {
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
                "kcal",
                calories.asFloat()
            )
        return SensorEventData(dimensionData,"lamp.calories")
    }

    private fun getStepsData(steps : Value): SensorEventData {
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
                null
            )
        return SensorEventData(dimensionData,"lamp.steps")
    }

    private fun getDistanceData(distanceDelta : Value): SensorEventData {
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
                "meter",
                distanceDelta.asFloat()
            )
        return SensorEventData(dimensionData,"lamp.distance")
    }

    private fun getActiveMinuteData(minute : Value): SensorEventData {
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
                minute.asInt()
            )
        return SensorEventData(dimensionData,"lamp.active_minute")
    }
}