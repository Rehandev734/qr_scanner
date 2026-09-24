package com.example.scanify.data.repository

import com.example.scanify.data.local.dao.ScanHistoryDao
import com.example.scanify.data.mapper.toDomain
import com.example.scanify.data.mapper.toEntity
import com.example.scanify.domain.model.ScanResult
import com.example.scanify.domain.repository.ScanRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScanRepositoryImpl @Inject constructor(
    private val dao: ScanHistoryDao
) : ScanRepository {

    override fun getAllScans(): Flow<List<ScanResult>> {
        return dao.getAllScans().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getFavorites(): Flow<List<ScanResult>> {
        return dao.getFavorites().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun searchScans(query: String): Flow<List<ScanResult>> {
        return dao.searchScans(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getScanById(id: Long): ScanResult? {
        return dao.getScanById(id)?.toDomain()
    }

    override suspend fun insertScan(scan: ScanResult): Long {
        return dao.insertScan(scan.toEntity())
    }

    override suspend fun deleteScan(scan: ScanResult) {
        dao.deleteScan(scan.toEntity())
    }

    override suspend fun deleteAllScans() {
        dao.deleteAllScans()
    }

    override suspend fun toggleFavorite(id: Long) {
        dao.toggleFavorite(id)
    }

    override suspend fun getRecentScans(limit: Int): List<ScanResult> {
        return dao.getRecentScans(limit).map { it.toDomain() }
    }

    override suspend fun getScanCount(): Int {
        return dao.getScanCount()
    }

    override fun getScansByType(type: String): Flow<List<ScanResult>> {
        return dao.getScansByType(type).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getScansByDateRange(startTime: Long, endTime: Long): Flow<List<ScanResult>> {
        return dao.getScansByDateRange(startTime, endTime).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getScansByTypeAndDateRange(type: String, startTime: Long, endTime: Long): Flow<List<ScanResult>> {
        return dao.getScansByTypeAndDateRange(type, startTime, endTime).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun deleteScansByIds(ids: List<Long>) {
        dao.deleteScansByIds(ids)
    }

    override suspend fun addFavoritesByIds(ids: List<Long>) {
        dao.addFavoritesByIds(ids)
    }

    override suspend fun removeFavoritesByIds(ids: List<Long>) {
        dao.removeFavoritesByIds(ids)
    }

    override suspend fun getScansCountByDate(startOfDay: Long, endOfDay: Long): Int {
        return dao.getScansCountByDate(startOfDay, endOfDay)
    }

    override suspend fun getScanTypeDistribution(): Map<String, Int> {
        return dao.getScanTypeDistribution().associate { it.scanType to it.count }
    }

    override fun searchScansByDateRange(query: String, startTime: Long, endTime: Long): Flow<List<ScanResult>> {
        return dao.searchScansByDateRange(query, startTime, endTime).map { entities ->
            entities.map { it.toDomain() }
        }
    }
}
