package com.smarttoolfactory.data.mapper

import com.google.common.truth.Truth
import com.smarttoolfactory.data.model.local.SessionTokenEntity
import com.smarttoolfactory.data.model.remote.sso.SessionTokenDTO
import org.junit.Test

internal class SessionTokenMapperTest {

    private val token =
        "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI3MmVhYjBlNy05ZGIyLTRmNmEtOTQy" +
            "MC1hNDY4YjQzYzczZDgiLCJzdWIiOjIxOTY5OTAsInBlcnNvbmFfaWQ" +
            "iOjMyNDUxMSwicmVnaXN0cmF0aW9uX2lkIjo1Mzc0NzA5LCJldmVudF9p" +
            "ZCI6MTA4NTY0LCJyb2xlIjoib3JnYW5pc2VyIiwibXVsdGlwbGVfY29ubi" +
            "6dHJ1ZSwiZGF0YV9zZWdyZWdhdGVkIjpmYWxzZX0.AAhrVXd5LYYy6YReFC" +
            "N3hAc7e9d4z0FltcmPt_YdesY"

    @Test
    fun `given valid session token DTO should return valid Entity`() {
        // GIVEN
        val sessionToken = SessionTokenDTO(token)
        val mapper = SessionTokenMapper()

        // WHEN
        val entity: SessionTokenEntity = mapper.map(sessionToken)

        // THEN
        Truth.assertThat(entity.token).isEqualTo(token)
    }
}
