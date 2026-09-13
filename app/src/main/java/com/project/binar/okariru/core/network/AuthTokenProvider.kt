package com.project.binar.okariru.core.network

fun interface AuthTokenProvider {
    suspend fun currentToken(): String?
}