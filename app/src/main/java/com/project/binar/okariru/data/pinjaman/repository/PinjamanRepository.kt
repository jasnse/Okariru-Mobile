package com.project.binar.okariru.data.pinjaman.repository

import com.project.binar.okariru.core.network.AppResult
import com.project.binar.okariru.core.network.asAppResult
import com.project.binar.okariru.core.network.runApiCatching
import com.project.binar.okariru.data.auth.remote.AuthApi
import com.project.binar.okariru.data.pinjaman.dto.PinjamanDto
import com.project.binar.okariru.data.pinjaman.dto.PinjamanTransactionRequestDto
import com.project.binar.okariru.data.pinjaman.dto.PinjamanTransactionResponseDto
import com.project.binar.okariru.data.pinjaman.remote.PinjamanApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.MultipartBody

class PinjamanRepository internal constructor(
    private val api: PinjamanApi,
    private val json: Json,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {

    suspend fun getLoanTypes(): AppResult<List<PinjamanDto>> = withContext(ioDispatcher) {
        runApiCatching(json) {
            api.getLoanTypes().content.asAppResult()
        }
    }

    suspend fun submitLoanApplication(
        request: PinjamanTransactionRequestDto,
    ): AppResult<PinjamanTransactionResponseDto> = withContext(ioDispatcher) {
        runApiCatching(json) {
            api.submitLoanApplication(request).asAppResult()
        }
    }



}