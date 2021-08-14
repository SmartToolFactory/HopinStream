package com.smarttoolfactory.domain.usecase

import com.google.common.truth.Truth
import com.smarttoolfactory.data.model.remote.request.SessionTokenRequest
import com.smarttoolfactory.data.repository.LoginRepository
import com.smarttoolfactory.domain.dispatcher.UseCaseDispatchers
import com.smarttoolfactory.domain.error.NoConnectivityException
import com.smarttoolfactory.domain.mapper.ConnectivityManager
import com.smarttoolfactory.domain.mapper.JWTDecoder
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * This part is written with TDD
 *
 * Possible scenarios for login process
 *
 * 1. There is no internet connection
 * 2  Token is available in local db but expired
 * 3. Token is not available in local db, fetched from remote source but parse error occurred
 * 4. Given user logged in and has session token parse session token
 * return event id and session token
 */
class LoginUseCaseTest {

    private lateinit var loginUseCase: LoginUseCase

    private val repository: LoginRepository = mockk()
    private val jwtDecoder: JWTDecoder = mockk()
    private val connectivityManager: ConnectivityManager = mockk()

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

    private val dispatcherProvider: UseCaseDispatchers =
        UseCaseDispatchers(Dispatchers.Main, Dispatchers.Main, Dispatchers.Main)

    @Test
    fun `given no internet connection should throw NoConnectivityException`() {
        // GIVEN
        coEvery { connectivityManager.isConnected() } returns false

        // WHEN
        val expected = try {
            loginUseCase.getUserSession()
        } catch (e: NoConnectivityException) {
            e
        }
        // THEN
        Truth.assertThat(expected).isInstanceOf(NoConnectivityException::class.java)

        // Verify that stages for valid event is not called
        coVerify(exactly = 0) { jwtDecoder.decodeTokenToEventId(any()) }
        coVerify(exactly = 0) { repository.fetchSessionTokenFromLocal() }
    }

    @Before
    fun setUp() {
        loginUseCase = LoginUseCase(repository, dispatcherProvider, jwtDecoder, connectivityManager)
    }

    @After
    fun tearDown() {
        clearMocks(repository, jwtDecoder, connectivityManager)
    }
}
