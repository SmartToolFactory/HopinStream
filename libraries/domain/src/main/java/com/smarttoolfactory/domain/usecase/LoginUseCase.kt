package com.smarttoolfactory.domain.usecase

import com.smarttoolfactory.data.constant.TOKEN_REFRESH_INTERVAL
import com.smarttoolfactory.data.model.remote.request.SessionTokenRequest
import com.smarttoolfactory.data.repository.LoginRepository
import com.smarttoolfactory.domain.dispatcher.UseCaseDispatchers
import com.smarttoolfactory.domain.error.TokenNotAvailableException
import com.smarttoolfactory.domain.mapper.JWTDecoder
import com.smarttoolfactory.domain.model.UserSession
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class LoginUseCase @Inject constructor(
    private val repository: LoginRepository,
    private val dispatcherProvider: UseCaseDispatchers,
    private val jwtDecoder: JWTDecoder
) {

    /**
     * Chek if there is a session token that hasn't expired
     */
    /**
     * Chek if there is a session token that hasn't expired
     */
    fun getUserSession(): Flow<UserSession> {

        return flow {
            emit(repository.fetchSessionTokenFromLocal())
        }
            .map { sessionTokenEntity ->
                // valid token in db, use it
                if (sessionTokenEntity == null ||
                    System.currentTimeMillis() - sessionTokenEntity.fetchDate
                > TOKEN_REFRESH_INTERVAL
                ) {
                    throw TokenNotAvailableException()
                } else {
                    val token = sessionTokenEntity.token
                    val eventId = jwtDecoder.decodeTokenToEventId(token)
                    UserSession(sessionToken = token, eventId)
                }
            }
            .flowOn(dispatcherProvider.defaultDispatcher)
    }

    /**
     * Get session token from remote server via REST api
     */
    fun createUserSession(
        cookie: String,
        eventSlug: SessionTokenRequest
    ): Flow<UserSession> {
        return flow {
            emit(repository.fetchSessionTokenFromRemote(cookie, eventSlug))
        }
            .map { sessionTokenEntity ->

                val token = sessionTokenEntity.token
                // Decode eventId from session token
                val eventId = jwtDecoder.decodeTokenToEventId(token)

                repository.deleteSessionToken()
                repository.saveSessionToken(sessionTokenEntity)

                UserSession(sessionToken = token, eventId)
            }
            .flowOn(dispatcherProvider.defaultDispatcher)
    }
}
