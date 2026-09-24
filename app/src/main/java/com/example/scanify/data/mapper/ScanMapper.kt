package com.example.scanify.data.mapper

import com.example.scanify.data.local.entity.ScanHistoryEntity
import com.example.scanify.domain.model.ScanResult

fun ScanHistoryEntity.toDomain(): ScanResult {
    return ScanResult(
        id = id,
        rawValue = rawValue,
        format = format,
        scanType = scanType,
        timestamp = timestamp,
        isFavorite = isFavorite
    )
}

fun ScanResult.toEntity(): ScanHistoryEntity {
    return ScanHistoryEntity(
        id = id,
        rawValue = rawValue,
        format = format,
        scanType = scanType,
        timestamp = timestamp,
        isFavorite = isFavorite
    )
}
