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

package digital.lamp.mindlamp.sensor.health_services

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope


import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Holds most of the interaction logic and UI state for the app.
 */
@HiltViewModel
class HealthServiceViewModel @Inject constructor(
    private val repository: HealthServiceDataRepository,
    private val healthServicesManager: HealthServicesManager
) : ViewModel() {

    val latestHeartRate = repository.latestHeartRate

    init {
        // Check that the device has the heart rate capability and progress to the next state
        // accordingly.


        viewModelScope.launch {
            healthServicesManager.registerForHeartRateData()
        }

    }

    fun unregister() {
        viewModelScope.launch {
            healthServicesManager.unregisterForHeartRateData()
        }
    }

}

