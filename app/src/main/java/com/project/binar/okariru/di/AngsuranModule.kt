package com.project.binar.okariru.di

import com.project.binar.okariru.data.angsuran.remote.AngsuranApi
import com.project.binar.okariru.data.angsuran.repository.AngsuranRepository
import com.project.binar.okariru.data.pinjaman.remote.PinjamanApi
import com.project.binar.okariru.data.pinjaman.repository.PinjamanRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import retrofit2.Retrofit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AngsuranModule {

    @Provides
    @Singleton
    fun provideAngsuranApi(retrofit: Retrofit): AngsuranApi = retrofit.create(AngsuranApi::class.java)

    @Provides
    @Singleton
    fun provideAngsuranRepository(
        api: AngsuranApi,
//        dao: ,
        json: Json,
    ): AngsuranRepository = AngsuranRepository(
        api = api,
//        dao = dao,
        json = json,
    )
}