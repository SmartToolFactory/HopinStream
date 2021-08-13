package com.smarttoolfactory.data.repository

import com.smarttoolfactory.data.mapper.SessionTokenMapper
import com.smarttoolfactory.data.model.local.SessionTokenEntity
import com.smarttoolfactory.data.model.remote.request.SessionTokenRequest
import com.smarttoolfactory.data.model.remote.sso.SessionTokenDTO
import com.smarttoolfactory.data.source.LocalLoginDataSource
import com.smarttoolfactory.data.source.RemoteLoginDataSource
import javax.inject.Inject

interface LoginRepository {

    // Local operations
    suspend fun fetSessionTokenFromLocal(
        cookie: String,
        eventSlug: SessionTokenRequest
    ): SessionTokenEntity?

    suspend fun saveSessionToken(sessionTokenEntity: SessionTokenEntity): Long
    suspend fun deleteSessionToken()

    // Remote operation
    suspend fun fetSessionTokenFromRemote(
        cookie: String,
        eventSlug: SessionTokenRequest
    ): SessionTokenEntity
}

/**
 * Login repository is responsible of getting session token from remote source, or
 * local db source and execute local db operations to have fresh session token
 */
class LoginRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteLoginDataSource,
    private val localDataSource: LocalLoginDataSource,
    private val mapper: SessionTokenMapper
) : LoginRepository {

    override suspend fun fetSessionTokenFromRemote(
        cookie: String,
        eventSlug: SessionTokenRequest
    ): SessionTokenEntity {
        val sessionToken: SessionTokenDTO = remoteDataSource.getSessionTokenDTO(cookie, eventSlug)
        return mapper.map(sessionToken)
    }

    override suspend fun fetSessionTokenFromLocal(
        cookie: String,
        eventSlug: SessionTokenRequest
    ): SessionTokenEntity? {
        return localDataSource.getSessionTokeEntity()
    }

    override suspend fun saveSessionToken(sessionTokenEntity: SessionTokenEntity): Long {
        return localDataSource.saveSessionTokenEntity(sessionTokenEntity)
    }

    override suspend fun deleteSessionToken() {
        localDataSource.deleteSessionToken()
    }
}
