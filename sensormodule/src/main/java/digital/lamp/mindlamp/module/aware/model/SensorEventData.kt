package lamp.mindlamp.sensormodule.aware.aware.model

import lamp.mindlamp.sensormodule.aware.model.DimensionData
import java.io.Serializable

/**
 * Created by ZCO Engineering Dept. on 05,February,2020
 */
data class SensorEventData(


    val dimensionData: DimensionData?,
    val sensor: String,
    val timestamp: Long
)
