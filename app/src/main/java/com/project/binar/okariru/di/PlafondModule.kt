package com.project.binar.okariru.di

import com.project.binar.okariru.data.plafond.remote.PlafondApi
import com.project.binar.okariru.data.plafond.repository.PlafondRepository
import com.project.binar.okariru.core.database.dao.PlafondDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PlafondModule {

    @Provides
    @Singleton
    fun providePlafondApi(retrofit: Retrofit): PlafondApi = retrofit.create(PlafondApi::class.java)

    @Provides
    @Singleton
    fun providePlafondRepository(
        api: PlafondApi,
        dao: PlafondDao,
        json: Json,
    ): PlafondRepository = PlafondRepository(
        api = api,
        dao = dao,
        json = json,
    )
}
