package com.project.binar.okariru.data.document.repository

import com.project.binar.okariru.core.network.AppResult
import com.project.binar.okariru.core.network.asAppResult
import com.project.binar.okariru.core.network.runApiCatching
import com.project.binar.okariru.data.document.dto.DocumentUploadResponseDto
import com.project.binar.okariru.data.document.remote.DocumentApi
import com.project.binar.okariru.data.pinjaman.remote.PinjamanApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.MultipartBody

class DocumentRepository(
    private val api: DocumentApi,
    private val json: Json,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {

    suspend fun uploadDocuments(
        transPinjamanId: Int,
        customerId: Int,
        files: List<MultipartBody.Part>,
    ): AppResult<List<DocumentUploadResponseDto>> = withContext(ioDispatcher) {
        runApiCatching(json) {
            api.uploadDocuments(transPinjamanId, customerId, files).asAppResult()
        }
    }
}