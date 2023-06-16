package digital.lamp.mindlamp.sensor.data

 data class SensorSpecs(

    var identity:Int?,
    var id: String?,
    var spec: String?,
    var name: String?,
    var frequency : Double?,
    var cellularUpload : Boolean?
){

    constructor() : this(null,null,null,null,null,null)
}