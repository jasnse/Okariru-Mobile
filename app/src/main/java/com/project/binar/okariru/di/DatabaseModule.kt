package com.project.binar.okariru.di

import android.content.Context
import androidx.room.Room
import com.project.binar.okariru.core.database.OkariruDatabase
import com.project.binar.okariru.core.database.dao.CustomerDao
import com.project.binar.okariru.core.database.dao.PlafondDao
import com.project.binar.okariru.core.database.dao.StatusPinjamanDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): OkariruDatabase =
        Room.databaseBuilder(context, OkariruDatabase::class.java, OkariruDatabase.NAME)
            //.addMigrations(*BCAF_MIGRATIONS)
            .fallbackToDestructiveMigration(dropAllTables = true)
            .fallbackToDestructiveMigrationOnDowngrade(dropAllTables = true)
            .build()

    @Provides
    @Singleton
    fun providePlafondDao(database: OkariruDatabase): PlafondDao =
        database.plafondDao()

    @Provides
    @Singleton
    fun provideCustomerDao(database: OkariruDatabase): CustomerDao =
        database.customerDao()

    @Provides
    @Singleton
    fun provideStatusPinjamanDao(database: OkariruDatabase): StatusPinjamanDao =
        database.statusPinjamanDao()
}