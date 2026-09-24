package com.example.scanify.domain.usecase

import com.example.scanify.domain.model.GeneratedQR
import com.example.scanify.domain.repository.GeneratedQRRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllGeneratedUseCase @Inject constructor(
    private val repository: GeneratedQRRepository
) {
    operator fun invoke(): Flow<List<GeneratedQR>> = repository.getAllGenerated()
}

class GetGeneratedFavoritesUseCase @Inject constructor(
    private val repository: GeneratedQRRepository
) {
    operator fun invoke(): Flow<List<GeneratedQR>> = repository.getGeneratedFavorites()
}

class SearchGeneratedUseCase @Inject constructor(
    private val repository: GeneratedQRRepository
) {
    operator fun invoke(query: String): Flow<List<GeneratedQR>> = repository.searchGenerated(query)
}

class GetGeneratedByIdUseCase @Inject constructor(
    private val repository: GeneratedQRRepository
) {
    suspend operator fun invoke(id: Long): GeneratedQR? = repository.getGeneratedById(id)
}

class InsertGeneratedUseCase @Inject constructor(
    private val repository: GeneratedQRRepository
) {
    suspend operator fun invoke(qr: GeneratedQR): Long = repository.insertGenerated(qr)
}

class DeleteGeneratedUseCase @Inject constructor(
    private val repository: GeneratedQRRepository
) {
    suspend operator fun invoke(qr: GeneratedQR) = repository.deleteGenerated(qr)
}

class DeleteAllGeneratedUseCase @Inject constructor(
    private val repository: GeneratedQRRepository
) {
    suspend operator fun invoke() = repository.deleteAllGenerated()
}

class ToggleGeneratedFavoriteUseCase @Inject constructor(
    private val repository: GeneratedQRRepository
) {
    suspend operator fun invoke(id: Long) = repository.toggleGeneratedFavorite(id)
}

class GetRecentGeneratedUseCase @Inject constructor(
    private val repository: GeneratedQRRepository
) {
    suspend operator fun invoke(limit: Int = 10): List<GeneratedQR> = repository.getRecentGenerated(limit)
}

class GetGeneratedCountUseCase @Inject constructor(
    private val repository: GeneratedQRRepository
) {
    suspend operator fun invoke(): Int = repository.getGeneratedCount()
}

class GetGeneratedByTypeUseCase @Inject constructor(
    private val repository: GeneratedQRRepository
) {
    operator fun invoke(type: String): Flow<List<GeneratedQR>> = repository.getGeneratedByType(type)
}

class GetGeneratedByDateRangeUseCase @Inject constructor(
    private val repository: GeneratedQRRepository
) {
    operator fun invoke(startTime: Long, endTime: Long): Flow<List<GeneratedQR>> =
        repository.getGeneratedByDateRange(startTime, endTime)
}

class DeleteGeneratedByIdsUseCase @Inject constructor(
    private val repository: GeneratedQRRepository
) {
    suspend operator fun invoke(ids: List<Long>) = repository.deleteGeneratedByIds(ids)
}

class AddGeneratedFavoritesByIdsUseCase @Inject constructor(
    private val repository: GeneratedQRRepository
) {
    suspend operator fun invoke(ids: List<Long>) = repository.addGeneratedFavoritesByIds(ids)
}

class RemoveGeneratedFavoritesByIdsUseCase @Inject constructor(
    private val repository: GeneratedQRRepository
) {
    suspend operator fun invoke(ids: List<Long>) = repository.removeGeneratedFavoritesByIds(ids)
}

class GetGeneratedCountByDateUseCase @Inject constructor(
    private val repository: GeneratedQRRepository
) {
    suspend operator fun invoke(startOfDay: Long, endOfDay: Long): Int =
        repository.getGeneratedCountByDate(startOfDay, endOfDay)
}

class GetGeneratedTypeDistributionUseCase @Inject constructor(
    private val repository: GeneratedQRRepository
) {
    suspend operator fun invoke(): Map<String, Int> = repository.getGeneratedTypeDistribution()
}

class SearchGeneratedByDateRangeUseCase @Inject constructor(
    private val repository: GeneratedQRRepository
) {
    operator fun invoke(query: String, startTime: Long, endTime: Long): Flow<List<GeneratedQR>> =
        repository.searchGeneratedByDateRange(query, startTime, endTime)
}
