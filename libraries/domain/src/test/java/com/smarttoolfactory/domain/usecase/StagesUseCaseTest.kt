package com.smarttoolfactory.domain.usecase

import com.smarttoolfactory.data.model.remote.broadcast.Stages
import com.smarttoolfactory.data.repository.StageRepository
import com.smarttoolfactory.domain.error.StageNotAvailableException
import com.smarttoolfactory.myapplication.model.broadcast.StageWithStatus
import com.smarttoolfactory.test_utils.RESPONSE_STAGES_JSON_PATH
import com.smarttoolfactory.test_utils.RESPONSE_STAGE_WITH_NO_ACTIVE_JSON_PATH
import com.smarttoolfactory.test_utils.RESPONSE_STAGE_WITH_STATUS_JSON_PATH
import com.smarttoolfactory.test_utils.rule.TestCoroutineRule
import com.smarttoolfactory.test_utils.test_observer.test
import com.smarttoolfactory.test_utils.util.convertToObjectFromJson
import com.smarttoolfactory.test_utils.util.getResourceAsText
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifySequence
import io.mockk.mockk
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.NotActiveException

class StagesUseCaseTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private lateinit var stagesUseCase: StagesUseCase

    private val repository: StageRepository = mockk()

    private val sessionToken =
        "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI3MmVhYjBlNy05ZGIyLTRmNmEtOTQyMC1hNDY4Y" +
            "jQzYzczZDgiLCJzdWIiOjIxOTY5OTAsInBlcnNvbmFfaWQiOjMyNDUxMSwicm" +
            "VnaXN0cmF0aW9uX2lkIjo1Mzc0NzA5LCJldmVudF9pZCI6MTA4NTY0LCJyb2xl" +
            "Ijoib3JnYW5pc2VyIiwibXVsdGlwbGVfY29ubiI6dHJ1ZSwiZGF0YV9zZWdyZWd" +
            "hdGVkIjpmYWxzZX0.AAhrVXd5LYYy6YReFCN3hAc7e9d4z0FltcmPt_YdesY"

    private val eventId = 108564L
    val uuid = "82604620-18b0-424c-b550-c74019551ba8"

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

    val stageWithNoActiveLinks by lazy {
        convertToObjectFromJson<StageWithStatus>(
            getResourceAsText(RESPONSE_STAGE_WITH_NO_ACTIVE_JSON_PATH)
        )!!
    }

    @Test
    fun `given exception returned from stages should return StagesNotAvailableException`() =
        testCoroutineRule.runBlockingTest {

            // GIVEN
            coEvery {
                repository.getStages(sessionToken, eventId)
            } throws StageNotAvailableException("Your user is not registered on this event")

            // WHEN
            val testObserver = stagesUseCase.getVideoLinks(sessionToken, eventId).test(this)

            // THEN
            testObserver
                .assertNotComplete()
                .assertError(StageNotAvailableException::class.java)
                .dispose()

            coVerify(exactly = 1) { repository.getStages(sessionToken, eventId) }
            coVerify(exactly = 0) { repository.getStageWithStatus(any(), any(), any()) }
        }

    @Test
    fun `given exception returned from stage with stats should return exception`() =
        testCoroutineRule.runBlockingTest {

            // GIVEN
            val stageException = StageNotAvailableException("Stage not found")
            coEvery {
                repository.getStages(sessionToken, eventId)
            } returns stages

            coEvery {
                repository.getStageWithStatus(sessionToken, eventId, uuid)
            } throws stageException

            // WHEN
            val testObserver = stagesUseCase.getVideoLinks(sessionToken, eventId).test(this)

            // THEN
            testObserver
                .assertNotComplete()
                .assertError(StageNotAvailableException::class.java)
                .dispose()

            coVerifySequence {
                repository.getStages(sessionToken, eventId)
                repository.getStageWithStatus(sessionToken, eventId, uuid)
            }
        }

    @Test
    fun `given no active broadcasts should return InActiveBroadcastException`() =
        testCoroutineRule.runBlockingTest {
        }

    @Test
    fun `given valid link exists should return list of stream urls`() =
        testCoroutineRule.runBlockingTest {
        }

    @Before
    fun setUp() {
        stagesUseCase = StagesUseCase(repository)
    }

    @After
    fun tearDown() {
        clearMocks(repository)
    }
}
