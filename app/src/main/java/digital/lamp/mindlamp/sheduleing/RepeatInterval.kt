package digital.lamp.mindlamp.sheduleing

/**
 * Enum class representing different repeat intervals for scheduling.
 *
 * @param tag A tag associated with each repeat interval.
 */
enum class RepeatInterval(val tag: String) {
    HOURLY("hourly"),// Every hour
    EVERY_3H("every3h"),// Every 3 hours
    EVERY_6H("every6h"), // Every 6 hours
    EVERY_12H("every12h"), // Every 12 hours
    CUSTOM("custom"), // Custom interval
    DAILY("daily"),  // Every day
    BIWEEKLY("biweekly"), //TUESDAY AND THURSDAY
    TRIWEEKLY("triweekly"), //MONDAY, WEDNESDAY AND FRIDAY
    WEEKLY("weekly"), // WEEK OF THE TIME
    BIMONTHLY("bimonthly"), //10 AND 20
    MONTHLY("monthly"),// Monthly
    FORTNIGHTLY("fortnightly"),//every 2 weeks
    NONE("none")// No repeat
}