package digital.lamp.mindlamp.standalone.model

import android.provider.ContactsContract
import com.google.android.gms.wearable.DataMap
import java.io.Serializable

class CustomMap() : Serializable {

    var dataMap: DataMap? = null

    constructor(map: DataMap) : this() {
        this.dataMap = map

    }

}