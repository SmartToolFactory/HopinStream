package com.smarttoolfactory.domain.usecase

import com.smarttoolfactory.data.model.remote.request.SessionTokenRequest
import com.smarttoolfactory.data.repository.LoginRepository
import com.smarttoolfactory.domain.dispatcher.UseCaseDispatchers
import com.smarttoolfactory.domain.mapper.ConnectivityManager
import com.smarttoolfactory.domain.mapper.JWTDecoder
import com.smarttoolfactory.domain.model.UserSession
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class LoginUseCase @Inject constructor(
    private val repository: LoginRepository,
    private val dispatcherProvider: UseCaseDispatchers,
    private val jwtDecoder: JWTDecoder,
    private val connectivityManager: ConnectivityManager
) {

    /**
     * Chek if there is a session token that hasn't expired
     */
    fun getUserSession(): Flow<UserSession> {
        TODO()
    }

    /**
     * Get session token from remote server via REST api
     */
    fun createUserSession(
        cookie: String,
        eventSlug: SessionTokenRequest
    ): Flow<UserSession> {
        TODO()
    }
}
