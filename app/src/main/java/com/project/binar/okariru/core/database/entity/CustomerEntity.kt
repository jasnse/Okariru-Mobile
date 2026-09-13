package com.project.binar.okariru.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.project.binar.okariru.data.auth.dto.CustomerDTO
import com.project.binar.okariru.data.plafond.dto.PlafondDto
import kotlin.time.Instant


@Entity(tableName = "mst_customer")
data class CustomerEntity(
    @PrimaryKey val customerId: Int,
    val userName: String,
    val sidName: String?,
    val email: String,
    val nik: String,
    val tempatLahir: String,
    val tanggalLahir: String,
    val alamat: String,
    val pekerjaan: String,
    val pendapatan: Int,
    val maritalStatus: String,
    val gender: String,
    val noRekening: String,
    val roleCustomer: String,
    val createdAt: String,
    val updatedAt: String?,
)

fun CustomerEntity.toDTO(): CustomerDTO = CustomerDTO(
    customerId = customerId,
    userName = userName,
    sidName = sidName,
    email = email,
    nik = nik,
    tempatLahir = tempatLahir,
    tanggalLahir = tanggalLahir,
    alamat = alamat,
    pekerjaan = pekerjaan,
    pendapatan = pendapatan,
    maritalStatus = maritalStatus,
    gender = gender,
    noRekening = noRekening,
    createdAt = createdAt,
    updatedAt = updatedAt,
    roleCustomer = roleCustomer
)

fun CustomerDTO.toEntity(): CustomerEntity = CustomerEntity(
    customerId = customerId,
    userName = userName,
    sidName = sidName,
    email = email,
    nik = nik,
    tempatLahir = tempatLahir,
    tanggalLahir = tanggalLahir,
    alamat = alamat,
    pekerjaan = pekerjaan,
    pendapatan = pendapatan,
    maritalStatus = maritalStatus,
    gender = gender,
    noRekening = noRekening,
    createdAt = createdAt,
    updatedAt = updatedAt,
    roleCustomer = roleCustomer
)

private fun String?.toInstantOrNull(): Instant? =
    this?.let { runCatching { Instant.parse(it) }.getOrNull() }