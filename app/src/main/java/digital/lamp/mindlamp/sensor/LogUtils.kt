package digital.lamp.mindlamp.sensor

import digital.lamp.mindlamp.network.model.LogEventRequest
import digital.lamp.mindlamp.repository.HomeRepository
import digital.lamp.mindlamp.utils.LampLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object LogUtils {
    fun invokeLogData(origin:String, level:String, logEventRequest: LogEventRequest) {
        val homeRepository = HomeRepository()
        GlobalScope.launch(Dispatchers.IO){
            try {
                val addLogEventResult = homeRepository.addLogData(origin, level, logEventRequest)
                LampLog.e("Log Utils : "," : $addLogEventResult")
            }catch (er: Exception){er.printStackTrace()}
        }
    }
}