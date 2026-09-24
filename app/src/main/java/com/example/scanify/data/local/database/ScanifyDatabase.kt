package com.example.scanify.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.scanify.core.constants.AppConstants
import com.example.scanify.data.local.dao.GeneratedQRDao
import com.example.scanify.data.local.dao.ScanHistoryDao
import com.example.scanify.data.local.entity.GeneratedQREntity
import com.example.scanify.data.local.entity.ScanHistoryEntity

@Database(
    entities = [ScanHistoryEntity::class, GeneratedQREntity::class],
    version = 4,
    exportSchema = false
)
abstract class ScanifyDatabase : RoomDatabase() {

    abstract fun scanHistoryDao(): ScanHistoryDao
    abstract fun generatedQRDao(): GeneratedQRDao

    companion object {
        @Volatile
        private var INSTANCE: ScanifyDatabase? = null

        fun getInstance(context: Context): ScanifyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ScanifyDatabase::class.java,
                    AppConstants.DATABASE_NAME
                ).fallbackToDestructiveMigration(dropAllTables = true).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
