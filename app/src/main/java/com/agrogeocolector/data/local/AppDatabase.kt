package com.agrogeocolector.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.agrogeocolector.data.local.dao.SoilSampleDao
import com.agrogeocolector.data.local.entity.SoilSample

/**
 * Banco de dados Room para armazenamento offline.
 * 
 * Este banco é a espinha dorsal do app offline-first.
 * Todas as coletas são salvas aqui primeiro, e sincronizadas depois.
 */
@Database(
    entities = [SoilSample::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun soilSampleDao(): SoilSampleDao
}
