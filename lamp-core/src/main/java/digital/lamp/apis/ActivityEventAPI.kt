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
package digital.lamp.apis

import digital.lamp.infrastructure.*
import digital.lamp.models.ActivityEvent


class ActivityEventAPI(basePath: kotlin.String = defaultBasePath) : ApiClient(basePath) {
    companion object {
        @JvmStatic
        val defaultBasePath: String by lazy {
            System.getProperties().getProperty("digital.lamp.lamp-core.baseUrl", "https://api.lamp.digital")
        }
    }

    /**
    * Get all activity events for a participant.
    * Get the set of all activity events produced by a given participant,  by identifier.
    * @param participantId  
    * @param origin  (optional)
    * @param from  (optional)
    * @param to  (optional)
    * @param transform  (optional)
    * @return kotlin.Array<kotlin.Any>
    * @throws UnsupportedOperationException If the API returns an informational or redirection response
    * @throws ClientException If the API returns a client error response
    * @throws ServerException If the API returns a server error response
    */
    @Suppress("UNCHECKED_CAST")
    @Throws(UnsupportedOperationException::class, ClientException::class, ServerException::class)
    fun activityEventAllByParticipant(participantId: kotlin.String, origin: kotlin.String?, from: java.sql.Timestamp?, to: java.sql.Timestamp?, transform: kotlin.String?) : kotlin.Array<kotlin.Any> {
        val localVariableBody: kotlin.Any? = null
        val localVariableQuery: MultiValueMap = mutableMapOf<kotlin.String, List<kotlin.String>>()
            .apply {
                if (origin != null) {
                    put("origin", listOf(origin.toString()))
                }
                if (from != null) {
                    put("from", listOf(from.toString()))
                }
                if (to != null) {
                    put("to", listOf(to.toString()))
                }
                if (transform != null) {
                    put("transform", listOf(transform.toString()))
                }
            }
        val localVariableHeaders: MutableMap<String, String> = mutableMapOf()
        val localVariableConfig = RequestConfig(
            RequestMethod.GET,
            "/participant/{participant_id}/activity_event".replace("{"+"participant_id"+"}", "$participantId"),
            query = localVariableQuery,
            headers = localVariableHeaders
        )
        val localVarResponse = request<kotlin.Array<kotlin.Any>>(
            localVariableConfig,
            localVariableBody
        )

        return when (localVarResponse.responseType) {
            ResponseType.Success -> (localVarResponse as Success<*>).data as kotlin.Array<kotlin.Any>
            ResponseType.Informational -> throw UnsupportedOperationException("Client does not support Informational responses.")
            ResponseType.Redirection -> throw UnsupportedOperationException("Client does not support Redirection responses.")
            ResponseType.ClientError -> {
                val localVarError = localVarResponse as ClientError<*>
                throw ClientException("Client error : ${localVarError.statusCode} ${localVarError.message.orEmpty()}", localVarError.statusCode, localVarResponse)
            }
            ResponseType.ServerError -> {
                val localVarError = localVarResponse as ServerError<*>
                throw ServerException("Server error : ${localVarError.statusCode} ${localVarError.message.orEmpty()}", localVarError.statusCode, localVarResponse)
            }
        }
    }

    /**
    * Get all activity events for a researcher by participant.
    * Get the set of all activity events produced by participants of any  study conducted by a researcher, by researcher identifier.
    * @param researcherId  
    * @param origin  (optional)
    * @param from  (optional)
    * @param to  (optional)
    * @param transform  (optional)
    * @return kotlin.Array<kotlin.Any>
    * @throws UnsupportedOperationException If the API returns an informational or redirection response
    * @throws ClientException If the API returns a client error response
    * @throws ServerException If the API returns a server error response
    */
    @Suppress("UNCHECKED_CAST")
    @Throws(UnsupportedOperationException::class, ClientException::class, ServerException::class)
    fun activityEventAllByResearcher(researcherId: kotlin.String, origin: kotlin.String?, from: java.sql.Timestamp?, to: java.sql.Timestamp?, transform: kotlin.String?) : kotlin.Array<kotlin.Any> {
        val localVariableBody: kotlin.Any? = null
        val localVariableQuery: MultiValueMap = mutableMapOf<kotlin.String, List<kotlin.String>>()
            .apply {
                if (origin != null) {
                    put("origin", listOf(origin.toString()))
                }
                if (from != null) {
                    put("from", listOf(from.toString()))
                }
                if (to != null) {
                    put("to", listOf(to.toString()))
                }
                if (transform != null) {
                    put("transform", listOf(transform.toString()))
                }
            }
        val localVariableHeaders: MutableMap<String, String> = mutableMapOf()
        val localVariableConfig = RequestConfig(
            RequestMethod.GET,
            "/researcher/{researcher_id}/activity_event".replace("{"+"researcher_id"+"}", "$researcherId"),
            query = localVariableQuery,
            headers = localVariableHeaders
        )
        val localVarResponse = request<kotlin.Array<kotlin.Any>>(
            localVariableConfig,
            localVariableBody
        )

        return when (localVarResponse.responseType) {
            ResponseType.Success -> (localVarResponse as Success<*>).data as kotlin.Array<kotlin.Any>
            ResponseType.Informational -> throw UnsupportedOperationException("Client does not support Informational responses.")
            ResponseType.Redirection -> throw UnsupportedOperationException("Client does not support Redirection responses.")
            ResponseType.ClientError -> {
                val localVarError = localVarResponse as ClientError<*>
                throw ClientException("Client error : ${localVarError.statusCode} ${localVarError.message.orEmpty()}", localVarError.statusCode, localVarResponse)
            }
            ResponseType.ServerError -> {
                val localVarError = localVarResponse as ServerError<*>
                throw ServerException("Server error : ${localVarError.statusCode} ${localVarError.message.orEmpty()}", localVarError.statusCode, localVarResponse)
            }
        }
    }

    /**
    * Get all activity events for a study by participant.
    * Get the set of all activity events produced by participants of a  single study, by study identifier.
    * @param studyId  
    * @param origin  (optional)
    * @param from  (optional)
    * @param to  (optional)
    * @param transform  (optional)
    * @return kotlin.Array<kotlin.Any>
    * @throws UnsupportedOperationException If the API returns an informational or redirection response
    * @throws ClientException If the API returns a client error response
    * @throws ServerException If the API returns a server error response
    */
    @Suppress("UNCHECKED_CAST")
    @Throws(UnsupportedOperationException::class, ClientException::class, ServerException::class)
    fun activityEventAllByStudy(studyId: kotlin.String, origin: kotlin.String?, from: java.sql.Timestamp?, to: java.sql.Timestamp?, transform: kotlin.String?) : kotlin.Array<kotlin.Any> {
        val localVariableBody: kotlin.Any? = null
        val localVariableQuery: MultiValueMap = mutableMapOf<kotlin.String, List<kotlin.String>>()
            .apply {
                if (origin != null) {
                    put("origin", listOf(origin.toString()))
                }
                if (from != null) {
                    put("from", listOf(from.toString()))
                }
                if (to != null) {
                    put("to", listOf(to.toString()))
                }
                if (transform != null) {
                    put("transform", listOf(transform.toString()))
                }
            }
        val localVariableHeaders: MutableMap<String, String> = mutableMapOf()
        val localVariableConfig = RequestConfig(
            RequestMethod.GET,
            "/study/{study_id}/activity_event".replace("{"+"study_id"+"}", "$studyId"),
            query = localVariableQuery,
            headers = localVariableHeaders
        )
        val localVarResponse = request<kotlin.Array<kotlin.Any>>(
            localVariableConfig,
            localVariableBody
        )

        return when (localVarResponse.responseType) {
            ResponseType.Success -> (localVarResponse as Success<*>).data as kotlin.Array<kotlin.Any>
            ResponseType.Informational -> throw UnsupportedOperationException("Client does not support Informational responses.")
            ResponseType.Redirection -> throw UnsupportedOperationException("Client does not support Redirection responses.")
            ResponseType.ClientError -> {
                val localVarError = localVarResponse as ClientError<*>
                throw ClientException("Client error : ${localVarError.statusCode} ${localVarError.message.orEmpty()}", localVarError.statusCode, localVarResponse)
            }
            ResponseType.ServerError -> {
                val localVarError = localVarResponse as ServerError<*>
                throw ServerException("Server error : ${localVarError.statusCode} ${localVarError.message.orEmpty()}", localVarError.statusCode, localVarResponse)
            }
        }
    }

    /**
    * Create a new ActivityEvent for the given Participant.
    * Create a new ActivityEvent for the given Participant.
    * @param participantId  
    * @param activityEvent  
    * @return kotlin.String
    * @throws UnsupportedOperationException If the API returns an informational or redirection response
    * @throws ClientException If the API returns a client error response
    * @throws ServerException If the API returns a server error response
    */
    @Suppress("UNCHECKED_CAST")
    @Throws(UnsupportedOperationException::class, ClientException::class, ServerException::class)
    fun activityEventCreate(participantId: kotlin.String, activityEvent: ActivityEvent) : kotlin.String {
        val localVariableBody: kotlin.Any? = activityEvent
        val localVariableQuery: MultiValueMap = mutableMapOf()
        val localVariableHeaders: MutableMap<String, String> = mutableMapOf()
        val localVariableConfig = RequestConfig(
            RequestMethod.POST,
            "/participant/{participant_id}/activity_event".replace("{"+"participant_id"+"}", "$participantId"),
            query = localVariableQuery,
            headers = localVariableHeaders
        )
        val localVarResponse = request<kotlin.String>(
            localVariableConfig,
            localVariableBody
        )

        return when (localVarResponse.responseType) {
            ResponseType.Success -> (localVarResponse as Success<*>).data as kotlin.String
            ResponseType.Informational -> throw UnsupportedOperationException("Client does not support Informational responses.")
            ResponseType.Redirection -> throw UnsupportedOperationException("Client does not support Redirection responses.")
            ResponseType.ClientError -> {
                val localVarError = localVarResponse as ClientError<*>
                throw ClientException("Client error : ${localVarError.statusCode} ${localVarError.message.orEmpty()}", localVarError.statusCode, localVarResponse)
            }
            ResponseType.ServerError -> {
                val localVarError = localVarResponse as ServerError<*>
                throw ServerException("Server error : ${localVarError.statusCode} ${localVarError.message.orEmpty()}", localVarError.statusCode, localVarResponse)
            }
        }
    }

    /**
    * Delete a ActivityEvent.
    * Delete a ActivityEvent.
    * @param participantId  
    * @param origin  (optional)
    * @param from  (optional)
    * @param to  (optional)
    * @param transform  (optional)
    * @return kotlin.String
    * @throws UnsupportedOperationException If the API returns an informational or redirection response
    * @throws ClientException If the API returns a client error response
    * @throws ServerException If the API returns a server error response
    */
    @Suppress("UNCHECKED_CAST")
    @Throws(UnsupportedOperationException::class, ClientException::class, ServerException::class)
    fun activityEventDelete(participantId: kotlin.String, origin: kotlin.String?, from: java.sql.Timestamp?, to: java.sql.Timestamp?, transform: kotlin.String?) : kotlin.String {
        val localVariableBody: kotlin.Any? = null
        val localVariableQuery: MultiValueMap = mutableMapOf<kotlin.String, List<kotlin.String>>()
            .apply {
                if (origin != null) {
                    put("origin", listOf(origin.toString()))
                }
                if (from != null) {
                    put("from", listOf(from.toString()))
                }
                if (to != null) {
                    put("to", listOf(to.toString()))
                }
                if (transform != null) {
                    put("transform", listOf(transform.toString()))
                }
            }
        val localVariableHeaders: MutableMap<String, String> = mutableMapOf()
        val localVariableConfig = RequestConfig(
            RequestMethod.DELETE,
            "/participant/{participant_id}/activity_event".replace("{"+"participant_id"+"}", "$participantId"),
            query = localVariableQuery,
            headers = localVariableHeaders
        )
        val localVarResponse = request<kotlin.String>(
            localVariableConfig,
            localVariableBody
        )

        return when (localVarResponse.responseType) {
            ResponseType.Success -> (localVarResponse as Success<*>).data as kotlin.String
            ResponseType.Informational -> throw UnsupportedOperationException("Client does not support Informational responses.")
            ResponseType.Redirection -> throw UnsupportedOperationException("Client does not support Redirection responses.")
            ResponseType.ClientError -> {
                val localVarError = localVarResponse as ClientError<*>
                throw ClientException("Client error : ${localVarError.statusCode} ${localVarError.message.orEmpty()}", localVarError.statusCode, localVarResponse)
            }
            ResponseType.ServerError -> {
                val localVarError = localVarResponse as ServerError<*>
                throw ServerException("Server error : ${localVarError.statusCode} ${localVarError.message.orEmpty()}", localVarError.statusCode, localVarResponse)
            }
        }
    }

}
