package com.project.binar.okariru.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.binar.okariru.core.network.AppResult
import com.project.binar.okariru.data.auth.repository.AuthRepository
import com.project.binar.okariru.data.plafond.dto.PlafondDto
import com.project.binar.okariru.data.plafond.repository.PlafondRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = true,
    val userName: String = "Friend",
    val SisaPlafond: Long? = null,
    val errorMessage: String? = null,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val plafondRepository: PlafondRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    data class PlafondModel(
//        val page: Int = 0,
//        val size: Int = 100,
//        val totalElements: Long,
//        val totalPages: Int,
        val userId: Int = 0,
        val items: List<PlafondDto>,
    )
//    {
//        val hasNextPage: Boolean get() = page + 1 < totalPages
//    }

    init {
//        loadPlafondAndUsername()
        viewModelScope.launch {
            val session = authRepository.observeSession().firstOrNull()
            val userId = session?.user?.id

            _uiState.update { it.copy(userName = session?.user?.name ?: "Friend") }


            if (userId == null) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Sesi tidak ditemukan") }
                return@launch
            }

            launch {
                authRepository.observeCustomer(userId).collect { customer ->
                    _uiState.update { it.copy(userName = customer?.sidName ?: it.userName) }
                }
            }


            plafondRepository.observePlafond(userId).collect { plafond ->
                _uiState.update { it.copy(SisaPlafond = plafond?.sisaPlafond, isLoading = false) }
            }
        }
        //ini hit ke API
        refresh()
    }

    fun loadPlafondAndUsername() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val session = authRepository.observeSession().firstOrNull()
            val userId = session?.user?.id

            if (userId == null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        userName = session?.user?.name ?: "Friend",
                        errorMessage = "Sesi tidak ditemukan",
                    )
                }
                return@launch
            }

            when (val result = plafondRepository.getPlafond(userId)) {
                is AppResult.Success -> _uiState.update {
                    it.copy(
                        isLoading = false,
                        userName = session.user.name,
                        SisaPlafond = result.data.sisaPlafond,
                        errorMessage = null,
                    )
                }
                is AppResult.Failure -> _uiState.update {
                    it.copy(
                        isLoading = false,
                        userName = session.user.name,
                        errorMessage = "Gagal memuat data plafon",
                    )
                }
            }
        }
    }


    //ini hit ke API
    fun refresh() {
        viewModelScope.launch {
            val userId = authRepository.observeSession().firstOrNull()?.user?.id ?: return@launch
            if (plafondRepository.refreshPlafond(userId) is AppResult.Failure) {
                _uiState.update { it.copy(errorMessage = "Gagal memuat data plafon") }
            }
            authRepository.refreshCustomer(userId)
        }
    }
}
