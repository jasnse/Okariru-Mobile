package com.project.binar.okariru.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.project.binar.okariru.data.plafond.dto.PlafondDto
import com.project.binar.okariru.data.status_pinjaman.dto.listPinjamanDto
import kotlin.time.Instant

@Entity(tableName = "pinjaman_transaction")
data class StatusPinjamanEntity(
    @PrimaryKey val transPinjamanId: Int,
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

fun StatusPinjamanEntity.toDTO(): listPinjamanDto = listPinjamanDto(
    transPinjamanId = transPinjamanId,
    kodeTransaksi = kodeTransaksi,
    customerId = customerId,
    customerName = customerName,
    pinjamanId = pinjamanId,
    tanggalPengajuan = tanggalPengajuan,
    tanggalReview = tanggalReview,
    tanggalApproval = tanggalApproval,
    nominalPinjaman = nominalPinjaman,
    tenor = tenor,
    statusPengajuan = statusPengajuan,
    noteMarketing = noteMarketing,
    noteBm = noteBm,
    noteBackOffice = noteBackOffice,
    lastUpdate = lastUpdate,
    lastUpdateBy = lastUpdateBy,
    jenisPinjaman = jenisPinjaman
)

fun listPinjamanDto.toEntity(): StatusPinjamanEntity = StatusPinjamanEntity(
    transPinjamanId = transPinjamanId,
    kodeTransaksi = kodeTransaksi,
    customerId = customerId,
    customerName = customerName,
    pinjamanId = pinjamanId,
    tanggalPengajuan = tanggalPengajuan,
    tanggalReview = tanggalReview,
    tanggalApproval = tanggalApproval,
    nominalPinjaman = nominalPinjaman,
    tenor = tenor,
    statusPengajuan = statusPengajuan,
    noteMarketing = noteMarketing,
    noteBm = noteBm,
    noteBackOffice = noteBackOffice,
    lastUpdate = lastUpdate,
    lastUpdateBy = lastUpdateBy,
    jenisPinjaman = jenisPinjaman
)

private fun String?.toInstantOrNull(): Instant? =
    this?.let { runCatching { Instant.parse(it) }.getOrNull() }