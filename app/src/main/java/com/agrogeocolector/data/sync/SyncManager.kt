package com.agrogeocolector.data.sync

import android.content.Context
import android.util.Log
import androidx.work.*
import java.util.concurrent.TimeUnit

/**
 * Gerenciador de sincronização usando WorkManager.
 * 
 * Agenda o trabalho de sincronização com constraints:
 * - Requer conexão com internet
 * - Retry automático com backoff exponencial
 * - Execução em background otimizada
 */
object SyncManager {
    
    private const val TAG = "SyncManager"
    
    /**
     * Agenda sincronização periódica.
     * Executará a cada 15 minutos quando houver internet.
     * 
     * Características:
     * - Constraint de rede: CONNECTED (qualquer conexão)
     * - Backoff: Exponencial com 30s inicial
     * - Flexibilidade: 5 minutos de janela
     */
    fun schedulePeriodicSync(context: Context) {
        Log.d(TAG, "Agendando sincronização periódica...")
        
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED) // Requer internet
            .setRequiresBatteryNotLow(false) // Pode executar com bateria baixa
            .setRequiresStorageNotLow(false) // Pode executar com storage baixo
            .build()
        
        val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(
            repeatInterval = 15,
            repeatIntervalTimeUnit = TimeUnit.MINUTES,
            flexTimeInterval = 5,
            flexTimeIntervalUnit = TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                30, // Espera inicial de 30 segundos
                TimeUnit.SECONDS
            )
            .addTag("sync")
            .addTag("periodic")
            .build()
        
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            SyncWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP, // Mantém o trabalho existente
            syncRequest
        )
        
        Log.d(TAG, "✅ Sincronização periódica agendada (a cada 15 min)")
    }
    
    /**
     * Força sincronização imediata (one-time).
     * 
     * Casos de uso:
     * - Usuário pressiona botão "Sincronizar Agora"
     * - App acaba de salvar uma nova amostra
     * - Usuário quer verificar se há dados pendentes
     * 
     * Esta função DEVE ser chamada sempre que uma nova amostra for salva!
     */
    fun syncNow(context: Context) {
        Log.d(TAG, "Iniciando sincronização imediata...")
        
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        
        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                10,
                TimeUnit.SECONDS
            )
            .addTag("sync")
            .addTag("immediate")
            .build()
        
        // REPLACE: Substitui qualquer sync imediato pendente
        WorkManager.getInstance(context).enqueueUniqueWork(
            "${SyncWorker.WORK_NAME}_immediate",
            ExistingWorkPolicy.REPLACE,
            syncRequest
        )
        
        Log.d(TAG, "✅ Sincronização imediata agendada")
    }
    
    /**
     * Agenda sincronização após salvar uma amostra.
     * Chamada automaticamente pelo ViewModel/Repository.
     */
    fun syncAfterSave(context: Context) {
        // Usa a mesma lógica de syncNow, mas com delay de 2 segundos
        // para dar tempo de outras operações terminarem
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        
        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .setInitialDelay(2, TimeUnit.SECONDS) // Delay de 2s
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                10,
                TimeUnit.SECONDS
            )
            .addTag("sync")
            .addTag("after_save")
            .build()
        
        WorkManager.getInstance(context).enqueueUniqueWork(
            "${SyncWorker.WORK_NAME}_after_save",
            ExistingWorkPolicy.REPLACE,
            syncRequest
        )
        
        Log.d(TAG, "✅ Sincronização agendada para após salvamento")
    }
    
    /**
     * Cancela todas as sincronizações agendadas.
     * Útil para testes ou quando usuário quer pausar sync.
     */
    fun cancelSync(context: Context) {
        Log.d(TAG, "Cancelando todas as sincronizações...")
        WorkManager.getInstance(context).cancelUniqueWork(SyncWorker.WORK_NAME)
        WorkManager.getInstance(context).cancelAllWorkByTag("sync")
        Log.d(TAG, "✅ Sincronizações canceladas")
    }
    
    /**
     * Cancela apenas a sincronização periódica.
     * Mantém sincronizações manuais.
     */
    fun cancelPeriodicSync(context: Context) {
        Log.d(TAG, "Cancelando sincronização periódica...")
        WorkManager.getInstance(context).cancelUniqueWork(SyncWorker.WORK_NAME)
        Log.d(TAG, "✅ Sincronização periódica cancelada")
    }
    
    /**
     * Verifica o status da sincronização (LiveData).
     * Útil para observar na UI.
     */
    fun getSyncStatus(context: Context) = 
        WorkManager.getInstance(context).getWorkInfosForUniqueWorkLiveData(SyncWorker.WORK_NAME)
    
    /**
     * Verifica o status de todas as sincronizações (Flow).
     * Mais moderno e reativo.
     */
    fun getAllSyncStatusFlow(context: Context) =
        WorkManager.getInstance(context).getWorkInfosByTagFlow("sync")
    
    /**
     * Retorna estatísticas das sincronizações.
     */
    suspend fun getSyncStats(context: Context): SyncStats {
        val workInfos = WorkManager.getInstance(context)
            .getWorkInfosByTag("sync")
            .get()
        
        val running = workInfos.count { it.state == WorkInfo.State.RUNNING }
        val enqueued = workInfos.count { it.state == WorkInfo.State.ENQUEUED }
        val succeeded = workInfos.count { it.state == WorkInfo.State.SUCCEEDED }
        val failed = workInfos.count { it.state == WorkInfo.State.FAILED }
        
        return SyncStats(
            running = running,
            enqueued = enqueued,
            succeeded = succeeded,
            failed = failed
        )
    }
}

/**
 * Estatísticas de sincronização.
 */
data class SyncStats(
    val running: Int,
    val enqueued: Int,
    val succeeded: Int,
    val failed: Int
) {
    val hasPending: Boolean get() = running > 0 || enqueued > 0
    val total: Int get() = running + enqueued + succeeded + failed
}
