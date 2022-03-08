package digital.lamp.mindlamp.web

class WebConstant {
    companion object {
        const val SOCKET_TIME_OUT = 60L
        //Headers
//        var UN = ""
//        var PWD = ""
        var USERID = ""
        /**
         * Codes
         */
        const val CODE_SUCCESS = 200
        const val CODE_FAILED = 1
        const val CODE_SOCKET_TIME_OUT = 30
        const val CODE_INVALID_USER = 403
        //Methods
        const val ADD_LOG_DATA = "/"
        const val ADD_SENSOR_DATA = "participant/{participant_id}/sensor_event"
        const val PARTICIPANT_ID = "/participant/{participant_id}"

        //API Request code
        const val ADDSENSOREVENT_REQ_CODE = 1003
        const val ADDDEVICETOKEN_REQ_CODE = 1004
        const val ISUSEREXISTS_REQ_CODE = 1005
        const val LOGEVENT_REQ_CODE = 1006
        const val ADDNOTIFICATION_REQ_CODE = 1007

    }
}