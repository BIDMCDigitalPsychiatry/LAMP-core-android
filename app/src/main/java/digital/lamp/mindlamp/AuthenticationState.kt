package digital.lamp.mindlamp

import digital.lamp.mindlamp.model.LoginResponse

/**
 * Created by ZCO Engineering Dept. on 13,February,2020
 */
sealed class AuthenticationState {
    object SignedIn : AuthenticationState()
    data class StoredCredentials(val credentials: LoginResponse) : AuthenticationState()
    object SignedOut : AuthenticationState()
}