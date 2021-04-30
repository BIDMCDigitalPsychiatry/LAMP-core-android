package digital.lamp.mindlamp.sensor.constants

object SensorConstants {
    enum class ScreenState(val value :Int){
        SCREEN_ON(0),
        SCREEN_OFF(1),
        SCREEN_LOCKED(2),
        SCREEN_UNLOCKED(3)
    }
    enum class ScreenStateRepresentation(val value :String){
        SCREEN_ON("screen_on"),
        SCREEN_OFF("screen_off"),
        SCREEN_LOCKED("screen_locked"),
        SCREEN_UNLOCKED("screen_unlocked")
    }
}