package com.smarttoolfactory.domain.mapper

import java.io.UnsupportedEncodingException

interface JWTDecoder {
    @Throws(UnsupportedEncodingException::class)
    fun decodeTokenToEventId(jwt: String): Long
}
