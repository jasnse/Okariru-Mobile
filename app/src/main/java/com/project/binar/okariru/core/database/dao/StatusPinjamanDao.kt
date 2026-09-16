package com.project.binar.okariru.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.project.binar.okariru.core.database.entity.PlafondEntity
import com.project.binar.okariru.core.database.entity.StatusPinjamanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StatusPinjamanDao {
    @Query("SELECT * FROM pinjaman_transaction WHERE customerId = :customerId ORDER BY transPinjamanId DESC")
    fun observeList(customerId: Int): Flow<List<StatusPinjamanEntity>>

    @Query("SELECT COUNT(*) FROM pinjaman_transaction WHERE customerId = :customerId")
    suspend fun count(customerId: Int): Int

    @Upsert
    suspend fun upsertAll(items: List<StatusPinjamanEntity>)

    @Query("DELETE FROM pinjaman_transaction WHERE customerId = :customerId")
    suspend fun clearByCustomer(customerId: Int)

    @Transaction
    suspend fun replaceAll(customerId: Int, items: List<StatusPinjamanEntity>) {
        clearByCustomer(customerId)
        upsertAll(items)
    }
}
