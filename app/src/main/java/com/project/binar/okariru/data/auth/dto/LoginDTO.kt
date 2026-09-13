package com.project.binar.okariru.data.auth.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(
    val username: String,
    val password: String
)

@Serializable
data class LoginResponseDto(
    val username: String,
    val role: String,
    val token: String,
    val userId: Int? = null,
)