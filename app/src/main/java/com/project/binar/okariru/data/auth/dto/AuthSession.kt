package com.project.binar.okariru.data.auth.dto

data class AuthSession(
    val user: AuthUser,
    val accessToken: String,
    val expiresAtMillis: Long,
) {
    fun isExpiredAt(nowMillis: Long): Boolean = nowMillis >= expiresAtMillis
}