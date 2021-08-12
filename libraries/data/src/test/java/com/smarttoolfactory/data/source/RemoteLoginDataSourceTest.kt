package com.smarttoolfactory.data.source

import com.google.common.truth.Truth
import com.smarttoolfactory.data.api.HopinApi
import com.smarttoolfactory.data.model.remote.request.SessionTokenRequest
import com.smarttoolfactory.data.model.remote.sso.SessionTokenDTO
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.net.UnknownHostException
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Wrong token or body returns retrofit2.HttpException: HTTP 401
 *
 * Not having connection returns java.net.UnknownHostException:
 * Unable to resolve host "hopin.com": No address associated with hostname
 */
internal class RemoteLoginDataSourceTest {

    private val cookie =
        "user.token=QDla%2Fin5Ryv071eziBpHb56KNwQQQdROaealpQHGZHvBxRKe%2FwZwgUFbGz" +
            "ks3OaJRs%2BWWNSZybMwgDKNuJeX5rnwr7OggNXPX5w%3D--XxJELxpUNISUuZl6--" +
            "Rts4nWVmI4uJCKgVDnyT%2Bw%3D%3D"

    private val sessionToken =
        "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI3MmVhYjBlNy05ZGIyLTRmNmEtOTQyMC1hNDY4YjQzYzc" +
            "zZDgiLCJzdWIiOjIxOTY5OTAsInBlcnNvbmFfaWQiOjMyNDUxMSwicmVnaXN0cmF0aW9u" +
            "X2lkIjo1Mzc0NzA5LCJldmVudF9pZCI6MTA4NTY0LCJyb2xlIjoib3JnYW5pc2VyIiwibXV" +
            "sdGlwbGVfY29ubiI6dHJ1ZSwiZGF0YV9zZWdyZWdhdGVkIjpmYWxzZX0.AAhrVXd5LYYy6YR" +
            "eFCN3hAc7e9d4z0FltcmPt_YdesY"

    private val request = SessionTokenRequest("hopincon2022")

    private val api = mockk<HopinApi>()

    private lateinit var remoteDataSource: RemoteLoginDataSource

    @Test
    fun `given error occurred, should throw Exception`() = runBlockingTest {

        // GIVEN
        val errorMessages =
            "Unable to resolve host \"hopin.com\": No address associated with hostname"
        val request = SessionTokenRequest("hopincon2022")
        coEvery { api.getSessionToken(any(), any()) } throws UnknownHostException(errorMessages)

        // WHEN
        val expected = try {
            remoteDataSource.getSessionTokenDTO(cookie, request)
        } catch (e: Exception) {
            e
        }

        // THEN
        Truth.assertThat(expected).isInstanceOf(UnknownHostException::class.java)
        coVerify(exactly = 1) { api.getSessionToken(cookie, request) }
    }

    @Test
    fun `given Http 200, should return sessionTokenDTO`() = runBlockingTest {

        // GIVEN
        val actual = SessionTokenDTO(sessionToken)
        coEvery { remoteDataSource.getSessionTokenDTO(cookie, request) } returns actual

        // WHEN
        val expected = remoteDataSource.getSessionTokenDTO(cookie, request)

        // THEN
        Truth.assertThat(expected).isEqualTo(actual)
        coVerify(exactly = 1) { remoteDataSource.getSessionTokenDTO(cookie, request) }
    }

    @Before
    fun setUp() {
        remoteDataSource = RemoteLoginDataSourceIml(api)
    }

    @After
    fun tearDown() {
        clearMocks(api)
    }
}
