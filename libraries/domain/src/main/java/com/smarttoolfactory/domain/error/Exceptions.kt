package com.smarttoolfactory.domain.error

import java.io.IOException

class NoConnectivityException(message: String? = null) : IOException()

class TokenNotAvailableException(message: String? = null) : RuntimeException()

/**
 * Occurs when there is no stage available
 */
class StageNotAvailableException(message: String? = null) : RuntimeException(message)

/**
 * Occurs when there is no active broadcast available
 */
class InActiveBroadcastException(message: String? = null) : RuntimeException(message)
