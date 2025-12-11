package com.agrogeocolector.di

import android.content.Context
import androidx.room.Room
import com.agrogeocolector.data.local.AppDatabase
import com.agrogeocolector.data.local.dao.SoilSampleDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo Hilt para prover o banco de dados Room.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "agrocoletor_database"
        )
            .fallbackToDestructiveMigration() // Para MVP; em produção usar migrations
            .build()
    }
    
    @Provides
    fun provideSoilSampleDao(database: AppDatabase): SoilSampleDao {
        return database.soilSampleDao()
    }
}
