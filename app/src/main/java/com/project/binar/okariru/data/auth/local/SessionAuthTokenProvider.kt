package com.project.binar.okariru.data.auth.local


import com.project.binar.okariru.core.network.AuthTokenProvider
import kotlinx.coroutines.flow.first

class SessionAuthTokenProvider(
    private val localDataSource: AuthSessionLocalDataSource,
) : AuthTokenProvider {

    override suspend fun currentToken(): String? =
        localDataSource.observe().first()?.accessToken
}