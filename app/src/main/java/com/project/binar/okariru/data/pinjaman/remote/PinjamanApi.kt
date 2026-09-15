package com.project.binar.okariru.data.pinjaman.remote

import com.project.binar.okariru.data.pinjaman.dto.PageResponse
import com.project.binar.okariru.data.pinjaman.dto.PinjamanDto
import com.project.binar.okariru.data.pinjaman.dto.PinjamanTransactionRequestDto
import com.project.binar.okariru.data.pinjaman.dto.PinjamanTransactionResponseDto
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface PinjamanApi {

    @GET("/api/v1/pinjaman")
    suspend fun getLoanTypes(
        @Query("keyword") keyword: String = "",
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 50,
    ): PageResponse<PinjamanDto>

    @POST("/api/v1/pinjaman/transaction")
    suspend fun submitLoanApplication(
        @Body body: PinjamanTransactionRequestDto,
    ): PinjamanTransactionResponseDto




}

