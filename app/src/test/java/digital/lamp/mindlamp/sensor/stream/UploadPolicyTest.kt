package digital.lamp.mindlamp.sensor.stream

import org.junit.Assert.assertEquals
import org.junit.Test

class UploadPolicyTest {

    @Test
    fun followsContractResponseTable() {
        val table = mapOf(
            200 to UploadAction.DELETE,
            // The contract deletes only on 200; any other status keeps the frame for a retry.
            201 to UploadAction.RETRY_LATER,
            202 to UploadAction.RETRY_LATER,
            204 to UploadAction.RETRY_LATER,
            409 to UploadAction.QUARANTINE,
            400 to UploadAction.QUARANTINE,
            413 to UploadAction.QUARANTINE,
            401 to UploadAction.RETRY_LATER,
            403 to UploadAction.RETRY_LATER,
            500 to UploadAction.RETRY_LATER,
            502 to UploadAction.RETRY_LATER,
            503 to UploadAction.RETRY_LATER,
            504 to UploadAction.RETRY_LATER,
            429 to UploadAction.RETRY_LATER,
            404 to UploadAction.RETRY_LATER
        )
        table.forEach { (code, action) -> assertEquals("HTTP $code", action, UploadPolicy.actionFor(code)) }
    }

    @Test
    fun networkErrorRetries() {
        assertEquals(UploadAction.RETRY_LATER, UploadPolicy.actionFor(null))
    }
}
