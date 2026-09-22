package com.project.binar.okariru.data.auth.repository

import com.project.binar.okariru.core.error.CommonFailure
import com.project.binar.okariru.core.network.AppResult
import com.project.binar.okariru.data.auth.dto.RegisterResponse
import com.project.binar.okariru.presentation.register.RegisterFormState
import com.project.binar.okariru.presentation.register.RegisterUiState
import com.project.binar.okariru.presentation.register.RegisterViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(),
) : TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}

private fun validForm() = RegisterFormState(
    userName = "jason123",
    sidName = "Jason",
    email = "jason@example.com",
    password = "password123",
    confirmPassword = "password123",
    nik = "1234567890123456",
    tempatLahir = "Jakarta",
    tanggalLahir = "2000-01-01",
    alamat = "Jl. Merdeka",
    pekerjaan = "Karyawan",
    pendapatan = "5000000",
    maritalStatus = "Belum Kawin",
    gender = "Laki-laki",
    noRekening = "1234567890",
)

// Robolectric wajib di sini: RegisterViewModel.validateFields() manggil android.util.Patterns.EMAIL_ADDRESS,
// dan itu selalu null di JVM unit test polos (stub android.jar gak jalanin static initializer aslinya).
// MockK gak bisa nolongin karena EMAIL_ADDRESS itu Java "public static final" FIELD, bukan method/getter --
// cuma dibaca langsung (getstatic), gak ada pemanggilan fungsi buat di-intercept MockK.
// Robolectric jalanin bytecode Android asli, jadi field itu ke-isi beneran tanpa perlu di-mock.
@RunWith(RobolectricTestRunner::class)
class RegisterViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val registerRepository = mockk<RegisterRepository>()
    private val viewModel = RegisterViewModel(registerRepository)

    @Test
    fun `menolak submit dan tidak memanggil repository kalau no rekening bukan 10 digit`(): Unit = runTest {
        viewModel.updateForm { validForm().copy(noRekening = "123") }

        viewModel.register()

        val state = viewModel.uiState.value
        assertTrue(state is RegisterUiState.Error)
        assertEquals("No rekening harus 10 digit angka", (state as RegisterUiState.Error).message)
        coVerify(exactly = 0) { registerRepository.register(any()) }
    }

    @Test
    fun `submit sukses memanggil repository dan set state Success`() = runTest {
        val response = RegisterResponse(
            customerId = 1, userName = "jason123", sidName = "Jason", nik = "1234567890123456",
            tempatLahir = "Jakarta", tanggalLahir = "2000-01-01", alamat = "Jl. Merdeka",
            pekerjaan = "Karyawan", pendapatan = 5000000, maritalStatus = "Belum Kawin",
            gender = "Laki-laki", noRekening = "1234567890", createdAt = "2026-01-01",
            roleCustomer = "CUSTOMER",
        )
        coEvery { registerRepository.register(any()) } returns AppResult.success(response)

        viewModel.updateForm { validForm() }
        viewModel.register()

        assertEquals(RegisterUiState.Success, viewModel.uiState.value)
        coVerify(exactly = 1) { registerRepository.register(any()) }
    }

    @Test
    fun `submit gagal menampilkan pesan dari server`() = runTest {
        coEvery { registerRepository.register(any()) } returns
                AppResult.failure(CommonFailure.ApiError(details = listOf("Email sudah terdaftar")))

        viewModel.updateForm { validForm() }
        viewModel.register()

        val state = viewModel.uiState.value
        assertTrue(state is RegisterUiState.Error)
        assertEquals("Email sudah terdaftar", (state as RegisterUiState.Error).message)
    }
}