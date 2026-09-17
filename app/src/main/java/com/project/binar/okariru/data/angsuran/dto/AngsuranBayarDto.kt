package com.project.binar.okariru.data.angsuran.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AngsuranBayarRequest(
    val transPinjamanId: Int,
    val nominalBayar: Int,
)

@Serializable
data class AngsuranBayarResponse(
    @SerialName("Message") val message: String,
)
