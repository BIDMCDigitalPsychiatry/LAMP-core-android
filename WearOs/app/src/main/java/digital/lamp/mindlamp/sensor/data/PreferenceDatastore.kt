/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package digital.lamp.mindlamp.sensor.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


/**
 * Stores heart rate measurements and whether or not passive data is enabled.
 */
class PreferenceDatastore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {



    suspend fun setLastTimestampAccelerometer(timestamp: Long) {
        dataStore.edit { prefs ->
            prefs[LAST_TIMESTAMP_ACCELEROMETER] = timestamp
        }
    }
    suspend fun setLastTimestampMagnetometer(timestamp: Long) {
        dataStore.edit { prefs ->
            prefs[LAST_TIMESTAMP_MAGNETOMETER] = timestamp
        }
    }
    suspend fun setLastTimestampRotation(timestamp: Long) {
        dataStore.edit { prefs ->
            prefs[LAST_TIMESTAMP_ROTATION] = timestamp
        }
    }
    suspend fun setLastTimestampGravity(timestamp: Long) {
        dataStore.edit { prefs ->
            prefs[LAST_TIMESTAMP_GRAVITY] = timestamp
        }
    }


    val lastTimestampAccelerometer: Flow<Long> = dataStore.data.map { prefs ->
        prefs[LAST_TIMESTAMP_ACCELEROMETER] ?:0
    }
    val lastTimestampMagnetometer: Flow<Long> = dataStore.data.map { prefs ->
        prefs[LAST_TIMESTAMP_MAGNETOMETER] ?:0
    }
    val lastTimestampRotation: Flow<Long> = dataStore.data.map { prefs ->
        prefs[LAST_TIMESTAMP_ROTATION] ?:0
    }
    val lastTimestampGravity: Flow<Long> = dataStore.data.map { prefs ->
        prefs[LAST_TIMESTAMP_GRAVITY] ?:0
    }




    companion object {
            private val LAST_TIMESTAMP_ACCELEROMETER = longPreferencesKey("last_time_stamp_accelerometer")
        private val LAST_TIMESTAMP_MAGNETOMETER = longPreferencesKey("last_time_stamp_magnetometer")
        private val LAST_TIMESTAMP_GRAVITY = longPreferencesKey("last_time_stamp_gravity")
        private val LAST_TIMESTAMP_ROTATION = longPreferencesKey("last_time_stamp_rotation")


    }
}
