package com.project.binar.okariru.presentation.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.binar.okariru.core.network.AppResult
import com.project.binar.okariru.data.auth.dto.CustomerDTO
import com.project.binar.okariru.data.auth.dto.CustomerUpdateRequestDto
import com.project.binar.okariru.data.auth.repository.AuthRepository
import com.project.binar.okariru.data.plafond.dto.PlafondDto
import com.project.binar.okariru.data.plafond.repository.PlafondRepository
import com.project.binar.okariru.presentation.home.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class profileUiState(
    val isLoading: Boolean = true,
    val userName: String = "Friend",
    val email: String = "undefined",
    val customer: CustomerDTO? = null,
    val errorMessage: String? = null,
    val isSubmitting: Boolean = false,
    val profileUpdated: Boolean = false,
)


@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(profileUiState())
    val uiState: StateFlow<profileUiState> = _uiState.asStateFlow()

    sealed class PopupState {
        object Idle : PopupState()
        data class Show(val isSuccess: Boolean, val message: String) : PopupState()
    }

    private val _popupState = MutableStateFlow<PopupState>(PopupState.Idle)
    val popupState: StateFlow<PopupState> = _popupState.asStateFlow()

    fun dismissPopup() {
        _popupState.value = PopupState.Idle
    }

    data class CustomerModel(
        val customerId: Int = 0,
        val items: List<CustomerDTO>,
    )

    fun consumeProfileUpdatedEvent() {
        _uiState.update { it.copy(profileUpdated = false) }   // reset state
    }


    init {
        viewModelScope.launch {
            val session = authRepository.observeSession().firstOrNull()
            val userId = session?.user?.id

            _uiState.update { it.copy(userName = session?.user?.name ?: "Failed to Load") }

            if (userId == null) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Sesi tidak ditemukan") }
                return@launch
            }

            authRepository.observeCustomer(userId).collect { customer ->
                _uiState.update { it.copy(customer = customer, email = customer?.email ?: "Failed to Load", isLoading = false) }
            }
        }
        refresh()
    }


    //ini hit ke API
    fun refresh() {
        viewModelScope.launch {
            val userId = authRepository.observeSession().firstOrNull()?.user?.id ?: return@launch
            if (authRepository.refreshCustomer(userId) is AppResult.Failure) {
                _uiState.update { it.copy(errorMessage = "Gagal memuat data plafon") }
            }
        }
    }

    fun updateProfile(
        sidName: String, alamat: String, pekerjaan: String, pendapatanInput: String,
        maritalStatus: String, noRekening: String, tempatLahir: String, tanggalLahir: String, gender: String
    ) {
        val pendapatan = pendapatanInput.toIntOrNull()
        if (pendapatan == null) {
            _uiState.update { it.copy(errorMessage = "Pendapatan harus berupa angka") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
            val request = CustomerUpdateRequestDto(
                sidName,
                alamat,
                pekerjaan,
                pendapatan,
                maritalStatus,
                noRekening,
                tempatLahir,
                tanggalLahir,
                gender
            )

            when (val result = authRepository.updateProfile(request)) {
                is AppResult.Success -> {
                    Log.d("ProfileVM", "update sukses: ${result.data}")
                    //refresh data
                    val userId = authRepository.observeSession().firstOrNull()?.user?.id
                    if (userId != null) authRepository.refreshCustomer(userId)
                    _uiState.update { it.copy(isSubmitting = false, profileUpdated = true) }
                    _popupState.value = PopupState.Show(
                        isSuccess = true,
                        message = "Profil berhasil diperbarui"
                    )
                }
                is AppResult.Failure -> {
                    Log.e("ProfileVM", "update gagal: ${result.failure}")
                    _uiState.update { it.copy(isSubmitting = false, errorMessage = "Gagal menyimpan profil") }
                    _popupState.value = PopupState.Show(
                        isSuccess = false,
                        message = "Gagal menyimpan profil"
                    )
                }
            }


        }

    }

}