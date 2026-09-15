package com.project.binar.okariru.data.plafond.remote

import com.project.binar.okariru.data.plafond.dto.PlafondDto
import retrofit2.http.GET
import retrofit2.http.Query

interface PlafondApi {

    @GET("/api/v1/plafond")
    suspend fun getPlafond(@Query("userId") userId: Int?): PlafondDto

//    @GET("api/loan-product")
//    suspend fun getLoanProducts(
//        @Query("page") page: Int,
//        @Query("size") size: Int,
//        @Query("sort") sort: List<String>,
//    ): ApiEnvelope<List<LoanProductDto>>
}
