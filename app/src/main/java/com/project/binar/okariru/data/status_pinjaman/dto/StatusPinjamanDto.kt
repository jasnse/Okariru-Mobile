package com.project.binar.okariru.data.status_pinjaman.dto

import kotlinx.serialization.Serializable

@Serializable
data class listPinjamanDto(
    val transPinjamanId: Int,
    val kodeTransaksi: String,
    val customerId: Int,
    val customerName: String,
    val pinjamanId: Int,
    val tanggalPengajuan: String? = null,
    val tanggalReview: String? = null,
    val tanggalApproval: String? = null,
    val nominalPinjaman: Int,
    val tenor: Int,
    val statusPengajuan: String? = null,
    val noteMarketing: String? = null,
    val noteBm: String? = null,
    val noteBackOffice: String? = null,
    val lastUpdate: String? = null,
    val lastUpdateBy: Int? = null,
    val jenisPinjaman: String? = null,
)

enum class StatusPengajuanExist(val label: String) {
    DISETUJUI("disetujui"),
    DIREVIEW("direview"),
    DITOLAK("ditolak"),
    PENGAJUAN("pengajuan")
}
