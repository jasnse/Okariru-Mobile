package com.project.binar.okariru.data.plafond.dto

import kotlinx.serialization.Serializable

@Serializable
data class PlafondDto(
    val plafondId: Int,
    val userId: Int,
    val totalPlafond: Int,
    val sisaPlafond: Long? = null,
    val deskripsiPlafond: String? = null,
    val createdBy: Int? = null,
    val createdAt: String? = null,
    val updatedBy: Int? = null,
    val updatedAt: String? = null,
)
