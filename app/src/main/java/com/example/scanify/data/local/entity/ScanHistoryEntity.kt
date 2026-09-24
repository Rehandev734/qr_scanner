package com.example.scanify.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scan_history")
data class ScanHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val rawValue: String,
    val format: String,
    val scanType: String,
    val timestamp: Long,
    val isFavorite: Boolean = false
)
