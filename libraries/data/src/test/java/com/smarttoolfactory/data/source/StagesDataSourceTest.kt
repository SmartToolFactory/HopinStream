package com.smarttoolfactory.data.source

import com.google.common.truth.Truth
import com.smarttoolfactory.data.api.HopinApi
import com.smarttoolfactory.data.model.remote.broadcast.Stages
import com.smarttoolfactory.myapplication.model.broadcast.StageWithStatus
import com.smarttoolfactory.test_utils.RESPONSE_STAGES_JSON_PATH
import com.smarttoolfactory.test_utils.RESPONSE_STAGE_WITH_STATUS_JSON_PATH
import com.smarttoolfactory.test_utils.util.convertToObjectFromJson
import com.smarttoolfactory.test_utils.util.getResourceAsText
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runBlockingTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class StagesDataSourceTest {

    private val cookie =
        "user.token=QDla%2Fin5Ryv071eziBpHb56KNwQQQdROaealpQHGZHvBxRKe%2FwZwgUFbGzk" +
            "s3OaJRs%2BWWNSZybMwgDKNuJeX5rnwr7OggNXPX5w%3D--XxJELxpUNISUuZl6--Rt" +
            "s4nWVmI4uJCKgVDnyT%2Bw%3D%3D"

    private val sessionToken =
        "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI3MmVhYjBlNy05ZGIyLTRmNmEtOTQyMC1hNDY4YjQz" +
            "YzczZDgiLCJzdWIiOjIxOTY5OTAsInBlcnNvbmFfaWQiOjMyNDUxMSwicmVnaXN0cmF0" +
            "aW9uX2lkIjo1Mzc0NzA5LCJldmVudF9pZCI6MTA4NTY0LCJyb2xlIjoib3JnYW5pc2VyI" +
            "iwibXVsdGlwbGVfY29ubiI6dHJ1ZSwiZGF0YV9zZWdyZWdhdGVkIjpmYWxzZX0.AAh" +
            "rVXd5LYYy6YReFCN3hAc7e9d4z0FltcmPt_YdesY"

    private val api = mockk<HopinApi>()

    private lateinit var stagesDataSource: StagesDataSource

    private val stages by lazy {
        convertToObjectFromJson<Stages>(
            getResourceAsText(RESPONSE_STAGES_JSON_PATH)
        )!!
    }

    private val stageWithStatus by lazy {
        convertToObjectFromJson<StageWithStatus>(
            getResourceAsText(RESPONSE_STAGE_WITH_STATUS_JSON_PATH)
        )!!
    }

    @Test
    fun `given eventId is wrong, should throw HttpException with 403 code`() = runBlockingTest {

        // GIVEN
        val errorMessage = "HTTP 403"
        val eventId = 108564L
        coEvery { api.getStages(sessionToken, eventId + 5) } throws
            HttpException(Response.error<Stages>(403, errorMessage.toResponseBody()))

        // WHEN
        val expected = try {
            stagesDataSource.getStages(sessionToken, eventId + 5)
        } catch (e: HttpException) {
            e
        }

        // THEN
        Truth.assertThat(expected).isInstanceOf(HttpException::class.java)
        Truth.assertThat((expected as HttpException).message)
            .isEqualTo("$errorMessage Response.error()")
        // Verify that stages for valid event is not called
        coVerify(exactly = 0) { api.getStages(cookie, eventId = eventId) }
    }

    @Test
    fun `given Http 200, should return Stages`() = runBlockingTest {

        // GIVEN
        val eventId = 108564L
        val actual = stages
        coEvery { api.getStages(sessionToken, eventId) } returns actual

        // WHEN
        val expected = stagesDataSource.getStages(sessionToken, eventId)

        // THEN
        Truth.assertThat(expected).isEqualTo(actual)
        coVerify(exactly = 1) { api.getStages(sessionToken, eventId) }
    }

    @Test
    fun `given uuid is wrong, should throw HttpException with 404 code`() = runBlockingTest {

        // GIVEN
        val errorMessage = "HTTP 404"
        val eventId = 108564L
        val uuid = "82604620-18b0-424c-b550-c74019551ba8"
        coEvery { api.getStageWithStatus(sessionToken, eventId, uuid + "k") } throws
            HttpException(Response.error<Stages>(404, errorMessage.toResponseBody()))

        // WHEN
        val expected = try {
            stagesDataSource.getStageWithStatus(sessionToken, eventId, uuid + "k")
        } catch (e: HttpException) {
            e
        }

        // THEN
        Truth.assertThat(expected).isInstanceOf(HttpException::class.java)
        Truth.assertThat((expected as HttpException).code()).isEqualTo(404)
        // Verify that stages for valid event is not called
        coVerify(exactly = 0) { api.getStages(cookie, eventId = eventId) }
    }

    @Test
    fun `given Http 200, should return stage with status`() = runBlockingTest {

        // GIVEN
        val eventId = 108564L
        val uuid = "82604620-18b0-424c-b550-c74019551ba8"
        val actual = stageWithStatus
        coEvery { api.getStageWithStatus(sessionToken, eventId, uuid) } returns actual

        // WHEN
        val expected = stagesDataSource.getStageWithStatus(sessionToken, eventId, uuid)

        // THEN
        Truth.assertThat(expected).isEqualTo(actual)
        coVerify(exactly = 1) { api.getStageWithStatus(sessionToken, eventId, uuid) }
    }

    @Before
    fun setUp() {
        stagesDataSource = StagesDataSourceImpl(api)
    }

    @After
    fun tearDown() {
        clearMocks(api)
    }
}
