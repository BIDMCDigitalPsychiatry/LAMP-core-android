package digital.lamp.mindlamp.standalone.web

import digital.lamp.mindlamp.standalone.web.pojo.request.RequestBase
import digital.lamp.mindlamp.standalone.web.pojo.response.ResponseBase


class WebServiceResponseData {
    var responseBase: ResponseBase? = null
    var isSuccessful: Boolean? = null
    var responseCode = 0
    var responseMessage: String? = null
    var requestCode = 0
    var requestBase: RequestBase? = null
}