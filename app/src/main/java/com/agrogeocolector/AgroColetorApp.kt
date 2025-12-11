package com.agrogeocolector

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.agrogeocolector.data.sync.SyncManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Classe Application do app com Hilt.
 * Inicializa o WorkManager e agenda sincronização periódica.
 */
@HiltAndroidApp
class AgroColetorApp : Application(), Configuration.Provider {
    
    @Inject
    lateinit var workerFactory: HiltWorkerFactory
    
    override fun onCreate() {
        super.onCreate()
        
        // Agenda sincronização periódica ao iniciar o app
        SyncManager.schedulePeriodicSync(this)
    }
    
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
