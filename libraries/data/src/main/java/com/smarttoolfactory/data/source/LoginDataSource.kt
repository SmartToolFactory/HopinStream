package com.smarttoolfactory.data.source

import com.smarttoolfactory.data.api.HopinApi
import com.smarttoolfactory.data.db.dao.SessionTokenDao
import com.smarttoolfactory.data.model.local.SessionTokenEntity
import com.smarttoolfactory.data.model.remote.request.SessionTokenRequest
import com.smarttoolfactory.data.model.remote.sso.SessionTokenDTO
import javax.inject.Inject

/**
 * Interface for login process
 */
interface LoginDataSource

interface RemoteLoginDataSource : LoginDataSource {
    suspend fun getSessionTokenDTO(
        cookie: String,
        eventSlug: SessionTokenRequest
    ): SessionTokenDTO
}

class RemoteLoginDataSourceIml @Inject constructor(private val hopinApi: HopinApi) :
    RemoteLoginDataSource {

    override suspend fun getSessionTokenDTO(
        cookie: String,
        eventSlug: SessionTokenRequest
    ) = hopinApi.getSessionToken(cookie, eventSlug)
}


interface LocalLoginDataSource : LoginDataSource {
    suspend fun getSessionTokeEntity(): SessionTokenEntity?
    suspend fun saveSessionTokenEntity(sessionTokenEntity: SessionTokenEntity): Long
    suspend fun deleteSessionToken()
}

class LocalLoginDataSourceIml @Inject constructor(private val sessionTokenDao: SessionTokenDao) :
    LocalLoginDataSource {
    override suspend fun getSessionTokeEntity(): SessionTokenEntity? =
        sessionTokenDao.getSessionToken()

    override suspend fun saveSessionTokenEntity(sessionTokenEntity: SessionTokenEntity): Long =
        sessionTokenDao.insert(sessionTokenEntity)

    override suspend fun deleteSessionToken() = sessionTokenDao.deleteAll()

}

