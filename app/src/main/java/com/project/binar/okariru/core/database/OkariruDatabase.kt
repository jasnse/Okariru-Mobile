package com.project.binar.okariru.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.project.binar.okariru.core.database.dao.CustomerDao
import com.project.binar.okariru.core.database.dao.PlafondDao
import com.project.binar.okariru.core.database.entity.CustomerEntity
import com.project.binar.okariru.core.database.entity.PlafondEntity

@Database(
    entities = [CustomerEntity::class, PlafondEntity::class],
    version = 3,
    exportSchema = true,
)
abstract class OkariruDatabase : RoomDatabase() {

    abstract fun plafondDao(): PlafondDao
    abstract fun customerDao(): CustomerDao

    companion object {
        const val NAME = "okariru.db"
    }
}