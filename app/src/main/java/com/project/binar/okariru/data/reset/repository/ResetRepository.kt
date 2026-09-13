package com.project.binar.okariru.data.reset.repository

import com.project.binar.okariru.core.database.dao.PlafondDao
import com.project.binar.okariru.core.network.AppResult
import com.project.binar.okariru.core.network.asAppResult
import com.project.binar.okariru.core.network.runApiCatching
import com.project.binar.okariru.data.plafond.dto.PlafondDto
import com.project.binar.okariru.data.plafond.remote.PlafondApi
import com.project.binar.okariru.data.reset.dto.ResetPasswordRequestDto
import com.project.binar.okariru.data.reset.dto.ValidateOtpResponseDto
import com.project.binar.okariru.data.reset.remote.ResetPasswordApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class ResetRepository internal constructor(
    private val api: ResetPasswordApi,
    private val json: Json,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    suspend fun generateOtpRepo(email: String): AppResult<String> = withContext(ioDispatcher) {
        runApiCatching(json) {
            api.generateOTp(email).asAppResult()
        }
    }

    suspend fun validateOtp(email: String, otp:String): AppResult<ValidateOtpResponseDto> = withContext(ioDispatcher) {
        runApiCatching(json) {
            api.validateOtp(email, otp).asAppResult()
        }
    }


    suspend fun changePassword(resetToken: String, newPassword: String): AppResult<String> = withContext(ioDispatcher) {
        runApiCatching(json) {
            api.changePassword(resetToken, ResetPasswordRequestDto(newPassword)).asAppResult()
        }
    }
}

