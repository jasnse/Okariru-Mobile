package com.project.binar.okariru.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.project.binar.okariru.core.database.entity.CustomerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerDao {

    @Query("SELECT * FROM mst_customer WHERE customerId = :customerId ORDER BY customerId ASC")
    fun observeList(customerId: Int): Flow<List<CustomerEntity>>

    @Upsert
    suspend fun upsertAll(items: List<CustomerEntity>)

    @Query("DELETE FROM mst_customer")
    suspend fun clear()

    @Transaction
    suspend fun replaceAll(items: List<CustomerEntity>) {
        clear()
        upsertAll(items)
    }
}