package com.project.binar.okariru.presentation.status_pinjaman

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.binar.okariru.core.network.AppResult
import com.project.binar.okariru.data.auth.repository.AuthRepository
import com.project.binar.okariru.data.status_pinjaman.dto.ListPinjamanDto
import com.project.binar.okariru.data.status_pinjaman.repository.StatusPinjamanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class StatusPinjamanUiState(
    val isLoading: Boolean = true,
    val items: List<ListPinjamanDto> = emptyList(),
    val errorMessage: String? = null,
)
@HiltViewModel
class StatusPinjamanViewModel @Inject constructor(
    private val statusPinjamanRepository: StatusPinjamanRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatusPinjamanUiState())
    val uiState: StateFlow<StatusPinjamanUiState> = _uiState.asStateFlow()

    init {
        observeSessionAndLoad()
    }

    // reaktif terhadap perubahan sesi (ganti akun tanpa restart app), bukan cuma ambil sekali saat init
    private fun observeSessionAndLoad() {
        viewModelScope.launch {
            authRepository.observeSession()
                .map { it?.user?.id }
                .distinctUntilChanged()
                .collectLatest { userId ->
                    if (userId == null) {
                        _uiState.update {
                            it.copy(isLoading = false, items = emptyList(), errorMessage = "Sesi tidak ditemukan")
                        }
                        return@collectLatest
                    }

                    launch { refresh() }

                    statusPinjamanRepository.observeStatusPinjaman(userId).collect { list ->
                        _uiState.update { it.copy(items = list, isLoading = false) }
                    }
                }
        }
    }

    fun loadListPinjaman() {
        refresh()
    }

    fun refresh(status: String? = null, keyword: String? = null) {
        viewModelScope.launch {
            val userId = authRepository.observeSession().firstOrNull()?.user?.id ?: return@launch
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            if (statusPinjamanRepository.refreshStatusPinjaman(userId, status, keyword) is AppResult.Failure) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Gagal memuat data pengajuan") }
            }
        }
    }

}

