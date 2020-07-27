package digital.lamp.mindlamp.standalone.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import digital.lamp.mindlamp.standalone.viewmodels.repositories.WebServiceRepository;
import digital.lamp.mindlamp.standalone.web.WebServiceResponseData;

public class BaseViewModel extends AndroidViewModel {

    WebServiceRepository mWebServiceRepository;

    public BaseViewModel(@NonNull Application application) {
        super(application);

        mWebServiceRepository = WebServiceRepository.Companion.getInstance();

    }

    public LiveData<WebServiceResponseData> getWebServiceResponseLiveData() {
        return mWebServiceRepository.getWebServiceResponseLiveData();
    }

}
