package lamp.mindlamp.sensormodule.aware.aware.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import lamp.mindlamp.sensormodule.aware.model.DimensionData

/**
 * Created by ZCO Engineering Dept. on 05,February,2020
 */
data class SensorEventData(
    @SerializedName("data")
    @Expose
    val dimensionData: DimensionData?,
    val sensor: String,
    val timestamp: Long
)
