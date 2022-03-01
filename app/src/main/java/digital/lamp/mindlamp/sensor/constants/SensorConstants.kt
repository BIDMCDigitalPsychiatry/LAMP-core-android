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

    enum class CallState(val value: String){
        INCOMING("incoming"),
        OUTGOING("outgoing"),
        MISSED("missed")
    }
    enum class ActivityType(val value: String){
        IN_VEHICLE("In vehicle"),
        BIKING("Biking"),
        STILL("Still"),
        UNKNOWN("Unknown"),
        TILTING("Tilting"),
        WALKING("Walking"),
        RUNNING("Running"),
AEROBICS("Aerobics"),
        BADMINTON("Badminton "),
        BASEBALL("Baseball "),
        BASKETBALL("Basketball"),
        BIATHLON("Biathlon"),
        HANDBIKING("Handbiking"),
        MOUNTAIN_BIKING("Mountain biking"),
        ROAD_BIKING("Road biking"),
        SPINNING("Spinning"),
        STATIONARY_BIKING("Stationary biking"),
        UTILITY_BIKING("Utility biking"),
        BOXING("Boxing"),
        CALISTHENICS("Calisthenics"),
        CIRCUIT_TRAINING("Circuit training"),
        CRICKET("Cricket"),
        DANCING("Dancing"),
        ELLIPTICAL("Elliptical"),
        FENCING("Fencing"),
        FOOTBALL_AMERICAN("Football (American)"),
        FOOTBALL_AUSTRALIAN("Football (Australian)"),
        FOOTBALL_SOCCER("Football (Soccer)"),
        FRISBEE("Frisbee"),
        GARDENING("Gardening"),
        GOLF("Golf "),
        GYMNASTICS("Gymnastics"),
        HANDBALL("Handball "),
        HIKING("Hiking"),
        HOCKEY("Hockey"),
        HORSEBACK_RIDING("Horseback riding"),
        HOUSE_WORK("Housework"),
        JUMPING_ROPE("Jumping rope"),
        KAYAKING("Kayaking "),
        KETTLEBELL_TRAINING("Kettlebell training"),
        KICKBOXING("Kickboxing"),
        KITESURFING("Kitesurfing"),
        MARTIAL_ARTS("Martial arts"),
        MEDITATION("Meditation"),
        MIXED_MARTIAL_ARTS("Mixed martial arts"),
        P90X_EXERCISES("P90X exercises"),
        PARAGLIDING("Paragliding"),
        PILATES("Pilates"),
        POLO("Polo"),
        RACQUETBALL("Racquetball"),
        ROCK_CLIMBING("Rock climbing"),
        ROWING("Rowing"),
        ROWING_MACHINE("Rowing machine"),
        RUGBY("Rugby"),
        JOGGING("Jogging"),
        RUNNING_ON_SAND("Running on sand"),
        RUNNING_TREADMILL("Running (treadmill)"),
        SAILING("Sailing"),
        SCUBA_DIVING("Scuba diving"),
        SKATEBOARDING("Skateboarding"),
        SKATING("Skating"),
        CROSS_SKATING("Cross skating"),
        INDOOR_SKATING("Indoor skating"),
        INLINE_SKATING("Inline skating (rollerblading)"),
        SKIING("Skiing"),
        BACK_COUNTRY_SKIING("Back-country skiing"),
        CROSS_COUNTRY_SKIING("Cross-country skiing"),
        DOWNHILL_SKIING("Downhill skiing"),
        KITE_SKIING("Kite skiing"),
        ROLLER_SKIING("Roller skiing"),
        SLEDDING("Sledding"),
        SNOWBOARDING("Snowboarding"),
        SNOWMOBILE("Snowmobile"),
        SNOWSHOEING("Snowshoeing"),
        SOFTBALL("Softball"),
        SQUASH("Squash"),
        STAIR_CLIMBING("Stair climbing"),
        STAIR_CLIMBING_MACHINE("Stair-climbing machine"),
        STAND_UP_PADDLEBOARDING("Stand-up paddleboarding"),
        STRENGTH_TRAINING("Strength training"),
        SURFING("Surfing"),
SWIMMING("Swimming"),
        SWIMMING_OPEN_WATER("Swimming (open water)"),
        SWIMMING_SWIMMING_POOL("Swimming (swimming pool)"),
        TABLE_TENNIS("Table tennis (ping pong)"),
        TEAM_SPORTS("Team sports"),
        TENNIS("Tennis"),
        TREADMILL("Treadmill (walking or running)"),
        VOLLEYBALL("Volleyball"),
        VOLLEYBALL_BEACH("Volleyball (beach)"),
        VOLLEYBALL_INDOOR("Volleyball (indoor)"),
        WAKEBOARDING("Wakeboarding"),
        WALKING_FITNESS("Walking (fitness"),
        NORDING_WALKING("Nording walking"),
WALKING_TREADMILL("Walking (treadmill)"),
        WALKING_STROLLER("Walking (stroller)"),
        WATER_POLO("Waterpolo"),
        WEIGHTLIFTING("Weightlifting"),
        WHEELCHAIR("Wheelchair"),
        WINDSURFING("Windsurfing"),
        YOGA("Yoga"),
        ZUMBA("Zumba"),
        DIVING("Diving"),
        ERGOMETER("Ergometer"),
        ICE_SKATING("Ice skating"),
        OTHER("Other (unclassified fitness activity)"),
        CROSSFIT("Crossfit"),
        HIIT("HIIT "),
        INTERVAL_TRAINING("Interval Training"),
CURLING("Curling"),
       ESCALATOR("Escalator"),
        ELEVATOR("Elevator"),
        ARCHERY("Archery"),
        GUIDED_BREATHING("Guided Breathing")


    }
}