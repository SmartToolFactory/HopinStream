package com.smarttoolfactory.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.smarttoolfactory.data.model.local.SessionTokenEntity

@Dao
interface SessionTokenDao : BaseDao<SessionTokenEntity> {

    @Query("SELECT * FROM session_token")
    suspend fun getSessionToken(): SessionTokenEntity?

    @Query("DELETE FROM session_token")
    suspend fun deleteAll()
}
