package com.project.binar.okariru.data.angsuran.remote

import com.project.binar.okariru.data.angsuran.dto.AngsuranBayarRequest
import com.project.binar.okariru.data.angsuran.dto.AngsuranBayarResponse
import com.project.binar.okariru.data.angsuran.dto.AngsuranDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AngsuranApi {

    @GET("/api/v1/angsuran/pinjaman/{transPinjamanId}")
    suspend fun getByTransPinjamanId(
        @Path("transPinjamanId") transPinjamanId: Int
    ): List<AngsuranDto>

    @POST("/api/v1/angsuran/bayar")
    suspend fun bayarAngsuran(
        @Body request: AngsuranBayarRequest
    ): AngsuranBayarResponse
}
