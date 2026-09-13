package com.project.binar.okariru.data.reset.dto

import kotlinx.serialization.Serializable

@Serializable
data class ValidateOtpResponseDto(
    val message: String,
    val resetToken: String,
)
@Serializable
data class ResetPasswordRequestDto(
    val newPassword: String,
)