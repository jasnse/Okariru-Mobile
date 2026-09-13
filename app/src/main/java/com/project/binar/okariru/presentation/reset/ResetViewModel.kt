package com.project.binar.okariru.presentation.reset

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.binar.okariru.core.error.CommonFailure
import com.project.binar.okariru.core.network.AppResult
import com.project.binar.okariru.data.reset.repository.ResetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class ForgotPasswordUiState(
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val otpSentTo: String? = null,
    val resetToken: String? = null,
    val passwordChanged: Boolean = false,
)

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val resetRepository: ResetRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    fun consumeOtpSentEvent() {
        _uiState.update { it.copy(otpSentTo = null) }

    }
    fun consumeResetTokenEvent() {
        _uiState.update { it.copy(resetToken = null) }
    }
    fun consumePasswordChangedEvent() {
        _uiState.update { it.copy(passwordChanged = false) }
    }



    fun sendOtp(email: String) {
        if (email.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Email wajib diisi") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
            when (val result = resetRepository.generateOtpRepo(email)) {
                is AppResult.Success -> _uiState.update {
                    it.copy(isSubmitting = false, otpSentTo = email)
                }

                is AppResult.Failure -> _uiState.update {
                    val message = when (val failure = result.failure){
                        is CommonFailure.ApiError -> failure.details.firstOrNull() ?: "Email Tidak ditemukan"
                        is CommonFailure.Network -> "Tidak dapat terhubung ke server. Periksa koneksi internet Anda."
                        else -> {"Gagal mengirim kode OTP"}
                    }
                    it.copy(isSubmitting = false, errorMessage = message)
                }
            }
        }

    }

    fun validateOtp(email: String, otp: String){
        if (email.isBlank() || otp.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Email dan OTP Tidak ada") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
            when(val result = resetRepository.validateOtp(email, otp)) {
                is AppResult.Success -> _uiState.update {
                    it.copy(isSubmitting = false, otpSentTo = email, resetToken = result.data.resetToken)
                }

                is AppResult.Failure -> _uiState.update {
                    val message = when (val failure = result.failure){
                        is CommonFailure.ApiError -> failure.details.firstOrNull() ?: "gagal validate otp"
                        is CommonFailure.Network -> "Tidak dapat terhubung ke server. Periksa koneksi internet Anda."
                        else -> {"Gagal mengirim kode OTP"}
                    }
                    it.copy(isSubmitting = false, errorMessage = message)
                }
            }
        }
    }

    fun changePassword(newPassword: String, resetToken: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }

            when (val result = resetRepository.changePassword(resetToken, newPassword)) {
                is AppResult.Success -> _uiState.update {
                    it.copy(isSubmitting = false, errorMessage = null, passwordChanged = true)
                }
                is AppResult.Failure -> _uiState.update {
                    val message = when (val failure = result.failure) {
                        is CommonFailure.ApiError -> failure.details.firstOrNull() ?: "gagal ganti password"
                        is CommonFailure.Network -> "Tidak dapat terhubung ke server. Periksa koneksi internet Anda."
                        else -> "Gagal mengganti password"
                    }
                    it.copy(isSubmitting = false, errorMessage = message)
                }
            }
        }
    }

}