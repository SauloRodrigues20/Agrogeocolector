package com.agrogeocolector.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidade que representa uma amostra de solo coletada.
 * 
 * Importante: Não salvamos BLOB de imagem no banco! 
 * Apenas o caminho (photoPath) para o arquivo comprimido no filesDir.
 */
@Entity(tableName = "soil_samples")
data class SoilSample(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    // Coordenadas GPS da coleta
    val latitude: Double,
    val longitude: Double,
    
    // Altitude (opcional, mas útil)
    val altitude: Double? = null,
    
    // Precisão da localização em metros
    val accuracy: Float? = null,
    
    // Observações do agrônomo
    val note: String = "",
    
    // Caminho da foto comprimida no filesDir (não BLOB!)
    val photoPath: String? = null,
    
    // Data e hora da coleta
    val timestamp: Long = System.currentTimeMillis(),
    
    // Identificador da fazenda/talhão
    val farmId: String? = null,
    val fieldId: String? = null,
    
    // Status de sincronização
    val isSynced: Boolean = false,
    
    // ID remoto (quando sincronizado com servidor)
    val remoteId: String? = null
)
