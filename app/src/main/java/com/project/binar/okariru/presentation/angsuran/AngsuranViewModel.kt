package com.project.binar.okariru.presentation.angsuran

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.binar.okariru.core.error.CommonFailure
import com.project.binar.okariru.core.network.AppResult
import com.project.binar.okariru.data.angsuran.dto.AngsuranDto
import com.project.binar.okariru.data.angsuran.repository.AngsuranRepository
import com.project.binar.okariru.data.auth.repository.AuthRepository
import com.project.binar.okariru.data.pinjaman.repository.PinjamanRepository
import com.project.binar.okariru.data.plafond.repository.PlafondRepository
import com.project.binar.okariru.data.status_pinjaman.dto.ListPinjamanDto
import com.project.binar.okariru.data.status_pinjaman.repository.StatusPinjamanRepository
import com.project.binar.okariru.presentation.home.formatRupiah
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class AngsuranUiState(
    val isLoading: Boolean = true,
    val items: List<ListPinjamanDto> = emptyList(),
    val errorMessage: String? = null,
    val angsuranList: List<AngsuranDto> = emptyList(),
    val isLoadingDetail: Boolean = false,
    val detailErrorMessage: String? = null,
    val isPaying: Boolean = false,
    val paySuccessMessage: String? = null,
    val isLunas: Boolean = false,
    val bungaRate: Double? = null,
    val jenisPinjaman: String? = "",
    val selectedPinjaman: ListPinjamanDto? = null,
)

@HiltViewModel
class AngsuranViewModel @Inject constructor(
    private val angsuranRepository: AngsuranRepository,
    private val statusPinjamanRepository: StatusPinjamanRepository,
    private val authRepository: AuthRepository,
    private val pinjamanRepository: PinjamanRepository,
    private val plafondRepository: PlafondRepository,
): ViewModel() {

    private val _uiState = MutableStateFlow(AngsuranUiState())
    val uiState: StateFlow<AngsuranUiState> = _uiState.asStateFlow()

    init {
        getListPinjamanActive()
    }

    fun getListPinjamanActive(keyword: String? = null){
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val userId = authRepository.observeSession().firstOrNull()?.user?.id ?: return@launch
            val status = "Dicairkan"
            when (val result = statusPinjamanRepository.getListPinjaman(userId, status, keyword)) {
                is AppResult.Success -> _uiState.update { it.copy(items = result.data, isLoading = false) }
                is AppResult.Failure -> _uiState.update { it.copy(isLoading = false, errorMessage = "Gagal memuat data pengajuan") }
            }
        }
    }

    // ambil jadwal cicilan (angsuran)
    fun getAngsuranDetail(transPinjamanId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingDetail = true, detailErrorMessage = null) }
            when (val result = angsuranRepository.getAngsuranForCustomer(transPinjamanId)) {
                is AppResult.Success -> _uiState.update { it.copy(angsuranList = result.data, isLoadingDetail = false) }
                is AppResult.Failure -> _uiState.update {
                    it.copy(isLoadingDetail = false, detailErrorMessage = "Gagal memuat jadwal angsuran")
                }
            }
        }
    }

    // simpan item yang diklik di Daftar Pinjaman Aktif
    fun selectPinjaman(item: ListPinjamanDto) {
        _uiState.update { it.copy(selectedPinjaman = item) }
    }

    // ambil rate bunga produk pinjaman berdasarkan pinjamanId, buat ditampilkan di halaman Rincian Angsuran
    fun getPinjamanProduct(pinjamanId: Int) {
        viewModelScope.launch {
            when (val result = pinjamanRepository.getLoanTypes()) {
                is AppResult.Success -> {
                    val bunga = result.data.firstOrNull { it.pinjamanId == pinjamanId }?.bunga
                    val jenisPinjaman = result.data.firstOrNull {it.pinjamanId == pinjamanId}?.jenisPinjaman
                    _uiState.update { it.copy(bungaRate = bunga, jenisPinjaman = jenisPinjaman) }
                }
                is AppResult.Failure -> Unit
            }
        }
    }

    fun bayarAngsuran(transPinjamanId: Int, nominalBayar: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isPaying = true, detailErrorMessage = null, paySuccessMessage = null) }
            when (val result = angsuranRepository.bayarAngsuran(transPinjamanId, nominalBayar)) {
                is AppResult.Success -> {
                    // refresh dulu, biar sisa tagihan yang ditampilkan di pesan sukses itu data paling baru (bukan sebelum bayar)
                    when (val refreshed = angsuranRepository.getAngsuranForCustomer(transPinjamanId)) {
                        is AppResult.Success -> {
                            val sisaTagihan = refreshed.data.sumOf { it.sisaTagihan }
                            val lunas = refreshed.data.isNotEmpty() && refreshed.data.all { it.statusAngsuran == "Lunas" }

                            if (lunas) {
                                // refresh plafond biar sisa plafond yang balik kebuka gara-gara lunas langsung update
                                val userId = authRepository.observeSession().firstOrNull()?.user?.id
                                if (userId != null) {
                                    plafondRepository.refreshPlafond(userId)
                                }
                            }

                            _uiState.update {
                                it.copy(
                                    isPaying = false,
                                    angsuranList = refreshed.data,
                                    isLunas = lunas,
                                    paySuccessMessage = if (lunas) {
                                        "Selamat! Pinjaman kamu sudah lunas."
                                    } else {
                                        "Berhasil bayar sebesar ${formatRupiah(nominalBayar)}. " +
                                            "Sisa tagihan keseluruhan adalah ${formatRupiah(sisaTagihan)}."
                                    }
                                )
                            }
                        }
                        is AppResult.Failure -> {
                            _uiState.update { it.copy(isPaying = false, paySuccessMessage = result.data.message) }
                        }
                    }
                }

                is AppResult.Failure -> {
                    val pesan = (result.failure as? CommonFailure.ApiError)?.details?.firstOrNull() ?: "Gagal melakukan pembayaran"
                    _uiState.update { it.copy(isPaying = false, detailErrorMessage = pesan) }
                }
            }
        }
    }

    fun dismissPaySuccessMessage() {
        _uiState.update { it.copy(paySuccessMessage = null, isLunas = false) }
    }
}