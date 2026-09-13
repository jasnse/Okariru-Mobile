package com.project.binar.okariru.data.auth.repository

import com.project.binar.okariru.core.network.AppResult
import com.project.binar.okariru.core.network.asAppResult
import com.project.binar.okariru.core.network.runApiCatching
import com.project.binar.okariru.data.auth.dto.RegisterRequest
import com.project.binar.okariru.data.auth.dto.RegisterResponse
import com.project.binar.okariru.data.auth.remote.AuthApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class RegisterRepository internal constructor(
    private val remoteDataSource: AuthApi,
    private val json: Json,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    suspend fun register(request: RegisterRequest): AppResult<RegisterResponse> {
        return withContext(ioDispatcher) {
            runApiCatching(json) {
                remoteDataSource.register(request).asAppResult()
            }
        }
    }
}