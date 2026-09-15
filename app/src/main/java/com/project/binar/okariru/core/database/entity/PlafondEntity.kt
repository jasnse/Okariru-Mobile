package com.project.binar.okariru.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.project.binar.okariru.data.plafond.dto.PlafondDto
import kotlin.time.Instant


@Entity(
    tableName = "mst_plafond",
//    foreignKeys = [
//        ForeignKey(
//            entity = CustomerEntity::class, // Tabel Induk (Parent)
//            parentColumns = ["customerId"],  // Primary Key di mst_customer
//            childColumns = ["userId"],       // Kolom foreign key di mst_plafond
//            onDelete = ForeignKey.CASCADE,   // Jika customer dihapus, data plafond-nya ikut terhapus
//            onUpdate = ForeignKey.CASCADE
//        )
//    ],
    // index kolom foreign key
    indices = [Index(value = ["userId"])]
)
data class PlafondEntity(
    @PrimaryKey val plafondId: Int,
    val userId: Int, // Menghubungkan ke customer_id di tabel mst_customer
    val totalPlafond: Int,
    val sisaPlafond: Long?,
    val deskripsiPlafond: String?,
    val updatedAt: String?,
    val updatedBy: Int?,
    val createdAt: String?,
    val createdBy: Int?
)

fun PlafondEntity.toDTO(): PlafondDto = PlafondDto(
    plafondId = plafondId,
    userId = userId,
    totalPlafond = totalPlafond,
    sisaPlafond = sisaPlafond,
    deskripsiPlafond = deskripsiPlafond,
    updatedAt = updatedAt,
    updatedBy = updatedBy,
    createdAt = createdAt,
    createdBy = createdBy,
)

fun PlafondDto.toEntity(): PlafondEntity = PlafondEntity(
    plafondId = plafondId,
    userId = userId,
    totalPlafond = totalPlafond,
    sisaPlafond = sisaPlafond,
    deskripsiPlafond = deskripsiPlafond,
    updatedAt = updatedAt,
    updatedBy = updatedBy,
    createdAt = createdAt,
    createdBy = createdBy,
)

private fun String?.toInstantOrNull(): Instant? =
    this?.let { runCatching { Instant.parse(it) }.getOrNull() }