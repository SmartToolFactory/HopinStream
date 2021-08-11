package com.smarttoolfactory.data.model.local

import androidx.room.Entity
import com.smarttoolfactory.data.model.IEntity

@Entity(tableName = "session_token")
data class SessionTokenEntity(val token: String, val fetchDate: Long) : IEntity
