/**
* LAMP Platform
* The LAMP Platform API.
*
* The version of the OpenAPI document: 1.0.0
* Contact: team@digitalpsych.org
*
* NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
* https://openapi-generator.tech
* Do not edit the class manually.
*/
package digital.lamp.models

import com.squareup.moshi.Json
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

import java.io.Serializable
/**
 * A specific sub-detail of a `ActivityEvent` that contains specific  interaction information that comprises the parent `ActivityEvent`.
 * @param item The item that was interacted with; for example, in a Jewels game, the  corresponding alphabet, or in a survey, the question index.
 * @param value The value of the item that was interacted with; in most games,  this field is  `null`, but in a survey, this field is the question  choice index.
 * @param type The type of interaction that for this detail; for example, in  a Jewels game,  `none` if the tapped jewel was  incorrect, or `correct` if it was correct, or in  a  survey, this field will be `null`.
 * @param duration The time difference from the previous detail or the  start of the parent result.
 * @param level The level of activity for this detail; for example, in  games with multiple  levels, this field might be `2` or  `4`, but for surveys and other games this field  will be `null`.
 */
@Parcelize

data class TemporalSlice (
    /* The item that was interacted with; for example, in a Jewels game, the  corresponding alphabet, or in a survey, the question index. */
    @Json(name = "item")
    var item: @RawValue kotlin.Any? = null,
    /* The value of the item that was interacted with; in most games,  this field is  `null`, but in a survey, this field is the question  choice index. */
    @Json(name = "value")
    var value: @RawValue kotlin.Any? = null,
    /* The type of interaction that for this detail; for example, in  a Jewels game,  `none` if the tapped jewel was  incorrect, or `correct` if it was correct, or in  a  survey, this field will be `null`. */
    @Json(name = "type")
    var type: kotlin.String? = null,
    /* The time difference from the previous detail or the  start of the parent result. */
    @Json(name = "duration")
    var duration: kotlin.Long? = null,
    /* The level of activity for this detail; for example, in  games with multiple  levels, this field might be `2` or  `4`, but for surveys and other games this field  will be `null`. */
    @Json(name = "level")
    var level: kotlin.Long? = null
) : Serializable, Parcelable {
	companion object {
		private const val serialVersionUID: Long = 123
	}

}

