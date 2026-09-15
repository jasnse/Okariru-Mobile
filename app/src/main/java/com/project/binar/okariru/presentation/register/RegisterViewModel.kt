package com.project.binar.okariru.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.binar.okariru.core.error.CommonFailure
import com.project.binar.okariru.core.network.AppResult
import com.project.binar.okariru.data.auth.dto.RegisterRequest
import com.project.binar.okariru.data.auth.repository.AuthViewModel
import com.project.binar.okariru.data.auth.repository.RegisterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeParseException
import javax.inject.Inject

data class RegisterFormState(
    val userName: String = "",
    val sidName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val nik: String = "",
    val tempatLahir: String = "",
    val tanggalLahir: String = "",
    val alamat: String = "",
    val pekerjaan: String = "",
    val pendapatan: String = "",
    val maritalStatus: String = "",
    val gender: String = "",
    val noRekening: String = ""
)

sealed interface RegisterUiState {
    data object Idle : RegisterUiState
    data object Loading : RegisterUiState
    data object Success : RegisterUiState
    data class Error(val message: String) : RegisterUiState
}

sealed class PopupState {
    object Idle : PopupState()
    data class Show(val isSuccess: Boolean, val message: String) : PopupState()
}



@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerRepository: RegisterRepository) : ViewModel() {
    val steps = listOf("Akun", "Personal", "Financial")

    private val _currentStep = MutableStateFlow(0)
    val currentStep: StateFlow<Int> = _currentStep.asStateFlow()

    private val _formState = MutableStateFlow(RegisterFormState())
    val formState: StateFlow<RegisterFormState> = _formState.asStateFlow()

    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    private val _popupState = MutableStateFlow<PopupState>(PopupState.Idle)
    val popupStates: StateFlow<PopupState> = _popupState.asStateFlow()

    //kalau pindah2 tab form, tab form sebelumnya gak ke reset
    fun updateForm(update: (RegisterFormState) -> RegisterFormState) {
        _formState.value = update(_formState.value)
    }

    fun nextStep() {
        if (_currentStep.value < steps.lastIndex) _currentStep.value++
    }

    fun previousStep() {
        if (_currentStep.value > 0) _currentStep.value--
    }

    fun goToStep(index: Int) {
        if (index in steps.indices) _currentStep.value = index
    }

    fun dismissPopup() {
        _popupState.value = PopupState.Idle
    }

    fun register() {
        if (_uiState.value == RegisterUiState.Loading) return


        val form = _formState.value

        val allFields = listOf(
            form.userName,
            form.sidName,
            form.email,
            form.password,
            form.confirmPassword,
            form.nik,
            form.tempatLahir,
            form.tanggalLahir,
            form.alamat,
            form.pekerjaan,
            form.pendapatan,
            form.maritalStatus,
            form.gender,
            form.noRekening
        )

        if (allFields.any { it.isBlank() }) {
            _uiState.value = RegisterUiState.Error("Semua field wajib diisi")
            return
        }

        if(form.password != form.confirmPassword){
            _uiState.value = RegisterUiState.Error("Konfirmasi Password Tidak Sesuai")
            return
        }

        val pendapatanInt = form.pendapatan.toIntOrNull()
        if (form.pendapatan.isNotBlank() && pendapatanInt == null) {
            _uiState.value = RegisterUiState.Error("Pendapatan harus berupa angka")
            return
        }

        if (form.tanggalLahir.isNotBlank()) {
            try {
                LocalDate.parse(form.tanggalLahir)
            } catch (e: DateTimeParseException) {
                _uiState.value = RegisterUiState.Error("Format tanggal lahir tidak valid, gunakan yyyy-MM-dd")
                return
            }
        }

        viewModelScope.launch {
            _uiState.value = RegisterUiState.Loading

            val request = RegisterRequest(
                userName = form.userName,
                sidName = form.sidName,
                email = form.email,
                password = form.password,
                nik = form.nik,
                tempatLahir = form.tempatLahir,
                tanggalLahir = form.tanggalLahir,
                alamat = form.alamat,
                pekerjaan = form.pekerjaan,
                pendapatan = pendapatanInt ?: 0,
                maritalStatus = form.maritalStatus,
                gender = form.gender,
                noRekening = form.noRekening
            )

            when (val result = registerRepository.register(request)) {
                is AppResult.Success -> {
                    _uiState.value = RegisterUiState.Success
                    _popupState.value = PopupState.Show(
                        isSuccess = true,
                        message = "Register berhasil! Silahkan Login kembali."
                    )
                }
                is AppResult.Failure -> {
                    val message = when (val failure = result.failure) {
                        is CommonFailure.Unauthorized -> "Sesi tidak valid"
                        is CommonFailure.Network -> "Tidak dapat terhubung ke server"
                        is CommonFailure.ApiError -> failure.details.firstOrNull() ?: "Registrasi gagal"
                        else -> "Terjadi kesalahan, silakan coba lagi"
                    }
                    _uiState.value = RegisterUiState.Error(message)
                    _popupState.value = PopupState.Show(
                        isSuccess = false,
                        message = "Register Gagal! Silahkan coba kembali."
                    )
                }
            }
        }
    }
}