package com.smarttoolfactory.core.di

import com.smarttoolfactory.core.connectivity.ConnectionManager
import com.smarttoolfactory.core.connectivity.ConnectionManagerImpl
import com.smarttoolfactory.core.hopin.JWTDecoderImpl
import com.smarttoolfactory.data.di.DatabaseModule
import com.smarttoolfactory.data.di.NetworkModule
import com.smarttoolfactory.data.mapper.SessionTokenMapper
import com.smarttoolfactory.data.repository.LoginRepository
import com.smarttoolfactory.data.repository.LoginRepositoryImpl
import com.smarttoolfactory.data.repository.StageRepository
import com.smarttoolfactory.data.repository.StageRepositoryImpl
import com.smarttoolfactory.data.source.LocalLoginDataSource
import com.smarttoolfactory.data.source.LocalLoginDataSourceIml
import com.smarttoolfactory.data.source.RemoteLoginDataSource
import com.smarttoolfactory.data.source.RemoteLoginDataSourceIml
import com.smarttoolfactory.data.source.StagesDataSource
import com.smarttoolfactory.data.source.StagesDataSourceImpl
import com.smarttoolfactory.domain.mapper.JWTDecoder
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module(
    includes = [
        DataProviderModule::class,
        NetworkModule::class,
        DatabaseModule::class
    ]
)
/**
 * This dagger module binds concrete implementations to interface required by
 * **data** and **domain** structure modules.
 *
 * For instance
 * ```
 * class LoginRepositoryImpl @Inject constructor(
 *      private val remoteDataSource: RemoteLoginDataSource,
 *      private val localDataSource: LocalLoginDataSource,
 *      private val mapper: SessionTokenMapper
 *  ) : LoginRepository {
 *
 * ```
 *
 * requires interfaces instead of concrete implementations.
 */
interface DataModule {

    @Singleton
    @Binds
    fun bindRemoteDataSource(remoteDataSource: RemoteLoginDataSourceIml):
        RemoteLoginDataSource

    @Singleton
    @Binds
    fun bindLocalDataSource(localDataSource: LocalLoginDataSourceIml):
        LocalLoginDataSource

    @Singleton
    @Binds
    fun bindLoginRepository(repository: LoginRepositoryImpl):
        LoginRepository

    @Singleton
    @Binds
    fun bindStageDatasource(stagesDataSource: StagesDataSourceImpl):
        StagesDataSource

    @Singleton
    @Binds
    fun bindRepository(stageRepository: StageRepositoryImpl):
        StageRepository

    @Singleton
    @Binds
    fun bindJWTDecoder(jwtDecoderImpl: JWTDecoderImpl): JWTDecoder

    @Singleton
    @Binds
    fun bindConnectionManager(connectionManager: ConnectionManagerImpl): ConnectionManager
}

/**
 * This module is for injections with @Provides annotation
 */
@Module
@InstallIn(SingletonComponent::class)
object DataProviderModule {
    @Provides
    fun provideSessionTokenMapper() = SessionTokenMapper()
}
