package com.smarttoolfactory.data.repository

import com.google.common.truth.Truth
import com.smarttoolfactory.data.model.remote.broadcast.Stages
import com.smarttoolfactory.data.source.StagesDataSource
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

class StageRepositoryTest {

    private lateinit var repository: StageRepository

    private val dataSource: StagesDataSource = mockk()

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
        coEvery { dataSource.getStages(sessionToken, eventId + 5) } throws
            HttpException(Response.error<Stages>(403, errorMessage.toResponseBody()))

        // WHEN
        val expected = try {
            repository.getStages(sessionToken, eventId + 5)
        } catch (e: HttpException) {
            e
        }

        // THEN
        Truth.assertThat(expected).isInstanceOf(HttpException::class.java)
        Truth.assertThat((expected as HttpException).message)
            .isEqualTo("$errorMessage Response.error()")
        // Verify that stages for valid event is not called
        coVerify(exactly = 0) { dataSource.getStages(cookie, eventId = eventId) }
    }

    @Test
    fun `given Http 200, should return Stages`() = runBlockingTest {

        // GIVEN
        val eventId = 108564L
        val actual = stages
        coEvery { dataSource.getStages(sessionToken, eventId) } returns actual

        // WHEN
        val expected = repository.getStages(sessionToken, eventId)

        // THEN
        Truth.assertThat(expected).isEqualTo(actual)
        coVerify(exactly = 1) { dataSource.getStages(sessionToken, eventId) }
    }

    @Test
    fun `given uuid is wrong, should throw HttpException with 404 code`() = runBlockingTest {

        // GIVEN
        val errorMessage = "HTTP 404"
        val eventId = 108564L
        val uuid = "82604620-18b0-424c-b550-c74019551ba8"
        coEvery { dataSource.getStageWithStatus(sessionToken, eventId, uuid + "k") } throws
            HttpException(Response.error<Stages>(404, errorMessage.toResponseBody()))

        // WHEN
        val expected = try {
            repository.getStageWithStatus(sessionToken, eventId, uuid + "k")
        } catch (e: HttpException) {
            e
        }

        // THEN
        Truth.assertThat(expected).isInstanceOf(HttpException::class.java)
        Truth.assertThat((expected as HttpException).code()).isEqualTo(404)
        // Verify that stages for valid event is not called
        coVerify(exactly = 0) { dataSource.getStages(cookie, eventId = eventId) }
    }

    @Test
    fun `given Http 200, should return stage with status`() = runBlockingTest {

        // GIVEN
        val eventId = 108564L
        val uuid = "82604620-18b0-424c-b550-c74019551ba8"
        val actual = stageWithStatus
        coEvery { dataSource.getStageWithStatus(sessionToken, eventId, uuid) } returns actual

        // WHEN
        val expected = repository.getStageWithStatus(sessionToken, eventId, uuid)

        // THEN
        Truth.assertThat(expected).isEqualTo(actual)
        coVerify(exactly = 1) { dataSource.getStageWithStatus(sessionToken, eventId, uuid) }
    }

    @Before
    fun setUp() {
        repository = StageRepositoryImpl(dataSource)
    }

    @After
    fun tearDown() {
        clearMocks(dataSource)
    }
}
