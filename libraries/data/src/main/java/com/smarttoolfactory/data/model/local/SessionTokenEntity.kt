package com.smarttoolfactory.data.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.smarttoolfactory.data.model.IEntity

@Entity(tableName = "session_token")
data class SessionTokenEntity(@PrimaryKey val token: String, val fetchDate: Long) : IEntity
