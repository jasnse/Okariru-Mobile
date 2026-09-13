package com.project.binar.okariru.data.auth.repository

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.binar.okariru.core.error.AppFailure
import com.project.binar.okariru.core.error.CommonFailure
import com.project.binar.okariru.core.network.AppResult
import com.project.binar.okariru.data.auth.dto.LoginRequestDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    sealed class PopupState {
        object Idle : PopupState()
        data class Show(val isSuccess: Boolean, val message: String) : PopupState()
    }

    private val _popupState = MutableStateFlow<PopupState>(PopupState.Idle)
    val popupState: StateFlow<PopupState> = _popupState.asStateFlow()

    //save session -> ga perlu login kalau token masih blm expired
    init {
        observeSession()
    }
    private fun observeSession() {
        viewModelScope.launch {
            authRepository.observeSession().collect { session ->
                _uiState.update { state ->
                    state.copy(
                        status = if (session == null) {
                            AuthStatus.UNAUTHENTICATED
                        } else {
                            AuthStatus.AUTHENTICATED
                        },
                        user = session?.user,
                    )
                }
            }
        }
    }

    fun login(username: String, password: String) {
        if (_uiState.value.isSubmitting) return
        val credentials = LoginRequestDto(username = username, password = password)
        if (username.isEmpty() || password.isEmpty()) {
            _uiState.update { state ->
                state.copy(
                    errorMessage = "Email dan password tidak boleh kosong",
                )
            }
            return
        }

        viewModelScope.launch {
            markSubmitting()
            when (val result = authRepository.login(credentials)) {
                is AppResult.Success -> {
                    _uiState.update{ state ->
                        state.copy(
                            errorMessage = null,
                            status = AuthStatus.AUTHENTICATED
                        )
                    }
                    _popupState.value = PopupState.Show(
                        isSuccess = true,
                        message = "Login berhasil! Selamat datang."
                    )
                }
                is AppResult.Failure -> {
                    val message = result.failure.toMessage()
                    _uiState.update { state ->
                        state.copy(errorMessage = message)
                    }
                    _popupState.value = PopupState.Show(
                        isSuccess = false,
                        message = message
                    )
                }
            }

            markIdle()
        }
    }

    private fun AppFailure.toMessage(): String = when (this) {
        is CommonFailure.Network -> "Tidak dapat terhubung ke server. Periksa koneksi internet Anda."
        is CommonFailure.Unauthorized -> "Username atau password salah."
        is CommonFailure.ApiError -> details.firstOrNull() ?: "Login gagal. Silakan coba lagi."
        is CommonFailure.Unexpected -> "Terjadi kesalahan tak terduga. Silakan coba lagi."
        else -> "Login gagal. Silakan coba lagi."
    }

    private fun markSubmitting() {
        _uiState.update { it.copy(isSubmitting = true, failure = null) }
    }

    private fun markIdle() {
        _uiState.update { it.copy(isSubmitting = false) }
    }

    fun dismissPopup() {
        _popupState.value = PopupState.Idle
    }

    fun logout(){
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}