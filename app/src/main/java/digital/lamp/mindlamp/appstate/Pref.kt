package digital.lamp.mindlamp.appstate

import android.content.Context
import android.content.SharedPreferences
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Created by Zco Developer on 11/30/2018.
 * Copyright ChallengeMe
 */
open class Pref<T>(private val key: String, private val default: T) : ReadWriteProperty<Any?, T> {
    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: Any?, property: KProperty<*>): T {

        return when (default) {
            is Int -> prefSharedPreferences.getInt(key, default) as T
            is String -> prefSharedPreferences.getString(key, default) as T
            is Boolean -> prefSharedPreferences.getBoolean(key, default) as T
            is Float -> prefSharedPreferences.getFloat(key, default) as T
            is Long -> prefSharedPreferences.getLong(key, default) as T
            else -> throw IllegalArgumentException("Data type not supported")
        }
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        val editor: SharedPreferences.Editor = prefSharedPreferences.edit()

        when (value) {
            is String -> editor.putString(key, value)
            is Float -> editor.putFloat(key, value)
            is Int -> editor.putInt(key, value)
            is Boolean -> editor.putBoolean(key, value)
            is Long -> editor.putLong(key, value)
            else -> throw IllegalArgumentException("${property.name} variable type is not supported yet!!")
        }

        editor.apply()
    }

    companion object {
        private lateinit var prefName: String
        private lateinit var prefSharedPreferences: SharedPreferences
        private var prefMode: Int = Context.MODE_PRIVATE

        fun init(ctx: Context, name: String, mode: Int = Context.MODE_PRIVATE) {
            prefSharedPreferences = ctx.getSharedPreferences(name,
                prefMode
            )
            prefName = name
            prefMode = mode
        }
    }
}