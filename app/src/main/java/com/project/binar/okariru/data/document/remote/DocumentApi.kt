package com.project.binar.okariru.data.document.remote

import com.project.binar.okariru.data.document.dto.DocumentUploadResponseDto
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface DocumentApi {
    @Multipart
    @POST("/api/v1/document")
    suspend fun uploadDocuments(
        @Query("transPinjamanId") transPinjamanId: Int,
        @Query("customerId") customerId: Int,
        @Part files: List<MultipartBody.Part>,
    ): List<DocumentUploadResponseDto>
}