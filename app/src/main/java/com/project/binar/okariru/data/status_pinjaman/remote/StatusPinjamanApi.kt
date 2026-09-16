package com.project.binar.okariru.data.status_pinjaman.remote

import com.project.binar.okariru.data.status_pinjaman.dto.listPinjamanDto
import retrofit2.http.GET
import retrofit2.http.Query

interface StatusPinjamanApi {

    @GET("/api/v1/pinjaman/transaction/customer")
    suspend fun getListPinjaman(
        @Query("customerId") customerId: Int,
        @Query("status") status: String? = null,
        @Query("keyword") keyword: String? = null,
    ): List<listPinjamanDto>
//    suspend fun getLoanProducts(
//        @Query("page") page: Int,
//        @Query("size") size: Int,
//        @Query("sort") sort: List<String>,
//    ): ApiEnvelope<List<LoanProductDto>>
}