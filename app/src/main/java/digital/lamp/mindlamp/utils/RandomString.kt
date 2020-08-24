package digital.lamp.mindlamp.utils

object RandomString {

    fun getStringVal(length: Int): String? {
        var randomString = ""
        for (i in 0 until length) {
            var asc = 0
            while (true) {
                asc = (Math.random() * 75).toInt() + 48
                if (asc >= 48 && asc <= 57) {
                    break
                }
                if (asc >= 65 && asc <= 90) {
                    break
                }
                if (asc >= 97 && asc <= 122) {
                    break
                }
            }
            randomString = randomString + asc.toChar()
        }
        return randomString
    }
}