package com.agrogeocolector.data.local.dao

import androidx.room.*
import com.agrogeocolector.data.local.entity.SoilSample
import kotlinx.coroutines.flow.Flow

/**
 * DAO para operações com amostras de solo.
 * Usa Flow para observar mudanças no banco de dados de forma reativa.
 */
@Dao
interface SoilSampleDao {
    
    @Query("SELECT * FROM soil_samples ORDER BY timestamp DESC")
    fun getAllSamples(): Flow<List<SoilSample>>
    
    @Query("SELECT * FROM soil_samples WHERE id = :id")
    suspend fun getSampleById(id: Long): SoilSample?
    
    @Query("SELECT * FROM soil_samples WHERE isSynced = 0 ORDER BY timestamp ASC")
    suspend fun getUnsyncedSamples(): List<SoilSample>
    
    @Query("SELECT * FROM soil_samples WHERE farmId = :farmId ORDER BY timestamp DESC")
    fun getSamplesByFarm(farmId: String): Flow<List<SoilSample>>
    
    @Query("SELECT * FROM soil_samples WHERE fieldId = :fieldId ORDER BY timestamp DESC")
    fun getSamplesByField(fieldId: String): Flow<List<SoilSample>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSample(sample: SoilSample): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSamples(samples: List<SoilSample>)
    
    @Update
    suspend fun updateSample(sample: SoilSample)
    
    @Query("UPDATE soil_samples SET isSynced = 1, remoteId = :remoteId WHERE id = :id")
    suspend fun markAsSynced(id: Long, remoteId: String)
    
    @Delete
    suspend fun deleteSample(sample: SoilSample)
    
    @Query("DELETE FROM soil_samples WHERE id = :id")
    suspend fun deleteSampleById(id: Long)
    
    @Query("DELETE FROM soil_samples")
    suspend fun deleteAllSamples()
    
    @Query("SELECT COUNT(*) FROM soil_samples")
    suspend fun getSamplesCount(): Int
    
    @Query("SELECT COUNT(*) FROM soil_samples WHERE isSynced = 0")
    suspend fun getUnsyncedCount(): Int
}
