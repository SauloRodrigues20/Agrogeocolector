package com.agrogeocolector.data.repository

import android.content.Context
import android.util.Log
import com.agrogeocolector.data.local.dao.SoilSampleDao
import com.agrogeocolector.data.local.entity.SoilSample
import com.agrogeocolector.data.sync.SyncManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repositório unificado para gerenciar amostras de solo.
 * 
 * Este repositório:
 * - Abstrai o DAO do Room
 * - Gerencia sincronização automática
 * - Encapsula lógica de negócio
 * - Fornece API limpa para ViewModels
 * 
 * Exemplo de uso:
 * ```kotlin
 * @HiltViewModel
 * class MyViewModel @Inject constructor(
 *     private val repository: SoilSampleRepository
 * ) : ViewModel() {
 *     val samples = repository.getAllSamples()
 *         .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
 *     
 *     fun addSample(lat: Double, lng: Double) {
 *         viewModelScope.launch {
 *             repository.insertSample(lat, lng, "Nota")
 *         }
 *     }
 * }
 * ```
 */
@Singleton
class SoilSampleRepository @Inject constructor(
    private val soilSampleDao: SoilSampleDao,
    @ApplicationContext private val context: Context
) {
    
    companion object {
        private const val TAG = "SoilSampleRepository"
    }
    
    // ============================================================
    // QUERIES (Read Operations)
    // ============================================================
    
    /**
     * Retorna todas as amostras em ordem decrescente de timestamp.
     * Flow reativo - atualiza automaticamente quando dados mudam.
     */
    fun getAllSamples(): Flow<List<SoilSample>> {
        return soilSampleDao.getAllSamples()
    }
    
    /**
     * Retorna amostras de uma fazenda específica.
     */
    fun getSamplesByFarm(farmId: String): Flow<List<SoilSample>> {
        return soilSampleDao.getSamplesByFarm(farmId)
    }
    
    /**
     * Retorna amostras de um talhão específico.
     */
    fun getSamplesByField(fieldId: String): Flow<List<SoilSample>> {
        return soilSampleDao.getSamplesByField(fieldId)
    }
    
    /**
     * Busca uma amostra pelo ID local.
     */
    suspend fun getSampleById(id: Long): SoilSample? {
        return soilSampleDao.getSampleById(id)
    }
    
    /**
     * Retorna quantidade de amostras não sincronizadas.
     */
    suspend fun getUnsyncedCount(): Int {
        return soilSampleDao.getUnsyncedCount()
    }
    
    /**
     * Retorna total de amostras.
     */
    suspend fun getTotalCount(): Int {
        return soilSampleDao.getSamplesCount()
    }
    
    // ============================================================
    // MUTATIONS (Write Operations)
    // ============================================================
    
    /**
     * Insere uma nova amostra.
     * 
     * ⚡ IMPORTANTE: Agenda sincronização automática após inserir!
     * 
     * @return ID da amostra inserida
     */
    suspend fun insertSample(
        latitude: Double,
        longitude: Double,
        altitude: Double? = null,
        accuracy: Float? = null,
        note: String = "",
        photoPath: String? = null,
        farmId: String? = null,
        fieldId: String? = null
    ): Long {
        try {
            val sample = SoilSample(
                latitude = latitude,
                longitude = longitude,
                altitude = altitude,
                accuracy = accuracy,
                note = note,
                photoPath = photoPath,
                farmId = farmId,
                fieldId = fieldId,
                timestamp = System.currentTimeMillis(),
                isSynced = false
            )
            
            val id = soilSampleDao.insertSample(sample)
            
            Log.d(TAG, "✅ Amostra inserida: ID=$id")
            
            // 🔄 Agenda sincronização automática
            SyncManager.syncAfterSave(context)
            
            return id
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Erro ao inserir amostra", e)
            throw e
        }
    }
    
    /**
     * Insere múltiplas amostras de uma vez.
     * Útil para importação de dados.
     */
    suspend fun insertSamples(samples: List<SoilSample>) {
        try {
            soilSampleDao.insertSamples(samples)
            Log.d(TAG, "✅ ${samples.size} amostras inseridas")
            
            // Agenda sincronização
            SyncManager.syncAfterSave(context)
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Erro ao inserir amostras", e)
            throw e
        }
    }
    
    /**
     * Atualiza uma amostra existente.
     */
    suspend fun updateSample(sample: SoilSample) {
        try {
            soilSampleDao.updateSample(sample)
            Log.d(TAG, "✅ Amostra atualizada: ID=${sample.id}")
            
            // Se não estava sincronizada, agenda sync novamente
            if (!sample.isSynced) {
                SyncManager.syncAfterSave(context)
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Erro ao atualizar amostra", e)
            throw e
        }
    }
    
    /**
     * Deleta uma amostra.
     * 
     * ⚠️ IMPORTANTE: Também deleta a foto do filesystem!
     */
    suspend fun deleteSample(sample: SoilSample) {
        try {
            // Deleta foto se existir
            sample.photoPath?.let { path ->
                val deleted = com.agrogeocolector.util.ImageFileUtils.deleteImageFile(path)
                if (deleted) {
                    Log.d(TAG, "🗑️ Foto deletada: $path")
                }
            }
            
            // Deleta do banco
            soilSampleDao.deleteSample(sample)
            Log.d(TAG, "✅ Amostra deletada: ID=${sample.id}")
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Erro ao deletar amostra", e)
            throw e
        }
    }
    
    /**
     * Deleta uma amostra pelo ID.
     */
    suspend fun deleteSampleById(id: Long) {
        try {
            val sample = getSampleById(id)
            if (sample != null) {
                deleteSample(sample)
            } else {
                Log.w(TAG, "⚠️ Amostra não encontrada: ID=$id")
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Erro ao deletar amostra por ID", e)
            throw e
        }
    }
    
    /**
     * Deleta todas as amostras.
     * ⚠️ USE COM CUIDADO!
     */
    suspend fun deleteAllSamples() {
        try {
            soilSampleDao.deleteAllSamples()
            // Deleta todas as fotos
            com.agrogeocolector.util.ImageFileUtils.clearAllImages(context)
            Log.d(TAG, "✅ Todas as amostras deletadas")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Erro ao deletar todas as amostras", e)
            throw e
        }
    }
    
    // ============================================================
    // SYNC OPERATIONS
    // ============================================================
    
    /**
     * Força sincronização imediata.
     */
    fun forceSyncNow() {
        Log.d(TAG, "🔄 Forçando sincronização imediata...")
        SyncManager.syncNow(context)
    }
    
    /**
     * Agenda sincronização periódica.
     * Chamado automaticamente no Application.onCreate().
     */
    fun schedulePeriodicSync() {
        Log.d(TAG, "⏰ Agendando sincronização periódica...")
        SyncManager.schedulePeriodicSync(context)
    }
    
    /**
     * Cancela todas as sincronizações.
     */
    fun cancelSync() {
        Log.d(TAG, "🛑 Cancelando sincronizações...")
        SyncManager.cancelSync(context)
    }
    
    /**
     * Retorna status da sincronização.
     */
    fun getSyncStatus() = SyncManager.getSyncStatus(context)
    
    /**
     * Retorna estatísticas de sincronização.
     */
    suspend fun getSyncStats() = SyncManager.getSyncStats(context)
}
