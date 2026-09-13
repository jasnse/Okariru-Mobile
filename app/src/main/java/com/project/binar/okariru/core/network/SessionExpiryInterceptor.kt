package com.project.binar.okariru.core.network

import com.project.binar.okariru.data.auth.local.AuthSessionLocalDataSource
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

private const val HTTP_UNAUTHORIZED = 401
private const val HEADER_AUTHORIZATION = "Authorization"

/** Kalau request yang bawa token mendapat 401, sesi lokal dianggap sudah tidak valid dan dihapus. */
class SessionExpiryInterceptor(
    private val localDataSource: AuthSessionLocalDataSource,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        if (response.code == HTTP_UNAUTHORIZED && request.header(HEADER_AUTHORIZATION) != null) {
            runBlocking { localDataSource.clear() }
        }

        return response
    }
}
