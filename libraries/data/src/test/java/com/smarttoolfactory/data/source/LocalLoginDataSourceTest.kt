package com.smarttoolfactory.data.source

import com.google.common.truth.Truth
import com.smarttoolfactory.data.db.dao.SessionTokenDao
import com.smarttoolfactory.data.model.local.SessionTokenEntity
import com.smarttoolfactory.data.model.remote.request.SessionTokenRequest
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class LocalLoginDataSourceTest {

    private val cookie =
        "user.token=QDla%2Fin5Ryv071eziBpHb56KNwQQQdROaealpQHGZHvBxRKe%2FwZwgUFbGzks3O" +
            "aJRs%2BWWNSZybMwgDKNuJeX5rnwr7OggNXPX5w%3D--XxJELxpUNISUuZl6--Rts4nW" +
            "VmI4uJCKgVDnyT%2Bw%3D%3D"

    private val sessionToken =
        "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI3MmVhYjBlNy05ZGIyLTRmNmEtOTQyMC1hNDY4YjQzYzczZ" +
            "DgiLCJzdWIiOjIxOTY5OTAsInBlcnNvbmFfaWQiOjMyNDUxMSwicmVnaXN0cmF0aW9uX2l" +
            "kIjo1Mzc0NzA5LCJldmVudF9pZCI6MTA4NTY0LCJyb2xlIjoib3JnYW5pc2VyIiwibXVsdG" +
            "lwbGVfY29ubiI6dHJ1ZSwiZGF0YV9zZWdyZWdhdGVkIjpmYWxzZX0.AAhrVXd5LYYy6YReFCN" +
            "3hAc7e9d4z0FltcmPt_YdesY"

    private val request = SessionTokenRequest("hopincon2022")

    private val dao = mockk<SessionTokenDao>()

    private lateinit var localDataSource: LocalLoginDataSource

    @Test
    fun `given DB is empty should return null`() = runBlockingTest {

        // GIVEN
        coEvery { dao.getSessionToken() } returns null

        // WHEN
        val actual = localDataSource.getSessionTokeEntity()

        // THEN
        Truth.assertThat(actual).isNull()
        coVerify(exactly = 1) { dao.getSessionToken() }
    }

    @Test
    fun `given new token fetched, should save token`() = runBlockingTest {

        // GIVEN
        val actual = SessionTokenEntity(sessionToken, System.currentTimeMillis())
        val slot = slot<SessionTokenEntity>()

        coEvery { dao.insert(capture(slot)) } returns 1

        // WHEN
        val result = localDataSource.saveSessionTokenEntity(actual)

        // THEN
        Truth.assertThat(result).isEqualTo(1)
        Truth.assertThat(actual).isEqualTo(slot.captured)
        coVerify(exactly = 1) { dao.insert(actual) }
    }

    @Test
    fun `given DB is populated should return session token`() = runBlockingTest {

        // GIVEN
        val expected = SessionTokenEntity(sessionToken, System.currentTimeMillis())
        coEvery { dao.getSessionToken() } returns expected

        // WHEN
        val actual = localDataSource.getSessionTokeEntity()

        // THEN
        Truth.assertThat(actual).isEqualTo(expected)
        coVerify(exactly = 1) { dao.getSessionToken() }
    }

    @Test
    fun `should delete entities`() = runBlockingTest {

        // GIVEN
        coEvery { dao.deleteAll() } just runs

        // WHEN
        localDataSource.deleteSessionToken()

        // THEN
        coVerify(exactly = 1) { dao.deleteAll() }
    }

    @Before
    fun setUp() {
        localDataSource = LocalLoginDataSourceIml(dao)
    }

    @After
    fun tearDown() {
        clearMocks(dao)
    }
}
