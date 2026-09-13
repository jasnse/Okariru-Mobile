package com.project.binar.okariru.data.auth.dto

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val userName: String,
    val sidName: String,
    val email: String,
    val password: String,
    val nik: String,
    val tempatLahir: String,
    val tanggalLahir: String,   // format "yyyy-MM-dd"
    val alamat: String,
    val pekerjaan: String,
    val pendapatan: Int,
    val maritalStatus: String,
    val gender: String,
    val noRekening: String
)

@Serializable
data class RegisterResponse(
    val customerId: Int,
    val userName: String,
    val sidName: String,
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