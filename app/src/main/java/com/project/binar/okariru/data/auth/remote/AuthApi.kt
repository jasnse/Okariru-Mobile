package com.project.binar.okariru.data.auth.remote


import com.project.binar.okariru.core.network.ApiEnvelope
import com.project.binar.okariru.data.auth.dto.CustomerDTO
import com.project.binar.okariru.data.auth.dto.CustomerUpdateRequestDto
import com.project.binar.okariru.data.auth.dto.LoginRequestDto
import com.project.binar.okariru.data.auth.dto.LoginResponseDto
import com.project.binar.okariru.data.auth.dto.RegisterRequest
import com.project.binar.okariru.data.auth.dto.RegisterResponse
import com.project.binar.okariru.data.plafond.dto.PlafondDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT

interface AuthApi {

    @POST("/api/v1/login/customer")
    suspend fun login(@Body body: LoginRequestDto): LoginResponseDto

    //register
    @POST("/api/v1/customer")
    suspend fun register(@Body body: RegisterRequest): RegisterResponse

    //get customer by Id
    @GET("/api/v1/customer")
    suspend fun getCustById(@Header("idCustomerSearch") customerId: Int): CustomerDTO

    @PUT("/api/v1/customer/me")
    suspend fun updateMyProfile(@Body body: CustomerUpdateRequestDto): String
}
