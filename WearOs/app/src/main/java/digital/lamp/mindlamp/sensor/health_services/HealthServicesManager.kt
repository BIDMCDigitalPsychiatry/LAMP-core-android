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

import androidx.concurrent.futures.await
import androidx.health.services.client.HealthServicesClient
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.PassiveListenerConfig
import digital.lamp.mindlamp.utils.LampLog
import javax.inject.Inject

/**
 * Entry point for [HealthServicesClient] APIs. This also provides suspend functions around
 * those APIs to enable use in coroutines.
 */
class HealthServicesManager @Inject constructor(
    healthServicesClient: HealthServicesClient
) {

    private val passiveMonitoringClient = healthServicesClient.passiveMonitoringClient
     private val dataTypeAll = setOf(DataType.HEART_RATE_BPM,DataType.STEPS)
    private val dataTypeHeartRate = setOf(DataType.HEART_RATE_BPM )
    private val dataTypeSteps = setOf(DataType.STEPS )

    suspend fun hasHeartRateCapability(): Boolean {
        try {
            val capabilities = passiveMonitoringClient.getCapabilitiesAsync().await()
            return (DataType.HEART_RATE_BPM in capabilities.supportedDataTypesPassiveMonitoring)
        }
        catch (e:Exception){
            LampLog.e("Lamp","hasHeartRateCapability"+e.message,e)
            return false
        }
    }
    suspend fun hasStepsCapability(): Boolean {
        try {
            val capabilities = passiveMonitoringClient.getCapabilitiesAsync().await()
            return (DataType.STEPS in capabilities.supportedDataTypesPassiveMonitoring)
        }
        catch (e:Exception){
            LampLog.e("Lamp","hasStepsCapability"+e.message,e)

            return false
        }
    }

    suspend fun registerHealthServicesData() {
        try {
            var dataType = dataTypeAll
            if (!hasHeartRateCapability() && !hasStepsCapability()) return
            if (hasStepsCapability() && !hasHeartRateCapability())
                dataType = dataTypeSteps
            else if (!hasStepsCapability() && hasHeartRateCapability())
                dataType = dataTypeHeartRate

            val passiveListenerConfig = PassiveListenerConfig.builder()
                .setDataTypes(dataType)
                .build()

            LampLog.d("NewWatch", "Registering heart rate listener")
            passiveMonitoringClient.setPassiveListenerServiceAsync(
                HealthServiceDataService::class.java,
                passiveListenerConfig
            ).await()
        }catch(e:Exception){
            LampLog.e("Lamp","HealthServicesManager:registerHealthServicesData"+e.message,e)
        }

    }

    suspend fun unregister() {
        try {
            LampLog.d("New Watch", "HealthserviceManager: Unregistering listeners")
            passiveMonitoringClient.clearPassiveListenerServiceAsync().await()
        }
        catch(e:Exception){
            LampLog.e("Lamp","HealthServicesManager:Unregister"+e.message,e)
        }
    }

}
