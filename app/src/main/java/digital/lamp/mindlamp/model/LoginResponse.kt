package digital.lamp.mindlamp.model

/**
 * Created by ZCO Engineering Dept. on 13,February,2020
 */
/**
 * Data class representing the response from a login operation.
 *
 * @param authorizationToken The authorization token obtained after successful login.
 * @param identityObject The identity object associated with the logged-in user.
 * @param serverAddress The server address or endpoint for the logged-in user.
 * @param deleteCache Flag indicating whether to delete the cache (default is false).
 */
data class LoginResponse(
    val authorizationToken: String,
    val identityObject: IdentityObject,
    val serverAddress: String,
    val deleteCache: Boolean = false
)
/**
 * Data class representing an identity object.
 *
 * @param id The unique identifier associated with the identity.
 * @param language
 */
data class IdentityObject(
    val id: String,
    val language: String
)