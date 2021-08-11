package com.smarttoolfactory.data.di

import android.app.Application
import androidx.room.Room
import com.smarttoolfactory.data.constant.DATABASE_NAME
import com.smarttoolfactory.data.db.HopinDatabase
import com.smarttoolfactory.data.db.dao.SessionTokenDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(application: Application): HopinDatabase {
        return Room.databaseBuilder(
            application,
            HopinDatabase::class.java,
            DATABASE_NAME
        ).build()
    }

    @Singleton
    @Provides
    fun provideSessionTokenDao(appDatabase: HopinDatabase): SessionTokenDao =
        appDatabase.sessionTokenDao()

}
