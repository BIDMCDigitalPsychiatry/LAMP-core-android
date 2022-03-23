package digital.lamp.mindlamp.model

data class DiagnosticData(
    val action: String?,
    val content: DiagnosticDataContent?,
    val device_type: String?,
    val user_agent: String?
)

data class DiagnosticDataContent(
        val availableDiskSpace: DiagnosticStorage?,
        val configuredSensors: List<String>?,
        val isAPIInProgress: Boolean?,
        val isLogin: Boolean?,
        val isLowpowerMode: Boolean?,
        val isRunning: Boolean?,
        val isWiFiReachable: Boolean?,
        val locaitonAutorizationStatus: String?,
        val pendingData:PendingData?
)

data class PendingData(
    val fromTimeStamp: String?,
    val numberOfRecords: String?
)

data class DiagnosticStorage(
    val totalStorage :String?,
    val availableStorage :String?,
)