package com.agrogeocolector.data.sync

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.agrogeocolector.data.local.dao.SoilSampleDao
import com.agrogeocolector.data.remote.SupabaseRepository
import com.agrogeocolector.data.remote.model.SoilSampleRemote
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Worker que sincroniza amostras de solo com o Supabase.
 * 
 * Fluxo de sincronização:
 * 1. Busca amostras não sincronizadas do Room
 * 2. Para cada amostra:
 *    a. Upload da foto (se houver) → Retorna URL
 *    b. Insere dados no PostgreSQL com a URL da foto
 *    c. Marca como sincronizado no Room
 * 3. Retorna Result.success() ou Result.retry()
 * 
 * Executado automaticamente quando:
 * - Internet volta (NetworkConstraint)
 * - App salva nova amostra (manual trigger)
 * 
 * Integrado com Hilt para injeção de dependências.
 */
@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val soilSampleDao: SoilSampleDao,
    private val supabaseRepository: SupabaseRepository
) : CoroutineWorker(context, params) {
    
    companion object {
        const val WORK_NAME = "sync_soil_samples"
        private const val TAG = "SyncWorker"
    }
    
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Iniciando sincronização...")
            
            // 1. Busca todas as amostras não sincronizadas
            val unsyncedSamples = soilSampleDao.getUnsyncedSamples()
            
            if (unsyncedSamples.isEmpty()) {
                Log.d(TAG, "Nenhuma amostra pendente para sincronizar")
                return@withContext Result.success()
            }
            
            Log.d(TAG, "Encontradas ${unsyncedSamples.size} amostras para sincronizar")
            
            // 2. Para cada amostra, tenta enviar ao Supabase
            var successCount = 0
            var failCount = 0
            val errors = mutableListOf<String>()
            
            for (sample in unsyncedSamples) {
                try {
                    Log.d(TAG, "Sincronizando amostra ID: ${sample.id}")
                    
                    // Passo 1: Upload da foto (se existir)
                    var photoUrl: String? = null
                    
                    if (!sample.photoPath.isNullOrBlank()) {
                        val photoFile = File(sample.photoPath)
                        
                        if (photoFile.exists() && photoFile.canRead()) {
                            Log.d(TAG, "Fazendo upload da foto: ${photoFile.name}")
                            
                            try {
                                photoUrl = supabaseRepository.uploadImage(photoFile)
                                Log.d(TAG, "Foto enviada com sucesso: $photoUrl")
                            } catch (e: Exception) {
                                Log.e(TAG, "Erro ao fazer upload da foto", e)
                                // Continua mesmo se a foto falhar
                                // Ou você pode decidir falhar a operação inteira:
                                // throw e
                            }
                        } else {
                            Log.w(TAG, "Arquivo de foto não encontrado ou inacessível: ${sample.photoPath}")
                        }
                    }
                    
                    // Passo 2: Monta o objeto para envio ao Supabase
                    val remoteSample = SoilSampleRemote(
                        latitude = sample.latitude,
                        longitude = sample.longitude,
                        altitude = sample.altitude,
                        accuracy = sample.accuracy,
                        note = sample.note.ifBlank { null },
                        photoUrl = photoUrl,
                        timestamp = sample.timestamp,
                        farmId = sample.farmId,
                        fieldId = sample.fieldId
                    )
                    
                    // Passo 3: Insere os dados no PostgreSQL
                    Log.d(TAG, "Inserindo dados no Supabase...")
                    val remoteId = supabaseRepository.insertSample(remoteSample)
                    
                    // Passo 4: Marca como sincronizado no Room
                    soilSampleDao.markAsSynced(sample.id, remoteId)
                    
                    successCount++
                    Log.d(TAG, "Amostra ${sample.id} sincronizada com sucesso! Remote ID: $remoteId")
                    
                } catch (e: Exception) {
                    Log.e(TAG, "Erro ao sincronizar amostra ${sample.id}", e)
                    errors.add("Amostra ${sample.id}: ${e.message}")
                    failCount++
                    
                    // Se for erro de rede, vale a pena tentar novamente
                    if (isNetworkError(e)) {
                        Log.d(TAG, "Erro de rede detectado, agendando retry...")
                    }
                }
            }
            
            // 5. Log do resultado final
            Log.d(TAG, "Sincronização concluída: $successCount sucesso(s), $failCount falha(s)")
            
            // 6. Retorna resultado
            when {
                failCount == 0 -> {
                    // Todas sincronizaram
                    Log.d(TAG, "✅ Todas as amostras foram sincronizadas")
                    Result.success()
                }
                successCount > 0 -> {
                    // Sucesso parcial - considera sucesso mas loga os erros
                    Log.w(TAG, "⚠️ Sincronização parcial. Erros: ${errors.joinToString("; ")}")
                    Result.success()
                }
                else -> {
                    // Todas falharam - tenta novamente depois
                    Log.e(TAG, "❌ Todas as tentativas falharam. Erros: ${errors.joinToString("; ")}")
                    Result.retry()
                }
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Erro fatal na sincronização", e)
            Result.retry()
        }
    }
    
    /**
     * Verifica se o erro é de rede (vale a pena tentar novamente).
     */
    private fun isNetworkError(e: Exception): Boolean {
        return e is java.net.UnknownHostException ||
               e is java.net.SocketTimeoutException ||
               e is java.io.IOException ||
               e.message?.contains("Unable to resolve host", ignoreCase = true) == true
    }
}
