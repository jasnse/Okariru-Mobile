package com.project.binar.okariru.data.reset.remote

import com.project.binar.okariru.data.reset.dto.ResetPasswordRequestDto
import com.project.binar.okariru.data.reset.dto.ValidateOtpResponseDto
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query


interface ResetPasswordApi {

        @POST("/api/v1/reset/forgot-password")
        suspend fun generateOTp(@Query("email") email: String): String

        @POST("/api/v1/reset/validate-otp")
        suspend fun validateOtp(
            @Query("email") email: String,
            @Query("otp") otp: String
        ): ValidateOtpResponseDto


    @POST("/api/v1/reset/reset-password")
    suspend fun changePassword(
        @Query("resetToken") resetToken: String,
        @Body request: ResetPasswordRequestDto
    ): String


}
