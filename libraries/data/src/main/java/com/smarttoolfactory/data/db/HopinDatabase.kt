package com.smarttoolfactory.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.smarttoolfactory.data.constant.DATABASE_VERSION
import com.smarttoolfactory.data.db.dao.SessionTokenDao
import com.smarttoolfactory.data.model.local.SessionTokenEntity

@Database(
    entities = [
        SessionTokenEntity::class,
    ],
    version = DATABASE_VERSION,
    exportSchema = true
)
abstract class HopinDatabase : RoomDatabase() {
    abstract fun sessionTokenDao(): SessionTokenDao
}