package com.example.scanify.domain.usecase

import com.example.scanify.domain.model.ScanResult
import com.example.scanify.domain.repository.ScanRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllScansUseCase @Inject constructor(
    private val repository: ScanRepository
) {
    operator fun invoke(): Flow<List<ScanResult>> = repository.getAllScans()
}

class GetFavoritesUseCase @Inject constructor(
    private val repository: ScanRepository
) {
    operator fun invoke(): Flow<List<ScanResult>> = repository.getFavorites()
}

class SearchScansUseCase @Inject constructor(
    private val repository: ScanRepository
) {
    operator fun invoke(query: String): Flow<List<ScanResult>> = repository.searchScans(query)
}

class InsertScanUseCase @Inject constructor(
    private val repository: ScanRepository
) {
    suspend operator fun invoke(scan: ScanResult): Long = repository.insertScan(scan)
}

class DeleteScanUseCase @Inject constructor(
    private val repository: ScanRepository
) {
    suspend operator fun invoke(scan: ScanResult) = repository.deleteScan(scan)
}

class DeleteAllScansUseCase @Inject constructor(
    private val repository: ScanRepository
) {
    suspend operator fun invoke() = repository.deleteAllScans()
}

class ToggleFavoriteUseCase @Inject constructor(
    private val repository: ScanRepository
) {
    suspend operator fun invoke(id: Long) = repository.toggleFavorite(id)
}

class GetRecentScansUseCase @Inject constructor(
    private val repository: ScanRepository
) {
    suspend operator fun invoke(limit: Int = 10): List<ScanResult> = repository.getRecentScans(limit)
}

class GetScanByIdUseCase @Inject constructor(
    private val repository: ScanRepository
) {
    suspend operator fun invoke(id: Long): ScanResult? = repository.getScanById(id)
}

class GetScanCountUseCase @Inject constructor(
    private val repository: ScanRepository
) {
    suspend operator fun invoke(): Int = repository.getScanCount()
}

class GetScansByTypeUseCase @Inject constructor(
    private val repository: ScanRepository
) {
    operator fun invoke(type: String): Flow<List<ScanResult>> = repository.getScansByType(type)
}

class GetScansByDateRangeUseCase @Inject constructor(
    private val repository: ScanRepository
) {
    operator fun invoke(startTime: Long, endTime: Long): Flow<List<ScanResult>> =
        repository.getScansByDateRange(startTime, endTime)
}

class DeleteScansByIdsUseCase @Inject constructor(
    private val repository: ScanRepository
) {
    suspend operator fun invoke(ids: List<Long>) = repository.deleteScansByIds(ids)
}

class AddFavoritesByIdsUseCase @Inject constructor(
    private val repository: ScanRepository
) {
    suspend operator fun invoke(ids: List<Long>) = repository.addFavoritesByIds(ids)
}

class RemoveFavoritesByIdsUseCase @Inject constructor(
    private val repository: ScanRepository
) {
    suspend operator fun invoke(ids: List<Long>) = repository.removeFavoritesByIds(ids)
}

class GetScansCountByDateUseCase @Inject constructor(
    private val repository: ScanRepository
) {
    suspend operator fun invoke(startOfDay: Long, endOfDay: Long): Int =
        repository.getScansCountByDate(startOfDay, endOfDay)
}

class GetScanTypeDistributionUseCase @Inject constructor(
    private val repository: ScanRepository
) {
    suspend operator fun invoke(): Map<String, Int> = repository.getScanTypeDistribution()
}

class SearchScansByDateRangeUseCase @Inject constructor(
    private val repository: ScanRepository
) {
    operator fun invoke(query: String, startTime: Long, endTime: Long): Flow<List<ScanResult>> =
        repository.searchScansByDateRange(query, startTime, endTime)
}
