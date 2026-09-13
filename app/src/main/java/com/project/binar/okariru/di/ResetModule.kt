package com.project.binar.okariru.di

import com.project.binar.okariru.data.reset.remote.ResetPasswordApi
import com.project.binar.okariru.data.reset.repository.ResetRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ResetModule {

    @Provides
    @Singleton
    fun provideResetPasswordApi(retrofit: Retrofit): ResetPasswordApi = retrofit.create(ResetPasswordApi::class.java)

    @Provides
    @Singleton
    fun provideResetRepository(
        api: ResetPasswordApi,
        json: Json,
    ): ResetRepository = ResetRepository(
        api = api,
        json = json,
    )
}