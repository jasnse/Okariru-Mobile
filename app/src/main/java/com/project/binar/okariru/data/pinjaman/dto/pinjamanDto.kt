package com.project.binar.okariru.data.pinjaman.dto

import kotlinx.serialization.Serializable

@Serializable
data class PinjamanDto(
    val pinjamanId: Int,
    val jenisPinjaman: String,
    val deskripsiPinjaman: String? = null,
    val bunga: Double? = null,
    val biayaLainnya: Double? = null,
)

/** Wrapper generic untuk parse response `Page<T>` dari Spring (kita cuma butuh field `content`). */
@Serializable
data class PageResponse<T>(
    val content: List<T> = emptyList(),
)

@Serializable
data class PinjamanTransactionRequestDto(
    val customerId: Int,
    val pinjamanId: Int,
    val nominalPinjaman: Int,
    val tenor: Int,
)

@Serializable
data class PinjamanTransactionResponseDto(
    val transPinjamanId: Int? = null,
    val kodeTransaksi: String? = null,
    val customerId: Int? = null,
    val pinjamanId: Int? = null,
    val nominalPinjaman: Int? = null,
    val tenor: Int? = null,
    val statusPengajuan: String? = null,
)

