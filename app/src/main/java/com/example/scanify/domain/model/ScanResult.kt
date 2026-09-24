package com.example.scanify.domain.model

data class ScanResult(
    val id: Long = 0,
    val rawValue: String,
    val format: String,
    val scanType: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)
