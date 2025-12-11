package com.agrogeocolector.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Modelo de dados para enviar ao Supabase (PostgreSQL).
 * 
 * Importante: Os nomes dos campos devem corresponder exatamente
 * aos nomes das colunas na tabela "soil_samples" do Supabase.
 * 
 * Schema SQL esperado:
 * ```sql
 * CREATE TABLE soil_samples (
 *   id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
 *   user_id UUID REFERENCES auth.users(id),
 *   latitude DOUBLE PRECISION NOT NULL,
 *   longitude DOUBLE PRECISION NOT NULL,
 *   altitude DOUBLE PRECISION,
 *   accuracy REAL,
 *   note TEXT,
 *   photo_url TEXT,
 *   timestamp BIGINT NOT NULL,
 *   farm_id TEXT,
 *   field_id TEXT,
 *   created_at TIMESTAMP DEFAULT NOW()
 * );
 * ```
 */
@Serializable
data class SoilSampleRemote(
    @SerialName("latitude")
    val latitude: Double,
    
    @SerialName("longitude")
    val longitude: Double,
    
    @SerialName("altitude")
    val altitude: Double? = null,
    
    @SerialName("accuracy")
    val accuracy: Float? = null,
    
    @SerialName("note")
    val note: String? = null,
    
    @SerialName("photo_url")
    val photoUrl: String? = null,
    
    @SerialName("timestamp")
    val timestamp: Long,
    
    @SerialName("farm_id")
    val farmId: String? = null,
    
    @SerialName("field_id")
    val fieldId: String? = null,
    
    // ID do usuário autenticado (preenchido automaticamente pelo Supabase RLS)
    @SerialName("user_id")
    val userId: String? = null
)

/**
 * Resposta do Supabase após inserção.
 */
@Serializable
data class SoilSampleResponse(
    @SerialName("id")
    val id: String,
    
    @SerialName("created_at")
    val createdAt: String
)
