package digital.lamp.mindlamp.utils

/**
 * All sensors
 */
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
    RESPIRATORY_RATE("lamp.respiratory_rate"),
    ACTIVITY_RECOGNITION("lamp.activity_recognition"),
    ACCELEROMETER("lamp.accelerometer"),
    DEVICE_STATE("lamp.device_state"),
    GPS_1M("lamp.gps1m"),
    ACCELEROMETER_10HZ("lamp.accelerometer10hz"),
    ACCELEROMETER_20HZ("lamp.accelerometer20hz"),
    ACCELEROMETER_50HZ("lamp.accelerometer50hz"),
    ACCELEROMETER_100HZ("lamp.accelerometer100hz")
}