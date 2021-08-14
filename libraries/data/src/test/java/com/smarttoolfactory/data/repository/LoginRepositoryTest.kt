package com.smarttoolfactory.data.repository

import com.google.common.truth.Truth
import com.smarttoolfactory.data.mapper.SessionTokenMapper
import com.smarttoolfactory.data.model.local.SessionTokenEntity
import com.smarttoolfactory.data.model.remote.request.SessionTokenRequest
import com.smarttoolfactory.data.model.remote.sso.SessionTokenDTO
import com.smarttoolfactory.data.source.LocalLoginDataSource
import com.smarttoolfactory.data.source.RemoteLoginDataSource
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerifyOrder
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Test

// TODO Didn't write other tests deliberately, they are basically same as datasource tests
class LoginRepositoryTest {

    private lateinit var repository: LoginRepository

    private val localDataSource: LocalLoginDataSource = mockk()
    private val remoteDataSource: RemoteLoginDataSource = mockk()
    private val mapper: SessionTokenMapper = mockk()

    private val cookie =
        "user.token=QDla%2Fin5Ryv071eziBpHb56KNwQQQdROaealpQHGZHvBxRKe%2FwZwgU" +
            "FbGzks3OaJRs%2BWWNSZybMwgDKNuJeX5rnwr7OggNXPX5w%3D--XxJELxpUNIS" +
            "UuZl6--Rts4nWVmI4uJCKgVDnyT%2Bw%3D%3D"

    private val sessionToken =
        "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI3MmVhYjBlNy05ZGIyLTRmNmEtOTQyMC1hNDY4Y" +
            "jQzYzczZDgiLCJzdWIiOjIxOTY5OTAsInBlcnNvbmFfaWQiOjMyNDUxMSwicm" +
            "VnaXN0cmF0aW9uX2lkIjo1Mzc0NzA5LCJldmVudF9pZCI6MTA4NTY0LCJyb2xl" +
            "Ijoib3JnYW5pc2VyIiwibXVsdGlwbGVfY29ubiI6dHJ1ZSwiZGF0YV9zZWdyZWd" +
            "hdGVkIjpmYWxzZX0.AAhrVXd5LYYy6YReFCN3hAc7e9d4z0FltcmPt_YdesY"

    private val request = SessionTokenRequest("hopincon2022")

    @Test
    fun `given json returned should have a session token entity`() = runBlockingTest {

        // GIVEN
        val sessionTokenDTO = SessionTokenDTO(sessionToken)
        val sessionTokenEntity = SessionTokenEntity(sessionToken, System.currentTimeMillis())

        coEvery { remoteDataSource.getSessionTokenDTO(cookie, request) } returns sessionTokenDTO
        every { mapper.map(sessionTokenDTO) } returns sessionTokenEntity

        // WHEN
        val sessionToken: SessionTokenEntity =
            repository.fetchSessionTokenFromRemote(cookie, request)

        // THEN
        Truth.assertThat(sessionToken).isEqualTo(sessionTokenEntity)

        // Verify that we got token from remote and mapped it into entity object
        coVerifyOrder {
            remoteDataSource.getSessionTokenDTO(cookie, request)
            mapper.map(sessionTokenDTO)
        }
    }

    @Before
    fun setUp() {
        repository = LoginRepositoryImpl(remoteDataSource, localDataSource, mapper)
    }

    @After
    fun tearDown() {
        clearMocks(localDataSource, remoteDataSource, mapper)
    }
}
