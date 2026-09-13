package com.project.binar.okariru.data.auth.repository


import com.project.binar.okariru.core.database.dao.CustomerDao
import com.project.binar.okariru.core.database.dao.PlafondDao
import com.project.binar.okariru.core.database.entity.toDTO
import com.project.binar.okariru.core.database.entity.toEntity
import com.project.binar.okariru.core.network.AppResult
import com.project.binar.okariru.core.network.asAppResult
import com.project.binar.okariru.core.network.map
import com.project.binar.okariru.core.network.requirePayload
import com.project.binar.okariru.core.network.runApiCatching
import com.project.binar.okariru.data.auth.local.AuthSessionLocalDataSource
import com.project.binar.okariru.data.auth.dto.AuthSession
import com.project.binar.okariru.data.auth.dto.AuthUser
import com.project.binar.okariru.data.auth.dto.CustomerDTO
import com.project.binar.okariru.data.auth.dto.CustomerUpdateRequestDto
import com.project.binar.okariru.data.auth.dto.LoginRequestDto
import com.project.binar.okariru.data.auth.dto.LoginResponseDto
import com.project.binar.okariru.data.auth.remote.AuthApi
import com.project.binar.okariru.data.plafond.dto.PlafondDto
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.util.concurrent.TimeUnit

// Masa berlaku sesi lokal -- dipakai supaya user
// tidak perlu login ulang selama durasi ini
private val SESSION_TTL_MILLIS = TimeUnit.MINUTES.toMillis(60)

class AuthRepository internal constructor(
    private val localDataSource: AuthSessionLocalDataSource,
    private val apiHit: AuthApi,

    private val json: Json,
    private val dao: CustomerDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val clock: () -> Long = System::currentTimeMillis,
) {

    suspend fun login(credentials: LoginRequestDto): AppResult<AuthSession>
    {

        return withContext(ioDispatcher) {
            runApiCatching(json) {
                val envelopeLogin = apiHit.login(
                    LoginRequestDto(username = credentials.username, password = credentials.password)
                )
                envelopeLogin.asAppResult()
                    .map { payload -> AuthSession(
                        user = AuthUser(name = payload.username, roles = listOf(payload.role), id = payload.userId),
                        accessToken = payload.token,
                        expiresAtMillis = clock() + SESSION_TTL_MILLIS) }
                    .also { result ->
                        if (result is AppResult.Success) localDataSource.save(result.data)
                    }
            }
        }
    }

    fun observeSession(): Flow<AuthSession?> = localDataSource.observe()
        .map { session -> session?.takeUnless { it.isExpiredAt(clock()) } }

    fun observeCustomer(customerId: Int): Flow<CustomerDTO?> =
        dao.observeList(customerId).map { it.firstOrNull()?.toDTO()}

    suspend fun refreshCustomer(customerId: Int): AppResult<Unit> = withContext(ioDispatcher) {
        runApiCatching(json) {
            val dto = apiHit.getCustById(customerId)
            dao.replaceAll(listOf(dto.toEntity()))
            Unit.asAppResult()
        }
    }

    suspend fun updateProfile(request: CustomerUpdateRequestDto): AppResult<String> = withContext(ioDispatcher) {
        runApiCatching(json) {
            apiHit.updateMyProfile(request).asAppResult()
        }
    }


    suspend fun logout(){
        localDataSource.clear()
    }
}