package com.project.binar.okariru.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.project.binar.okariru.core.database.entity.PlafondEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlafondDao {
    @Query("SELECT * FROM mst_plafond WHERE userId = :userid ORDER BY plafondId ASC")
    fun observeList(userid: Int): Flow<List<PlafondEntity>>

    @Query("SELECT COUNT(*) FROM mst_plafond")
    suspend fun count(): Int

    @Upsert
    suspend fun upsertAll(items: List<PlafondEntity>)

    @Query("DELETE FROM mst_plafond")
    suspend fun clear()

    @Transaction
    suspend fun replaceAll(items: List<PlafondEntity>) {
        clear()
        upsertAll(items)
    }


}