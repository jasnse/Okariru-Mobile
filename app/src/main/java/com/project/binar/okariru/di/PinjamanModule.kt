package com.project.binar.okariru.di

import com.project.binar.okariru.core.database.dao.PlafondDao
import com.project.binar.okariru.data.pinjaman.remote.PinjamanApi
import com.project.binar.okariru.data.pinjaman.repository.PinjamanRepository
import com.project.binar.okariru.data.plafond.remote.PlafondApi
import com.project.binar.okariru.data.plafond.repository.PlafondRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PinjamanModule {

    @Provides
    @Singleton
    fun providePinjamanApi(retrofit: Retrofit): PinjamanApi = retrofit.create(PinjamanApi::class.java)

    @Provides
    @Singleton
    fun providePinjamanRepository(
        api: PinjamanApi,
//        dao: ,
        json: Json,
    ): PinjamanRepository = PinjamanRepository(
        api = api,
//        dao = dao,
        json = json,
    )
}