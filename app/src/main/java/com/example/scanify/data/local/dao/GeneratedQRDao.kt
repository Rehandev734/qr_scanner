package com.example.scanify.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.scanify.data.local.entity.GeneratedQREntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GeneratedQRDao {

    @Query("SELECT * FROM generated_qr ORDER BY timestamp DESC")
    fun getAllGenerated(): Flow<List<GeneratedQREntity>>

    @Query("SELECT * FROM generated_qr WHERE isFavorite = 1 ORDER BY timestamp DESC")
    fun getFavorites(): Flow<List<GeneratedQREntity>>

    @Query("SELECT * FROM generated_qr WHERE id = :id")
    suspend fun getGeneratedById(id: Long): GeneratedQREntity?

    @Query("SELECT * FROM generated_qr WHERE content LIKE '%' || :query || '%' OR type LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchGenerated(query: String): Flow<List<GeneratedQREntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGenerated(qr: GeneratedQREntity): Long

    @Delete
    suspend fun deleteGenerated(qr: GeneratedQREntity)

    @Query("DELETE FROM generated_qr")
    suspend fun deleteAllGenerated()

    @Query("UPDATE generated_qr SET isFavorite = NOT isFavorite WHERE id = :id")
    suspend fun toggleFavorite(id: Long)

    @Query("SELECT * FROM generated_qr ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentGenerated(limit: Int): List<GeneratedQREntity>

    @Query("SELECT COUNT(*) FROM generated_qr")
    suspend fun getGeneratedCount(): Int

    @Query("SELECT * FROM generated_qr WHERE type = :type ORDER BY timestamp DESC")
    fun getGeneratedByType(type: String): Flow<List<GeneratedQREntity>>

    @Query("SELECT * FROM generated_qr WHERE timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp DESC")
    fun getGeneratedByDateRange(startTime: Long, endTime: Long): Flow<List<GeneratedQREntity>>

    @Query("SELECT * FROM generated_qr WHERE type = :type AND timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp DESC")
    fun getGeneratedByTypeAndDateRange(type: String, startTime: Long, endTime: Long): Flow<List<GeneratedQREntity>>

    @Query("DELETE FROM generated_qr WHERE id IN (:ids)")
    suspend fun deleteGeneratedByIds(ids: List<Long>)

    @Query("UPDATE generated_qr SET isFavorite = 1 WHERE id IN (:ids)")
    suspend fun addFavoritesByIds(ids: List<Long>)

    @Query("UPDATE generated_qr SET isFavorite = 0 WHERE id IN (:ids)")
    suspend fun removeFavoritesByIds(ids: List<Long>)

    @Query("SELECT COUNT(*) FROM generated_qr WHERE timestamp >= :startOfDay AND timestamp <= :endOfDay")
    suspend fun getGeneratedCountByDate(startOfDay: Long, endOfDay: Long): Int

    @Query("SELECT type, COUNT(*) as count FROM generated_qr GROUP BY type ORDER BY count DESC")
    suspend fun getGeneratedTypeDistribution(): List<GeneratedTypeCount>

    @Query("SELECT * FROM generated_qr WHERE content LIKE '%' || :query || '%' OR type LIKE '%' || :query || '%' AND timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp DESC")
    fun searchGeneratedByDateRange(query: String, startTime: Long, endTime: Long): Flow<List<GeneratedQREntity>>
}

data class GeneratedTypeCount(
    val type: String,
    val count: Int
)
