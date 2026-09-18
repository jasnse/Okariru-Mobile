package com.project.binar.okariru.data.auth.dto

import kotlinx.serialization.Serializable

@Serializable
data class CustomerDTO(
    val customerId: Int,
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
    val createdAt: String,
    val updatedAt: String? = null,
    val roleCustomer: String
)

@Serializable
data class CustomerUpdateRequestDto(
    val sidName: String,
    val alamat: String,
    val pekerjaan: String,
    val pendapatan: Int,
    val maritalStatus: String,
    val noRekening: String,
    val tempatLahir: String,
    val tanggalLahir: String,
    val gender: String,
)

@Serializable
data class FcmTokenRequestDto(
    val fcmToken: String,
)