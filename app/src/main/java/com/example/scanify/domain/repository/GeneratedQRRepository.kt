package com.example.scanify.domain.repository

import com.example.scanify.domain.model.GeneratedQR
import kotlinx.coroutines.flow.Flow

interface GeneratedQRRepository {
    fun getAllGenerated(): Flow<List<GeneratedQR>>
    fun getGeneratedFavorites(): Flow<List<GeneratedQR>>
    fun searchGenerated(query: String): Flow<List<GeneratedQR>>
    suspend fun getGeneratedById(id: Long): GeneratedQR?
    suspend fun insertGenerated(qr: GeneratedQR): Long
    suspend fun deleteGenerated(qr: GeneratedQR)
    suspend fun deleteAllGenerated()
    suspend fun toggleGeneratedFavorite(id: Long)
    suspend fun getRecentGenerated(limit: Int): List<GeneratedQR>
    suspend fun getGeneratedCount(): Int
    fun getGeneratedByType(type: String): Flow<List<GeneratedQR>>
    fun getGeneratedByDateRange(startTime: Long, endTime: Long): Flow<List<GeneratedQR>>
    fun getGeneratedByTypeAndDateRange(type: String, startTime: Long, endTime: Long): Flow<List<GeneratedQR>>
    suspend fun deleteGeneratedByIds(ids: List<Long>)
    suspend fun addGeneratedFavoritesByIds(ids: List<Long>)
    suspend fun removeGeneratedFavoritesByIds(ids: List<Long>)
    suspend fun getGeneratedCountByDate(startOfDay: Long, endOfDay: Long): Int
    suspend fun getGeneratedTypeDistribution(): Map<String, Int>
    fun searchGeneratedByDateRange(query: String, startTime: Long, endTime: Long): Flow<List<GeneratedQR>>
}
