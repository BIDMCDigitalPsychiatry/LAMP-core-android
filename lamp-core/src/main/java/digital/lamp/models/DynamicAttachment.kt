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
 * 
 * @param key The key.
 * @param from A globally unique reference for objects.
 * @param to Either \"me\" to apply to the attachment owner only, the ID of an object owned  by the attachment owner, or a string representing the child object type to apply to.
 * @param triggers The API triggers that activate script execution. These will be event stream types  or object types in the API, or, if scheduling execution periodically, a cron-job string  prefixed with \"!\" (exclamation point).
 * @param language The script language.
 * @param contents The script contents.
 * @param requirements The script requirements.
 */
@Parcelize

data class DynamicAttachment (
    /* The key. */
    @Json(name = "key")
    var key: kotlin.String? = null,
    /* A globally unique reference for objects. */
    @Json(name = "from")
    var from: kotlin.String? = null,
    /* Either \"me\" to apply to the attachment owner only, the ID of an object owned  by the attachment owner, or a string representing the child object type to apply to. */
    @Json(name = "to")
    var to: kotlin.String? = null,
    /* The API triggers that activate script execution. These will be event stream types  or object types in the API, or, if scheduling execution periodically, a cron-job string  prefixed with \"!\" (exclamation point). */
    @Json(name = "triggers")
    var triggers: @RawValue kotlin.Array<kotlin.Any>? = null,
    /* The script language. */
    @Json(name = "language")
    var language: kotlin.String? = null,
    /* The script contents. */
    @Json(name = "contents")
    var contents: kotlin.String? = null,
    /* The script requirements. */
    @Json(name = "requirements")
    var requirements: @RawValue kotlin.Array<kotlin.Any>? = null
) : Serializable, Parcelable {
	companion object {
		private const val serialVersionUID: Long = 123
	}

}

