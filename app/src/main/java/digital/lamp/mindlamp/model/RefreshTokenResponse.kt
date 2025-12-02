package digital.lamp.mindlamp.model
data class TokenResponseData(
    val data: RefreshTokenResponse
)
data class RefreshTokenResponse (
    val _id: String,
    val origin: String,
    val access_key: String,
    val secret_key: String,
    val description: String,
    val _deleted: Boolean,
    val access_token: String,
    val refresh_token: String
)