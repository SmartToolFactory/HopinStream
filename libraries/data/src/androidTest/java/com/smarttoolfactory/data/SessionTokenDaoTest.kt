package com.smarttoolfactory.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import com.smarttoolfactory.data.db.dao.SessionTokenDao
import com.smarttoolfactory.data.model.local.SessionTokenEntity
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SessionTokenDaoTest : AbstractDaoTest() {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var sessionTokenDao: SessionTokenDao

    companion object {

        val token =
            "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI3MmVhYjBlNy05ZGIyLTRmNmEtOTQyMC1hNDY4YjQzYzczZDgi" +
                "LCJzdWIiOjIxOTY5OTAsInBlcnNvbmFfaWQiOjMyNDUxMSwicmVnaXN0cmF0aW9uX2lkIjo1Mz" +
                "c0NzA5LCJldmVudF9pZCI6MTA4NTY0LCJyb2xlIjoib3JnYW5pc2VyIiwibXVsdGlwbGVfY29u" +
                "biI6dHJ1ZSwiZGF0YV9zZWdyZWdhdGVkIjpmYWxzZX0.AAhrVXd5LYYy6YReFCN3hAc7e9d4" +
                "z0FltcmPt_YdesY"

        val newToken =
            "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJiMjM4ZDBkMi1iMmUwLTQ1ZjUtYTY3Mi1jM2I5MTI1OWU2Yz" +
                "kiLCJzdWIiOjc1MDQ4OTQsInBlcnNvbmFfaWQiOjY1MTMzMiwicmVnaXN0cmF0aW9uX2lkI" +
                "joxMDI1OTk5NywiZXZlbnRfaWQiOjIzNTM5OSwicm9sZSI6Im9yZ2FuaXNlciIsIm11bHRpc" +
                "GxlX2Nvbm4iOnRydWUsImRhdGFfc2VncmVnYXRlZCI6ZmFsc2V9.pOBnFApLXlK3D6re_" +
                "U9ZrgJrfgWdMBUIPpGMJbsXVAY"
    }

    @Test
    fun givenDBEmptyShouldReturnNull() = runBlocking {

        // GIVEN

        // WHEN
        val sessionToken = sessionTokenDao.getSessionToken()

        // THEN
        Truth.assertThat(sessionToken).isNull()
    }

    @Test
    fun givenTokenFetchedShouldReturnToken() = runBlocking {

        // GIVEN
        val currentTime = System.currentTimeMillis()
        sessionTokenDao.insert(SessionTokenEntity(token, currentTime))

        // WHEN
        val sessionToken = sessionTokenDao.getSessionToken()

        // THEN
        Truth.assertThat(sessionToken).isNotNull()
        Truth.assertThat(token).isEqualTo(sessionToken!!.token)
        Truth.assertThat(sessionToken.fetchDate).isEqualTo(currentTime)
    }

    @Test
    fun givenDBPopulatedShouldDeleteExistingTokenAndReturnNew() = runBlocking {

        // GIVEN
        val oldSessionToken = SessionTokenEntity(token, System.currentTimeMillis())
        sessionTokenDao.insert(oldSessionToken)

        // WHEN
        sessionTokenDao.deleteAll()
        val newTime = System.currentTimeMillis()
        sessionTokenDao.insert(SessionTokenEntity(newToken, newTime))

        val newSessionToken = sessionTokenDao.getSessionToken()!!
        val tokenCount = sessionTokenDao.getTokenCount()

        // THEN
        Truth.assertThat(tokenCount).isEqualTo(1)
        Truth.assertThat(newSessionToken.token).isEqualTo(newToken)
        Truth.assertThat(newSessionToken.fetchDate).isEqualTo(newTime)
        Truth.assertThat(oldSessionToken).isNotEqualTo(newSessionToken)
    }

    @Before
    override fun setUp() {
        super.setUp()
        sessionTokenDao = database.sessionTokenDao()
    }
}
