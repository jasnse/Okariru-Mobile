package com.project.binar.okariru.data.auth.repository

import android.icu.text.RelativeDateTimeFormatter
import com.project.binar.okariru.core.network.AppResult
import com.project.binar.okariru.data.auth.dto.AuthSession
import com.project.binar.okariru.data.auth.dto.AuthUser
import com.project.binar.okariru.data.auth.dto.LoginRequestDto
import com.project.binar.okariru.data.auth.dto.LoginResponseDto
import com.project.binar.okariru.data.auth.local.AuthSessionLocalDataSource
import com.project.binar.okariru.data.auth.remote.AuthApi
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import java.io.IOException

private const val TOKEN = "header.payload.signature"

private const val NOW = 1_700_000_000L
private val CRED = AuthUser(
    name = "Jason123",
    roles = listOf("Customer"),
    id = 1,
)

@OptIn(ExperimentalCoroutinesApi::class)
private fun authRepostory(
    localDataSource: AuthSessionLocalDataSource,
    apiHit: AuthApi = mockk(),
    clock: () -> Long = System::currentTimeMillis,
) = AuthRepository(
    localDataSource = localDataSource,
    apiHit = apiHit,
    json = Json,
    dao = mockk(),
    fcmLocalStore = mockk(relaxed = true),
    ioDispatcher = UnconfinedTestDispatcher(),
    clock = clock,
)

private fun storedSession(expiredAtMills: Long): AuthSession = AuthSession(
    accessToken = TOKEN, expiresAtMillis = expiredAtMills,
    user = CRED,
)

@RunWith(Enclosed::class)
class AuthRepostioryTest {
    class Login {
        private val localDataSource = mockk<AuthSessionLocalDataSource>(relaxUnitFun = true)
        private val apiHit = mockk<AuthApi>()
        private val repository = authRepostory(localDataSource, apiHit = apiHit)

        private val loginResponse = LoginResponseDto(
            token = TOKEN,
            username = "Jason123",
            role = "Customer",
            userId = 1,
        )

        private val loginRequest = LoginRequestDto(
            username = "234234234",
            password = "12312312"
        )

        @Test
        fun `return session mapped from jwt and login response`() = runTest {
            coEvery { apiHit.login(any()) } returns loginResponse

            val result = repository.login(loginRequest)

            assert(result is AppResult.Success)
            val session = (result as AppResult.Success).data
            assert(session.user.name == loginResponse.username)
            assert(session.user.id == loginResponse.userId)
            assert(session.accessToken == loginResponse.token)
            coVerify(exactly = 1) { localDataSource.save(any()) }
        }

        @Test
        fun `should return failure when API call fails`() = runTest {
            coEvery { apiHit.login(any()) } throws Exception("Invalid credentials or Server Error")

            val result = repository.login(loginRequest)

            assert(result is AppResult.Failure)
            coVerify(exactly = 0) { localDataSource.save(any()) }
        }

        @Test
        fun `should not save session when network error occurs`() = runTest {
            coEvery { apiHit.login(any()) } throws IOException("No internet connection")

            val result = repository.login(loginRequest)

            assert(result is AppResult.Failure)
            coVerify(exactly = 0) { localDataSource.save(any()) }
        }
    }

    class observe {
        private val  localDataSource = mockk<AuthSessionLocalDataSource>()
        private var now = NOW
        private val repository = authRepostory(localDataSource, clock = {now})


        @Test
        fun `emits null when no seession is stored` () = runTest {

            every { localDataSource.observe() } returns flowOf(null)

            val session = repository.observeSession().first()

            assertNull(session)
        }

        @Test
        fun `emits stored session when it has not expired` () = runTest {
            val stored = storedSession(expiredAtMills = NOW + 1)
            every { localDataSource.observe() } returns flowOf(stored)
            val session = repository.observeSession().first()
            assertEquals(stored, session)
        }
    }
}
