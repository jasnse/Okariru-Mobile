package com.project.binar.okariru.data.angsuran.dto

import kotlinx.serialization.Serializable

@Serializable
data class AngsuranDto(
    val angsuranId: Int,
    val transPinjamanId: Int,
    val jumlahPokok: Long,
    val jumlahBunga: Double,
    val totalAngsuran: Int,
    val tanggalJatuhTempo: String,
    val statusAngsuran: String,
    val tenor: Int,
    val sisaTagihan: Int,
)
