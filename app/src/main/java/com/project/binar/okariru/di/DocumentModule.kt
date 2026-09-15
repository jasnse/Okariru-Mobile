package com.project.binar.okariru.di

import com.project.binar.okariru.core.database.dao.PlafondDao
import com.project.binar.okariru.data.document.remote.DocumentApi
import com.project.binar.okariru.data.document.repository.DocumentRepository
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
object DocumentModuleModule {

    @Provides
    @Singleton
    fun provideDocumentApi(retrofit: Retrofit): DocumentApi = retrofit.create(DocumentApi::class.java)

    @Provides
    @Singleton
    fun providePinjamanRepository(
        api: DocumentApi,
//        dao: ,
        json: Json,
    ): DocumentRepository = DocumentRepository(
        api = api,
//        dao = dao,
        json = json,
    )
}

