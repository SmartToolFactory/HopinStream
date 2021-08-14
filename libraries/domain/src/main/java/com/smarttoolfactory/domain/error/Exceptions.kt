package com.smarttoolfactory.domain.error

import java.io.IOException

class NoConnectivityException : IOException()

class TokenNotAvailableException : RuntimeException()

/**
 * Occurs when there is no stage available
 */
class StageNotAvailableException(message: String?) : RuntimeException(message)
class InActiveBroadcastException(message: String?) : RuntimeException(message)
