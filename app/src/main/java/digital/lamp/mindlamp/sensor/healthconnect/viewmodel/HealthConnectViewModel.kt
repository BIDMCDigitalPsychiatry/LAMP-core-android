package digital.lamp.mindlamp.sensor.healthconnect.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HealthConnectViewModel @Inject constructor(
) : ViewModel() {

    var permissionsGranted = MutableLiveData<Boolean>()

    init {
        permissionsGranted.value = false
    }

}