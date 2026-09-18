package com.project.binar.okariru.di

import android.content.Context
import com.project.binar.okariru.core.database.dao.CustomerDao
import com.project.binar.okariru.core.notification.FcmLocalStore
import com.project.binar.okariru.data.auth.local.AuthSessionLocalDataSource
import com.project.binar.okariru.data.auth.remote.AuthApi
import com.project.binar.okariru.data.auth.repository.AuthRepository
import com.project.binar.okariru.data.auth.repository.RegisterRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {
    @Provides
    @Singleton
    fun provideAuthSessionLocalDataSource(
        @ApplicationContext context: Context,
    ): AuthSessionLocalDataSource = AuthSessionLocalDataSource(context)

    @Provides
    @Singleton
    fun provideFcmLocalStore(
        @ApplicationContext context: Context,
    ): FcmLocalStore = FcmLocalStore(context)

    @Provides
    @Singleton
    fun provideAuthRepository(
        localDataSource: AuthSessionLocalDataSource,
        authApi: AuthApi,
        json: Json,
        dao: CustomerDao,
        fcmLocalStore: FcmLocalStore,
    ): AuthRepository = AuthRepository(
        localDataSource = localDataSource,
        apiHit = authApi,
        json = json,
        dao = dao,
        fcmLocalStore = fcmLocalStore,
    )

    @Provides
    @Singleton
    fun provideRegisterRepository(
        authApi: AuthApi,
        json: Json,
    ): RegisterRepository = RegisterRepository(
        remoteDataSource = authApi,
        json = json,
    )
}
