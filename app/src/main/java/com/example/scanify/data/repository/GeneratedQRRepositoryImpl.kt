package com.example.scanify.data.repository

import com.example.scanify.data.local.dao.GeneratedQRDao
import com.example.scanify.data.mapper.toDomain
import com.example.scanify.data.mapper.toEntity
import com.example.scanify.domain.model.GeneratedQR
import com.example.scanify.domain.repository.GeneratedQRRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeneratedQRRepositoryImpl @Inject constructor(
    private val dao: GeneratedQRDao
) : GeneratedQRRepository {

    override fun getAllGenerated(): Flow<List<GeneratedQR>> {
        return dao.getAllGenerated().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getGeneratedFavorites(): Flow<List<GeneratedQR>> {
        return dao.getFavorites().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun searchGenerated(query: String): Flow<List<GeneratedQR>> {
        return dao.searchGenerated(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getGeneratedById(id: Long): GeneratedQR? {
        return dao.getGeneratedById(id)?.toDomain()
    }

    override suspend fun insertGenerated(qr: GeneratedQR): Long {
        return dao.insertGenerated(qr.toEntity())
    }

    override suspend fun deleteGenerated(qr: GeneratedQR) {
        dao.deleteGenerated(qr.toEntity())
    }

    override suspend fun deleteAllGenerated() {
        dao.deleteAllGenerated()
    }

    override suspend fun toggleGeneratedFavorite(id: Long) {
        dao.toggleFavorite(id)
    }

    override suspend fun getRecentGenerated(limit: Int): List<GeneratedQR> {
        return dao.getRecentGenerated(limit).map { it.toDomain() }
    }

    override suspend fun getGeneratedCount(): Int {
        return dao.getGeneratedCount()
    }

    override fun getGeneratedByType(type: String): Flow<List<GeneratedQR>> {
        return dao.getGeneratedByType(type).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getGeneratedByDateRange(startTime: Long, endTime: Long): Flow<List<GeneratedQR>> {
        return dao.getGeneratedByDateRange(startTime, endTime).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getGeneratedByTypeAndDateRange(type: String, startTime: Long, endTime: Long): Flow<List<GeneratedQR>> {
        return dao.getGeneratedByTypeAndDateRange(type, startTime, endTime).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun deleteGeneratedByIds(ids: List<Long>) {
        dao.deleteGeneratedByIds(ids)
    }

    override suspend fun addGeneratedFavoritesByIds(ids: List<Long>) {
        dao.addFavoritesByIds(ids)
    }

    override suspend fun removeGeneratedFavoritesByIds(ids: List<Long>) {
        dao.removeFavoritesByIds(ids)
    }

    override suspend fun getGeneratedCountByDate(startOfDay: Long, endOfDay: Long): Int {
        return dao.getGeneratedCountByDate(startOfDay, endOfDay)
    }

    override suspend fun getGeneratedTypeDistribution(): Map<String, Int> {
        return dao.getGeneratedTypeDistribution().associate { it.type to it.count }
    }

    override fun searchGeneratedByDateRange(query: String, startTime: Long, endTime: Long): Flow<List<GeneratedQR>> {
        return dao.searchGeneratedByDateRange(query, startTime, endTime).map { entities ->
            entities.map { it.toDomain() }
        }
    }
}
