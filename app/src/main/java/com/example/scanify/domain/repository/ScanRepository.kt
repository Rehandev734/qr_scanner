package com.example.scanify.domain.repository

import com.example.scanify.domain.model.ScanResult
import kotlinx.coroutines.flow.Flow

interface ScanRepository {
    fun getAllScans(): Flow<List<ScanResult>>
    fun getFavorites(): Flow<List<ScanResult>>
    fun searchScans(query: String): Flow<List<ScanResult>>
    suspend fun getScanById(id: Long): ScanResult?
    suspend fun insertScan(scan: ScanResult): Long
    suspend fun deleteScan(scan: ScanResult)
    suspend fun deleteAllScans()
    suspend fun toggleFavorite(id: Long)
    suspend fun getRecentScans(limit: Int): List<ScanResult>
    suspend fun getScanCount(): Int
    fun getScansByType(type: String): Flow<List<ScanResult>>
    fun getScansByDateRange(startTime: Long, endTime: Long): Flow<List<ScanResult>>
    fun getScansByTypeAndDateRange(type: String, startTime: Long, endTime: Long): Flow<List<ScanResult>>
    suspend fun deleteScansByIds(ids: List<Long>)
    suspend fun addFavoritesByIds(ids: List<Long>)
    suspend fun removeFavoritesByIds(ids: List<Long>)
    suspend fun getScansCountByDate(startOfDay: Long, endOfDay: Long): Int
    suspend fun getScanTypeDistribution(): Map<String, Int>
    fun searchScansByDateRange(query: String, startTime: Long, endTime: Long): Flow<List<ScanResult>>
}
