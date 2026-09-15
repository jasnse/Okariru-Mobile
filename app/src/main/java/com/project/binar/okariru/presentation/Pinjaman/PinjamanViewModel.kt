package com.project.binar.okariru.presentation.Pinjaman

import android.content.Context
import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Payments
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.binar.okariru.core.network.AppResult
import com.project.binar.okariru.data.pinjaman.dto.PinjamanTransactionRequestDto
import com.project.binar.okariru.data.pinjaman.repository.PinjamanRepository
import com.project.binar.okariru.data.auth.repository.AuthRepository
import com.project.binar.okariru.data.document.repository.DocumentRepository
import com.project.binar.okariru.data.document.uriToMultipart
import com.project.binar.okariru.data.plafond.repository.PlafondRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PinjamanFormState(
    val selectedLoanTypeId: Int = 0,
    val selectedLoanTypeName: String = "",
    val nominal: Int = 0,
    val tenor: String = "",
    val sisaPlafond: Long? = 0,
)

sealed interface PinjamanUiState {
    data object Idle : PinjamanUiState
    data object Loading : PinjamanUiState
    data object Success : PinjamanUiState
    data class Error(val message: String) : PinjamanUiState
}

sealed class PopupState {
    data object Idle : PopupState()
    data class Show(val isSuccess: Boolean, val message: String) : PopupState()
}

@HiltViewModel
class PinjamanViewModel @Inject constructor(
    private val pinjamanRepository: PinjamanRepository,
    private val authRepository: AuthRepository,
    private val plafondRepository: PlafondRepository,
    private val documentRepository: DocumentRepository,

    @ApplicationContext private val appContext: Context,
) : ViewModel() {

    val steps = listOf("Tipe Pinjaman", "Simulasi", "Dokumen")

    private val _currentStep = MutableStateFlow(0)
    val currentStep: StateFlow<Int> = _currentStep.asStateFlow()

    private val _formState = MutableStateFlow(PinjamanFormState())
    val formState: StateFlow<PinjamanFormState> = _formState.asStateFlow()

    private val _loanTypes = MutableStateFlow<List<LoanTypeOption>>(emptyList())
    val loanTypes: StateFlow<List<LoanTypeOption>> = _loanTypes.asStateFlow()

    private val _isLoadingLoanTypes = MutableStateFlow(false)
    val isLoadingLoanTypes: StateFlow<Boolean> = _isLoadingLoanTypes.asStateFlow()

    private val _uiState = MutableStateFlow<PinjamanUiState>(PinjamanUiState.Idle)
    val uiState: StateFlow<PinjamanUiState> = _uiState.asStateFlow()

    private val _popupState = MutableStateFlow<PopupState>(PopupState.Idle)
    val popupState: StateFlow<PopupState> = _popupState.asStateFlow()

    private val _documents = MutableStateFlow(defaultDocuments())
    val documents: StateFlow<List<DocumentUploadItem>> = _documents.asStateFlow()

    init {
        loadLoanTypes()
    }

    fun updateForm(update: (PinjamanFormState) -> PinjamanFormState) {
        _formState.value = update(_formState.value)
    }

    //update state document
    fun updateDocumentUri(index: Int, uri: Uri, fileName: String) {
        _documents.value = _documents.value.toMutableList().also { list ->
            list[index] = list[index].copy(fileUri = uri, fileName = fileName)
        }
    }

    fun goToStep(index: Int) {
        if (index in steps.indices) _currentStep.value = index
    }

    fun nextStep() {
        if (_currentStep.value < steps.lastIndex) _currentStep.value++
    }

    fun previousStep() {
        if (_currentStep.value > 0) _currentStep.value--
    }

    fun dismissPopup() {
        _popupState.value = PopupState.Idle
    }

    fun resetForm() {
        _formState.value = PinjamanFormState()
        _currentStep.value = 0
        _uiState.value = PinjamanUiState.Idle
        _popupState.value = PopupState.Idle
        _documents.value = defaultDocuments()
    }

    private fun defaultDocuments() = listOf(
        DocumentUploadItem(Icons.Filled.Badge, "KTP", isRequired = true),
        DocumentUploadItem(Icons.Filled.Payments, "Slip Gaji", isRequired = true),
        DocumentUploadItem(Icons.Filled.Home, "Kartu Keluarga (KK)", isRequired = false),
        DocumentUploadItem(Icons.Filled.AccountBalance, "Buku Tabungan / Rekening", isRequired = false),
        DocumentUploadItem(Icons.Filled.Description, "Dokumen Lainnya", isRequired = false),
    )

    private fun loadLoanTypes() {
        viewModelScope.launch {
            _isLoadingLoanTypes.value = true
            val userId = authRepository.observeSession().firstOrNull()?.user?.id
            when (val result = pinjamanRepository.getLoanTypes()) {
                is AppResult.Success -> {
                    _loanTypes.value = result.data.map { dto ->
                        LoanTypeOption(
                            id = dto.pinjamanId,
                            title = dto.jenisPinjaman,
                            description = dto.deskripsiPinjaman.orEmpty(),
                            bunga = dto.bunga,
                            biayaLainnya = dto.biayaLainnya
                        )
                    }
                }
                is AppResult.Failure -> {
                    _uiState.value = PinjamanUiState.Error("Gagal memuat jenis pinjaman")
                }
            }
            when(val resultPlafond = plafondRepository.getPlafond(userId)){
                is AppResult.Success -> {
                    val plafondValue = resultPlafond.data.sisaPlafond
                    _formState.update { currentState ->
                        currentState.copy(sisaPlafond = plafondValue)
                    }
                }
                else -> {}
            }
            _isLoadingLoanTypes.value = false
        }
    }

    fun submitForm() {
        if (_uiState.value == PinjamanUiState.Loading) return

        val form = _formState.value
        if (form.selectedLoanTypeId == 0) {
            _uiState.value = PinjamanUiState.Error("Pilih jenis pinjaman terlebih dahulu")
            return
        }

        val nominal = form.nominal
        if (nominal == null || nominal <= 0) {
            _uiState.value = PinjamanUiState.Error("Nominal pinjaman harus berupa angka")
            return
        }

        val tenor = Regex("\\d+").find(form.tenor)?.value?.toIntOrNull()
        if (tenor == null || tenor <= 0) {
            _uiState.value = PinjamanUiState.Error("Pilih tenor pinjaman")
            return
        }

        viewModelScope.launch {
            _uiState.value = PinjamanUiState.Loading

            val userId = authRepository.observeSession().firstOrNull()?.user?.id
            val customer = userId?.let { authRepository.observeCustomer(it).firstOrNull() }
            if (customer == null) {
                _uiState.value = PinjamanUiState.Error("Data customer tidak ditemukan")
                return@launch
            }

            val request = PinjamanTransactionRequestDto(
                customerId = customer.customerId,
                pinjamanId = form.selectedLoanTypeId,
                nominalPinjaman = nominal,
                tenor = tenor,
            )

            when (val result = pinjamanRepository.submitLoanApplication(request)) {
                is AppResult.Success -> {

                    //submit loan dulu -> kebentuk transId -> pakai buat upload Document
                    val transPinjamanId = result.data.transPinjamanId
                    val fileParts = _documents.value
                        .mapNotNull { it.fileUri }
                        .map { uri -> appContext.uriToMultipart(uri) }

                    //upload document
                    val uploadOk = if (transPinjamanId != null && fileParts.isNotEmpty()) {
                        documentRepository.uploadDocuments(
                            transPinjamanId = transPinjamanId,
                            customerId = customer.customerId,
                            files = fileParts,
                        ) is AppResult.Success
                    } else {
                        true
                    }

                    _uiState.value = PinjamanUiState.Success
                    _popupState.value = if (uploadOk) {
                        PopupState.Show(
                            isSuccess = true,
                            message = "Pengajuan pinjaman berhasil dikirim."
                        )
                    } else {
                        PopupState.Show(
                            isSuccess = false,
                            message = "Pengajuan terkirim, tapi sebagian dokumen gagal diupload. Silakan upload ulang dari halaman detail pengajuan."
                        )
                    }
                }
                is AppResult.Failure -> {
                    _uiState.value = PinjamanUiState.Error("Pengajuan pinjaman gagal")
                    _popupState.value = PopupState.Show(
                        isSuccess = false,
                        message = "Pengajuan pinjaman gagal. Silakan coba lagi."
                    )
                }
            }
        }
    }
}