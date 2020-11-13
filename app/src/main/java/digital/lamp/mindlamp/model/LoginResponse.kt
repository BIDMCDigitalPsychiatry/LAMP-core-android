package digital.lamp.mindlamp.model

/**
 * Created by ZCO Engineering Dept. on 13,February,2020
 */
data class LoginResponse(
    val authorizationToken: String,
    val identityObject: IdentityObject,
    val serverAddress: String,
    val deleteCache: Boolean = false
)

data class IdentityObject(
    val id: String,
    val language: String
)