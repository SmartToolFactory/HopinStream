package com.smarttoolfactory.data.api

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import com.smarttoolfactory.test_utils.rule.TestCoroutineRule
import java.net.HttpURLConnection
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// TODO MockWebserver does not work properly with coroutines
// Check out my question here:
// https://stackoverflow.com/questions/62161708/suspending-function-test-with-mockwebserver

class HopinApiTest : AbstractApiTest() {

    companion object {

        @get:Rule
        val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

        @get:Rule
        val testCoroutineRule = TestCoroutineRule()
    }

    /**
     * Api is the SUT to test headers, url, response and DTO objects
     */
    private lateinit var api: HopinApi

    /**
     * ❌ This test FAILS, with launch builder sometimes fail, sometimes PASSes
     */
    @Test
    fun `Given a valid session token request, should be done to correct url with valid headers`() =
        testCoroutineRule.runBlockingTest {

            // GIVEN
            withContext(coroutineContext) {
                enqueueResponse(
                    code = HttpURLConnection.HTTP_OK,
                    headers = hashMapOf(
                        "Content-Type" to "application/json",
                        "Cookie" to cookie
                    )
                )
            }

            // WHEN
//                api.getSessionToken(cookie, request)
            launch(coroutineContext) {
                api.getSessionToken(cookie, request)
            }
            advanceUntilIdle()

//            launch(testCoroutineScope.coroutineContext) {
            val request = withContext(coroutineContext) {
                mockWebServer.takeRequest(1, TimeUnit.SECONDS)
            }

            // THEN
            Truth.assertThat(request?.path).isEqualTo("/users/sso")
        }

    /**
     * ❌ This test FAILS, with launch builder sometimes fail, sometimes PASSes
     */
    @ExperimentalCoroutinesApi
    @Test
    fun `Given api returns 200, should return session token`() =
        testCoroutineRule.runBlockingTest {

            // GIVEN

            println("testCoroutineRule.runBlockingTest() in thread: ${Thread.currentThread().name}")

            enqueueResponse(
                HttpURLConnection.HTTP_OK,
                headers = hashMapOf(
                    "Content-Type" to "application/json",
                    "Cookie" to cookie
                )
            )
            delay(2000)

            // WHEN
            val sessionToken = api.getSessionToken(cookie, request)

//            var sessionToken: SessionTokenDTO? = null
//
//            launch(coroutineContext) {
//                sessionToken = api.getSessionToken(cookie, request)
//            }
//
//            advanceUntilIdle()
            println("Session token: $sessionToken")

            // THEN
            Truth.assertThat(sessionToken).isNotNull()
        }

    @Before
    override fun setUp() {
        super.setUp()

        val okHttpClient = OkHttpClient
            .Builder()
            .build()

        api = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(HopinApi::class.java)
    }

    @After
    override fun tearDown() {
        super.tearDown()
    }
}
