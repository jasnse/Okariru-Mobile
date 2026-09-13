package com.project.binar.okariru.core.network

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

private const val HEADER_AUTHORIZATION = "Authorization"

// Prefix path yang di backend di-permitAll() untuk method POST (lihat SecurityConfig#securityFilterChain).
// Token lama/invalid tidak boleh ditempel ke endpoint ini, backend bisa menolaknya dengan 401.
private val PUBLIC_POST_PATH_PREFIXES = listOf(
    "/api/v1/login/",
    "/api/v1/customer",
    "/api/v1/reset/",
)

class AuthHeaderInterceptor(
    private val tokenProvider: AuthTokenProvider,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        if (request.header(HEADER_AUTHORIZATION) != null) return chain.proceed(request)

        if (request.method == "POST" &&
            PUBLIC_POST_PATH_PREFIXES.any { request.url.encodedPath.startsWith(it) }
        ) {
            return chain.proceed(request)
        }

        val token = runBlocking { tokenProvider.currentToken() }
            ?: return chain.proceed(request)

        return chain.proceed(
            request.newBuilder().header(HEADER_AUTHORIZATION, "Bearer $token").build()
        )
    }
}