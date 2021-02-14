package digital.lamp.mindlamp.utils

enum class Sensors(val sensor_name: String) {
    GPS("lamp.gps"),
    DEVICE_MOTION("lamp.device_motion"),
    STEPS("lamp.steps"),
    NEARBY_DEVICES("lamp.nearby_device"),
    TELEPHONY("lamp.telephony"),
    SCREEN_STATE("lamp.screen_state"),
    SEGMENT("lamp.segment"),
    SLEEP("lamp.sleep"),
    NUTRITION("lamp.nutrition"),
    BLOOD_GLUCOSE("lamp.blood_glucose"),
    BLOOD_PRESSURE("lamp.blood_pressure"),
    OXYGEN_SATURATION("lamp.oxygen_saturation"),
    BODY_TEMPERATURE("lamp.body_temperature"),
    HEART_RATE("lamp.heart_rate"),
    RESPIRATORY_RATE("lamp.respiratory_rate")
}