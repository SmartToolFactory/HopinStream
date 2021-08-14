package com.smarttoolfactory.domain.mapper

interface ConnectivityManager {

    /**
     * Chek if user is connected to internet
     */
    fun isConnected(): Boolean
}
