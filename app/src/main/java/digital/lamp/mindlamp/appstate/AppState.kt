package digital.lamp.mindlamp.appstate

class AppState {

    companion object {
        var session = SessionState()
        var encrypteddata: String = ""
        var key: String? = "ccC2H19lDDbQDfakxcrtNMQdd0FloLGG"
        var iv:String?=""
        var deviceid:String?="123456789"
    }
}