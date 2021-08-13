package com.smarttoolfactory.domain.error

sealed class HopinError {
    /**
     * There is no internet connection
     */
    class InternetConnectionError(exception: Exception, message: String? = exception.message) :
        HopinError()

    /**
     * User not logged in, there is no valid(there not exist one or expired) session token
     */
    class LoginError(exception: Exception, message: String? = exception.message) : HopinError()

    /**
     * There is available stage for a specific event id
     */
    class StageAvailabilityError(exception: Exception, message: String? = exception.message)
}


