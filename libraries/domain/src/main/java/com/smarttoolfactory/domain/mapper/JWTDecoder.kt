package com.smarttoolfactory.domain.mapper

interface JWTDecoder {
    fun decodeTokenToEventId(jwt: String): Long
}
