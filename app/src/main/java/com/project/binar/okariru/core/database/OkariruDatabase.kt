package com.project.binar.okariru.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.project.binar.okariru.core.database.dao.CustomerDao
import com.project.binar.okariru.core.database.dao.PlafondDao
import com.project.binar.okariru.core.database.dao.StatusPinjamanDao
import com.project.binar.okariru.core.database.entity.CustomerEntity
import com.project.binar.okariru.core.database.entity.PlafondEntity
import com.project.binar.okariru.core.database.entity.StatusPinjamanEntity

@Database(
    entities = [CustomerEntity::class, PlafondEntity::class, StatusPinjamanEntity::class],
    version = 4,
    exportSchema = true,
)
abstract class OkariruDatabase : RoomDatabase() {

    abstract fun plafondDao(): PlafondDao
    abstract fun customerDao(): CustomerDao
    abstract fun statusPinjamanDao(): StatusPinjamanDao

    companion object {
        const val NAME = "okariru.db"
    }
}