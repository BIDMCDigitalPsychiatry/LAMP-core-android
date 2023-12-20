package digital.lamp.mindlamp.model

/**
 * Data class representing DiagnosticData.
 *
 * @param action The name of the action.
 * @param content The DiagnosticDataContent (can be null).
 * @param device_type The device type
 * @param user_agent The user agent
 */
data class DiagnosticData(
    val action: String?,
    val content: DiagnosticDataContent?,
    val device_type: String?,
    val user_agent: String?
)

/**
 * The data class represents DiagnosticDataContent
 * @param availableDiskSpace The DiagnosticDiskStorage
 * @param configuredSensors The list of configured sensors
 * @param isAPIInProgress Boolean value status of api progress
 * @param isLogin login status
 * @param isLowpowerMode device battery status
 * @param isRunning service running status
 * @param isWiFiReachable wifi reach status
 * @param locaitonAutorizationStatus
 * @param pendingData
 */
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

/**
 * The data class for PendingData
 * @param fromTimeStamp
 * @param numberOfRecords
 */
data class PendingData(
    val fromTimeStamp: String?,
    val numberOfRecords: String?
)

/**
 * The data class for diagnostic storage
 * @param totalStorage total storage
 * @param availableStorage available storage
 */
data class DiagnosticStorage(
    val totalStorage :String?,
    val availableStorage :String?,
)