package com.project.binar.okariru.di

import com.project.binar.okariru.core.database.dao.PlafondDao
import com.project.binar.okariru.core.database.dao.StatusPinjamanDao
import com.project.binar.okariru.data.plafond.remote.PlafondApi
import com.project.binar.okariru.data.plafond.repository.PlafondRepository
import com.project.binar.okariru.data.status_pinjaman.remote.StatusPinjamanApi
import com.project.binar.okariru.data.status_pinjaman.repository.StatusPinjamanRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StatusPinjamanModule {

    @Provides
    @Singleton
    fun provideStatusPinjamanApi(retrofit: Retrofit): StatusPinjamanApi = retrofit.create(StatusPinjamanApi::class.java)

    @Provides
    @Singleton
    fun provideStatusPinjamanRepository(
        api: StatusPinjamanApi,
        dao: StatusPinjamanDao,
        json: Json,
    ): StatusPinjamanRepository = StatusPinjamanRepository(
        api = api,
        dao = dao,
        json = json,
    )
}