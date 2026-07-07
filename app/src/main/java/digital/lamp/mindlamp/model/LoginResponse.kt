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
// Every field has a default so Kotlin generates a no-args constructor and Gson
// constructs this normally (honoring defaults) instead of via Unsafe.
// identityObject/serverAddress are nullable because a malformed postMessage can
// omit them regardless of declaration — consumers must validate.
data class LoginResponse(
    val authorizationToken: String? = null,
    val identityObject: IdentityObject? = null,
    val serverAddress: String? = null,
    // Session/Bearer fields (new server). Absent on legacy/basic and
    // external-JWT logins, which send authorizationToken instead.
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val authScheme: String? = null,
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