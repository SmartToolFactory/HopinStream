package com.smarttoolfactory.data.api

import com.smarttoolfactory.data.model.remote.broadcast.Stages
import com.smarttoolfactory.data.model.remote.request.SessionTokenRequest
import com.smarttoolfactory.myapplication.model.broadcast.StageWithStatus
import com.smarttoolfactory.test_utils.RESPONSE_STAGES_JSON_PATH
import com.smarttoolfactory.test_utils.RESPONSE_STAGE_WITH_STATUS_JSON_PATH
import com.smarttoolfactory.test_utils.util.convertToObjectFromJson
import com.smarttoolfactory.test_utils.util.getResourceAsText
import java.io.IOException
import java.net.HttpURLConnection
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.QueueDispatcher
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Before

// TODO JUnit5 returns error Test events were not received with Arctic Fox 2020.3.1

// TODO MockWebServer still buggy with coroutines
// Check out my question here:
// https://stackoverflow.com/questions/62161708/suspending-function-test-with-mockwebserver

/**
 * Abstract class fo testing api with [MockWebServer] and [JUnit5]
 */
abstract class AbstractApiTest {

    /*
        Responses as String for setting response body for MockWebServer
     */
    lateinit var mockWebServer: MockWebServer

    internal val responseStages: String by lazy {
        getResourceAsText(RESPONSE_STAGES_JSON_PATH)
    }

    internal val responseStageWithStatus: String by lazy {
        getResourceAsText(RESPONSE_STAGE_WITH_STATUS_JSON_PATH)
    }

    internal val cookie =
        "user.token=QDla%2Fin5Ryv071eziBpHb56KNwQQQdROaealpQHGZHvBxRKe%2FwZwgU" +
            "FbGzks3OaJRs%2BWWNSZybMwgDKNuJeX5rnwr7OggNXPX5w%3D--XxJELxpUNIS" +
            "UuZl6--Rts4nWVmI4uJCKgVDnyT%2Bw%3D%3D"

    internal val sessionToken =
        "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI3MmVhYjBlNy05ZGIyLTRmNmEtOTQyMC1hNDY4Y" +
            "jQzYzczZDgiLCJzdWIiOjIxOTY5OTAsInBlcnNvbmFfaWQiOjMyNDUxMSwicm" +
            "VnaXN0cmF0aW9uX2lkIjo1Mzc0NzA5LCJldmVudF9pZCI6MTA4NTY0LCJyb2xl" +
            "Ijoib3JnYW5pc2VyIiwibXVsdGlwbGVfY29ubiI6dHJ1ZSwiZGF0YV9zZWdyZWd" +
            "hdGVkIjpmYWxzZX0.AAhrVXd5LYYy6YReFCN3hAc7e9d4z0FltcmPt_YdesY"

    internal val request = SessionTokenRequest("hopincon2022")

    /*
        DTO list of REST response
     */
    val stages by lazy {
        convertToObjectFromJson<Stages>(
            getResourceAsText(RESPONSE_STAGES_JSON_PATH)
        )!!
    }

    val stageWithStages by lazy {
        convertToObjectFromJson<StageWithStatus>(
            getResourceAsText(RESPONSE_STAGE_WITH_STATUS_JSON_PATH)
        )!!
    }

    @Before
    open fun setUp() {

        mockWebServer = MockWebServer()
            .apply {
                start()
//               dispatcher = ResponseDispatcher()
            }
    }

    @After
    open fun tearDown() {
        mockWebServer.shutdown()
    }

    @Throws(IOException::class)
    fun enqueueResponse(
        code: Int = 200,
        headers: Map<String, String>? = null,
        responseBody: String = sessionToken
    ): MockResponse {

        // Define mock response
        val mockResponse = MockResponse()
        // Set response code
        mockResponse.setResponseCode(code)

        // Set headers
        headers?.let {
            for ((key, value) in it) {
                mockResponse.addHeader(key, value)
            }
        }

        // Set response
        mockWebServer.enqueue(mockResponse.setBody(responseBody))
        println(
            "🍏 enqueueResponse() in thread: ${Thread.currentThread().name}," +
                " ${sessionToken.length} $mockResponse"
        )
        return mockResponse
    }

    /**
     * Dispatcher to return different responses for each endpoint
     */

    inner class ResponseDispatcher : QueueDispatcher() {

        override fun dispatch(request: RecordedRequest): MockResponse {

            return when {

                // Stages
                request.path!!.contains(regex = "api/v2/events/\\d+/stages".toRegex()) -> {
                    enqueueResponse(
                        HttpURLConnection.HTTP_OK,
                        responseBody = responseStages
                    )
                }

                // Stage with status
                request.path!!
                    .contains(regex = "api/v2/events/\\d+/studio/\\.+/status".toRegex()) -> {
                    enqueueResponse(
                        HttpURLConnection.HTTP_OK,
                        responseBody = responseStageWithStatus
                    )
                }

                else -> {
                    enqueueResponse(code = HttpURLConnection.HTTP_OK)
                }
            }
        }
    }
}
