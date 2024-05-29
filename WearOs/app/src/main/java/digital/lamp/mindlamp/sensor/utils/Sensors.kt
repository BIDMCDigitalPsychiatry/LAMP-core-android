package digital.lamp.mindlamp.sensor.utils

enum class Sensors(val sensor_name: String) {
    GPS("lamp.android.watch.gps"),
    DEVICE_MOTION("lamp.android.watch.device_motion"),
    STEPS("lamp.android.watch.steps"),
    NEARBY_DEVICES("lamp.android.watch.nearby_device"),
    TELEPHONY("lamp.android.watch.telephony"),
    SCREEN_STATE("lamp.android.watch.screen_state"),
    SEGMENT("lamp.android.watch.segment"),
    SLEEP("lamp.android.watch.sleep"),
    NUTRITION("lamp.android.watch.nutrition"),
    BLOOD_GLUCOSE("lamp.android.watch.blood_glucose"),
    BLOOD_PRESSURE("lamp.android.watch.blood_pressure"),
    OXYGEN_SATURATION("lamp.android.watch.oxygen_saturation"),
    BODY_TEMPERATURE("lamp.android.watch.body_temperature"),
    HEART_RATE("lamp.android.watch.heart_rate"),
    RESPIRATORY_RATE("lamp.android.watch.respiratory_rate"),
    ACTIVITY_RECOGNITION("lamp.android.watch.activity_recognition"),
    ACCELEROMETER("lamp.android.watch.accelerometer"),
    DEVICE_STATE("lamp.android.watch.device_state")
}