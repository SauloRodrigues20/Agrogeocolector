package com.agrogeocolector.data.remote

import android.util.Log
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage
import com.agrogeocolector.data.remote.model.SoilSampleRemote
import com.agrogeocolector.data.remote.model.SoilSampleResponse
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repositório para comunicação com o Supabase.
 * 
 * Encapsula todas as operações remotas:
 * - Upload de imagens para Storage
 * - Inserção de dados no PostgreSQL
 * 
 * Setup necessário no Supabase:
 * 
 * 1. Criar bucket "soil-photos":
 *    - Ir em Storage → New Bucket
 *    - Nome: "soil-photos"
 *    - Public: true (ou configurar RLS)
 * 
 * 2. Criar tabela "soil_samples":
 *    - Ver schema no SoilSampleRemote.kt
 * 
 * 3. Configurar Row Level Security (RLS):
 *    ```sql
 *    ALTER TABLE soil_samples ENABLE ROW LEVEL SECURITY;
 *    
 *    CREATE POLICY "Users can insert their own samples"
 *    ON soil_samples FOR INSERT
 *    WITH CHECK (auth.uid() = user_id);
 *    
 *    CREATE POLICY "Users can view their own samples"
 *    ON soil_samples FOR SELECT
 *    USING (auth.uid() = user_id);
 *    ```
 */
@Singleton
class SupabaseRepository @Inject constructor(
    private val supabase: SupabaseClient
) {
    
    companion object {
        private const val TAG = "SupabaseRepository"
        private const val BUCKET_NAME = "soil-photos"
        private const val TABLE_NAME = "soil_samples"
    }
    
    /**
     * Faz upload de uma foto para o Supabase Storage.
     * 
     * @param file Arquivo local da foto
     * @return URL pública da foto no Supabase
     * @throws Exception Se o upload falhar
     */
    suspend fun uploadImage(file: File): String {
        try {
            Log.d(TAG, "Iniciando upload da foto: ${file.name}")
            
            // Gera um nome único para o arquivo
            val fileName = "${System.currentTimeMillis()}_${file.name}"
            
            // Faz upload para o bucket
            val bucket = supabase.storage[BUCKET_NAME]
            bucket.upload(
                path = fileName,
                data = file.readBytes(),
                upsert = false // Não sobrescrever se já existir
            )
            
            // Retorna a URL pública
            val publicUrl = bucket.publicUrl(fileName)
            
            Log.d(TAG, "Upload concluído: $publicUrl")
            return publicUrl
            
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao fazer upload da foto", e)
            throw Exception("Falha no upload da foto: ${e.message}", e)
        }
    }
    
    /**
     * Insere uma amostra de solo no banco PostgreSQL.
     * 
     * @param sample Dados da amostra
     * @return ID remoto gerado pelo Supabase
     * @throws Exception Se a inserção falhar
     */
    suspend fun insertSample(sample: SoilSampleRemote): String {
        try {
            Log.d(TAG, "Inserindo amostra no Supabase: lat=${sample.latitude}, lng=${sample.longitude}")
            
            // Insere na tabela e retorna o registro criado
            val response = supabase.from(TABLE_NAME)
                .insert(sample) {
                    select()
                }
                .decodeSingle<SoilSampleResponse>()
            
            Log.d(TAG, "Amostra inserida com sucesso. ID: ${response.id}")
            return response.id
            
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao inserir amostra no Supabase", e)
            throw Exception("Falha ao salvar dados: ${e.message}", e)
        }
    }
    
    /**
     * Deleta uma foto do Storage.
     * Útil se precisar fazer rollback após erro.
     */
    suspend fun deleteImage(photoUrl: String) {
        try {
            // Extrai o nome do arquivo da URL
            val fileName = photoUrl.substringAfterLast("/")
            
            val bucket = supabase.storage[BUCKET_NAME]
            bucket.delete(fileName)
            
            Log.d(TAG, "Foto deletada: $fileName")
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao deletar foto", e)
            // Não propaga o erro para não bloquear a operação principal
        }
    }
    
    /**
     * Testa a conexão com o Supabase.
     * Útil para debug.
     */
    suspend fun testConnection(): Boolean {
        return try {
            // Tenta fazer uma query simples
            supabase.from(TABLE_NAME)
                .select {
                    limit(1)
                }
            true
        } catch (e: Exception) {
            Log.e(TAG, "Falha ao conectar com Supabase", e)
            false
        }
    }
}
