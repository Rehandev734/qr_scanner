package com.example.scanify.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.scanify.data.local.entity.ScanHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanHistoryDao {

    @Query("SELECT * FROM scan_history ORDER BY timestamp DESC")
    fun getAllScans(): Flow<List<ScanHistoryEntity>>

    @Query("SELECT * FROM scan_history WHERE isFavorite = 1 ORDER BY timestamp DESC")
    fun getFavorites(): Flow<List<ScanHistoryEntity>>

    @Query("SELECT * FROM scan_history WHERE rawValue LIKE '%' || :query || '%' OR scanType LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchScans(query: String): Flow<List<ScanHistoryEntity>>

    @Query("SELECT * FROM scan_history WHERE id = :id")
    suspend fun getScanById(id: Long): ScanHistoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScan(scan: ScanHistoryEntity): Long

    @Delete
    suspend fun deleteScan(scan: ScanHistoryEntity)

    @Query("DELETE FROM scan_history")
    suspend fun deleteAllScans()

    @Query("UPDATE scan_history SET isFavorite = NOT isFavorite WHERE id = :id")
    suspend fun toggleFavorite(id: Long)

    @Query("SELECT * FROM scan_history ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentScans(limit: Int): List<ScanHistoryEntity>

    @Query("SELECT COUNT(*) FROM scan_history")
    suspend fun getScanCount(): Int

    @Query("SELECT * FROM scan_history WHERE scanType = :type ORDER BY timestamp DESC")
    fun getScansByType(type: String): Flow<List<ScanHistoryEntity>>

    @Query("SELECT * FROM scan_history WHERE timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp DESC")
    fun getScansByDateRange(startTime: Long, endTime: Long): Flow<List<ScanHistoryEntity>>

    @Query("SELECT * FROM scan_history WHERE scanType = :type AND timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp DESC")
    fun getScansByTypeAndDateRange(type: String, startTime: Long, endTime: Long): Flow<List<ScanHistoryEntity>>

    @Query("DELETE FROM scan_history WHERE id IN (:ids)")
    suspend fun deleteScansByIds(ids: List<Long>)

    @Query("UPDATE scan_history SET isFavorite = 1 WHERE id IN (:ids)")
    suspend fun addFavoritesByIds(ids: List<Long>)

    @Query("UPDATE scan_history SET isFavorite = 0 WHERE id IN (:ids)")
    suspend fun removeFavoritesByIds(ids: List<Long>)

    @Query("SELECT COUNT(*) FROM scan_history WHERE timestamp >= :startOfDay AND timestamp <= :endOfDay")
    suspend fun getScansCountByDate(startOfDay: Long, endOfDay: Long): Int

    @Query("SELECT scanType, COUNT(*) as count FROM scan_history GROUP BY scanType ORDER BY count DESC")
    suspend fun getScanTypeDistribution(): List<TypeCount>

    @Query("SELECT * FROM scan_history WHERE rawValue LIKE '%' || :query || '%' OR scanType LIKE '%' || :query || '%' AND timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp DESC")
    fun searchScansByDateRange(query: String, startTime: Long, endTime: Long): Flow<List<ScanHistoryEntity>>
}

data class TypeCount(
    val scanType: String,
    val count: Int
)
