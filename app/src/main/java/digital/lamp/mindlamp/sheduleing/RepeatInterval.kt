package digital.lamp.mindlamp.sheduleing

enum class RepeatInterval(val tag: String) {
    HOURLY("hourly"),
    EVERY_3H("every3h"),
    EVERY_6H("every6h"),
    EVERY_12H("every12h"),
    CUSTOM("custom"),
    DAILY("daily"),
    BIWEEKLY("biweekly"), //TUESDAY AND THURSDAY
    TRIWEEKLY("triweekly"), //MONDAY, WEDNESDAY AND FRIDAY
    WEEKLY("weekly"), // WEEK OF THE TIME
    BIMONTHLY("bimonthly"), //10 AND 20
    MONTHLY("monthly"),
    NONE("none")
}