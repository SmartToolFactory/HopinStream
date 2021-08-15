package com.smarttoolfactory.core.hopin

import android.util.Base64
import com.smarttoolfactory.domain.mapper.JWTDecoder
import java.io.UnsupportedEncodingException
import javax.inject.Inject

class JWTDecoderImpl @Inject constructor() : JWTDecoder {

    override fun decodeTokenToEventId(jwt: String): Long {
        return try {
            val payload = jwt.split("\\.".toRegex()).toTypedArray()[1]
            val decodedPayload = decodeFromBase64(payload)

            val eventIdStartIndex = decodedPayload.indexOf("\"event_id\":")

            decodedPayload.substring(
                eventIdStartIndex,
                decodedPayload.indexOf(",", eventIdStartIndex)
            )
                .removePrefix("\"event_id\":")
                .toLong()
        } catch (e: Exception) {
            throw UnsupportedEncodingException(e.message)
        }
    }

    private fun decodeFromBase64(strEncoded: String): String {
        val decodedBytes: ByteArray = Base64.decode(strEncoded, Base64.URL_SAFE)
        return String(decodedBytes, Charsets.UTF_8)
    }
}
