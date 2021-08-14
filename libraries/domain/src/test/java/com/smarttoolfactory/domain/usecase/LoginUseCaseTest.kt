package com.smarttoolfactory.domain.usecase

import com.smarttoolfactory.data.constant.TOKEN_REFRESH_INTERVAL
import com.smarttoolfactory.data.model.local.SessionTokenEntity
import com.smarttoolfactory.data.model.remote.request.SessionTokenRequest
import com.smarttoolfactory.data.repository.LoginRepository
import com.smarttoolfactory.domain.dispatcher.UseCaseDispatchers
import com.smarttoolfactory.domain.error.TokenNotAvailableException
import com.smarttoolfactory.domain.mapper.JWTDecoder
import com.smarttoolfactory.test_utils.rule.TestCoroutineRule
import com.smarttoolfactory.test_utils.test_observer.test
import io.mockk.Runs
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifySequence
import io.mockk.just
import io.mockk.mockk
import java.io.UnsupportedEncodingException
import kotlinx.coroutines.Dispatchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * This part is written with TDD
 *
 * Possible scenarios for login process
 *
 * 1  Token is available in local db but expired
 * 2. Token is not available in local db, fetched from remote source but parse error occurred
 * 3. Given user logged in and has session token parse session token
 * return event id and session token
 */
class LoginUseCaseTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private lateinit var loginUseCase: LoginUseCase

    private val repository: LoginRepository = mockk()
    private val jwtDecoder: JWTDecoder = mockk()

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
    fun `given token in db but expired should throw TokenNotAvailableException`() =
        testCoroutineRule.runBlockingTest {
            // GIVEN
            // One minute passed after token refresh time
            val sessionTokenEntity = SessionTokenEntity(
                sessionToken,
                System.currentTimeMillis() - TOKEN_REFRESH_INTERVAL - 60_000
            )
            coEvery { repository.fetchSessionTokenFromLocal() } returns sessionTokenEntity

            // WHEN
            val testObserver = loginUseCase.getUserSession().test(this)

            // THEN
            testObserver
                .assertNotComplete()
                .assertError(TokenNotAvailableException::class.java)
                .dispose()

            coVerify(exactly = 1) { repository.fetchSessionTokenFromLocal() }
            coVerify(exactly = 0) { jwtDecoder.decodeTokenToEventId(any()) }
        }

    @Test
    fun `given token in db not expired should return UserSession`() =
        testCoroutineRule.runBlockingTest {
            // GIVEN
            val sessionTokenEntity = SessionTokenEntity(sessionToken, System.currentTimeMillis())
            val eventId = 108564L

            coEvery { repository.fetchSessionTokenFromLocal() } returns sessionTokenEntity
            coEvery { jwtDecoder.decodeTokenToEventId(sessionToken) } returns eventId

            // WHEN
            val testObserver = loginUseCase.getUserSession().test(this)

            // THEN
            testObserver
                .assertComplete()
                .assertValue { userSession ->
                    userSession.sessionToken == sessionTokenEntity.token &&
                        userSession.evenId == eventId
                }
                .dispose()

            // Verify that these functions called in this order
            coVerifySequence {
                repository.fetchSessionTokenFromLocal()
                jwtDecoder.decodeTokenToEventId(sessionToken)
            }
        }

    @Test
    fun `given token fetched from remote should delete old token save and return new token`() =
        testCoroutineRule.runBlockingTest {

            // GIVEN

            val sessionTokenEntity = SessionTokenEntity(sessionToken, System.currentTimeMillis())
            val eventId = 108564L
            coEvery {
                repository.fetchSessionTokenFromRemote(cookie, request)
            } returns sessionTokenEntity

            coEvery { jwtDecoder.decodeTokenToEventId(sessionToken) } returns eventId
            coEvery { repository.deleteSessionToken() } just Runs
            coEvery { repository.saveSessionToken(sessionTokenEntity) } returns 1

            // WHEN
            val testObserver = loginUseCase.createUserSession(cookie, request).test(this)

            // THEN
            testObserver
                .assertComplete()
                .assertValue { userSession ->
                    userSession.sessionToken == sessionTokenEntity.token &&
                        userSession.evenId == eventId
                }
                .dispose()

            coVerifySequence {
                repository.fetchSessionTokenFromRemote(cookie, request)
                jwtDecoder.decodeTokenToEventId(sessionToken)
                repository.deleteSessionToken()
                repository.saveSessionToken(sessionTokenEntity)
            }
        }

    @Test
    fun `given UnsupportedEncodingException occurred while parsing should throw this exception`() =
        testCoroutineRule.runBlockingTest {

            // GIVEN

            val sessionTokenEntity = SessionTokenEntity(sessionToken, System.currentTimeMillis())
            val eventId = 108564L
            coEvery {
                repository.fetchSessionTokenFromRemote(cookie, request)
            } returns sessionTokenEntity

            coEvery {
                jwtDecoder.decodeTokenToEventId(sessionToken)
            } throws UnsupportedEncodingException()

            coEvery { repository.deleteSessionToken() } just Runs
            coEvery { repository.saveSessionToken(sessionTokenEntity) } returns 1

            // WHEN
            val testObserver = loginUseCase.createUserSession(cookie, request).test(this)

            // THEN
            testObserver
                .assertNotComplete()
                .assertError(UnsupportedEncodingException::class.java)
                .dispose()

            coVerifySequence {
                repository.fetchSessionTokenFromRemote(cookie, request)
                jwtDecoder.decodeTokenToEventId(sessionToken)
            }

            coVerify(exactly = 0) { repository.deleteSessionToken() }
            coVerify(exactly = 0) { repository.saveSessionToken(sessionTokenEntity) }
        }

    @Before
    fun setUp() {
        loginUseCase = LoginUseCase(repository, dispatcherProvider, jwtDecoder)
    }

    @After
    fun tearDown() {
        clearMocks(repository, jwtDecoder)
    }
}
