package com.agrogeocolector.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import io.ktor.client.engine.android.Android
import javax.inject.Singleton

/**
 * Módulo Hilt para prover o cliente Supabase.
 * 
 * IMPORTANTE: Configurar as variáveis de ambiente antes de usar:
 * 
 * 1. Criar arquivo local.properties na raiz do projeto:
 *    ```
 *    SUPABASE_URL=https://seu-projeto.supabase.co
 *    SUPABASE_ANON_KEY=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
 *    ```
 * 
 * 2. Ou criar BuildConfig no app/build.gradle.kts:
 *    ```kotlin
 *    android {
 *        buildFeatures {
 *            buildConfig = true
 *        }
 *        defaultConfig {
 *            buildConfigField("String", "SUPABASE_URL", "\"https://seu-projeto.supabase.co\"")
 *            buildConfigField("String", "SUPABASE_ANON_KEY", "\"sua-chave-anon\"")
 *        }
 *    }
 *    ```
 * 
 * Como obter as credenciais do Supabase:
 * 1. Acesse seu projeto em https://supabase.com/dashboard
 * 2. Vá em Settings → API
 * 3. Copie:
 *    - URL: Project URL
 *    - Key: anon/public key
 */
@Module
@InstallIn(SingletonComponent::class)
object SupabaseModule {
    
    /**
     * Credenciais do Supabase.
     * 
     * Prioridade:
     * 1. BuildConfig (lido de local.properties)
     * 2. Valores hardcoded abaixo (fallback)
     * 
     * NOTA: A chave anon é pública e segura para uso no cliente.
     * A segurança real vem do Row Level Security (RLS) no Supabase.
     */
    private const val SUPABASE_URL_FALLBACK = "https://zwxobbirovyeudkzmxze.supabase.co"
    private const val SUPABASE_ANON_KEY_FALLBACK = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Inp3eG9iYmlyb3Z5ZXVka3pteHplIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjU0NTIzMzcsImV4cCI6MjA4MTAyODMzN30.BR9R2TtSaylsQNAwKzWFDhZzvC8t2htVY2pLVTmf6JI"
    
    @Provides
    @Singleton
    fun provideSupabaseClient(@ApplicationContext context: Context): SupabaseClient {
        // Tenta usar credenciais do BuildConfig (local.properties)
        // Se não existir, usa valores fallback hardcoded
        val url = try {
            val buildConfigClass = Class.forName("com.agrogeocolector.BuildConfig")
            val urlField = buildConfigClass.getDeclaredField("SUPABASE_URL")
            val configUrl = urlField.get(null) as? String
            if (configUrl.isNullOrBlank()) SUPABASE_URL_FALLBACK else configUrl
        } catch (e: Exception) {
            android.util.Log.w("SupabaseModule", "BuildConfig não encontrado, usando fallback")
            SUPABASE_URL_FALLBACK
        }
        
        val key = try {
            val buildConfigClass = Class.forName("com.agrogeocolector.BuildConfig")
            val keyField = buildConfigClass.getDeclaredField("SUPABASE_ANON_KEY")
            val configKey = keyField.get(null) as? String
            if (configKey.isNullOrBlank()) SUPABASE_ANON_KEY_FALLBACK else configKey
        } catch (e: Exception) {
            android.util.Log.w("SupabaseModule", "BuildConfig não encontrado, usando fallback")
            SUPABASE_ANON_KEY_FALLBACK
        }
        
        android.util.Log.d("SupabaseModule", "Conectando ao Supabase: ${url.take(30)}...")
        
        return createSupabaseClient(
            supabaseUrl = url,
            supabaseKey = key
        ) {
            // Configurações do cliente HTTP
            httpEngine = Android.create()
            
            // Instala os plugins necessários
            install(Postgrest) {
                // Configurações do PostgreSQL REST API
            }
            
            install(Storage) {
                // Configurações do Storage
            }
            
            install(Auth) {
                // Configurações de autenticação
                // Se não usar auth, pode remover
            }
            
            // Opcional: Realtime para mudanças em tempo real
            // install(Realtime)
        }
    }
}

/**
 * Extensão para facilitar o acesso às credenciais do BuildConfig.
 * 
 * Exemplo de uso no build.gradle.kts:
 * ```kotlin
 * android {
 *     buildFeatures {
 *         buildConfig = true
 *     }
 *     
 *     defaultConfig {
 *         val localProperties = Properties()
 *         val localPropertiesFile = rootProject.file("local.properties")
 *         if (localPropertiesFile.exists()) {
 *             localProperties.load(FileInputStream(localPropertiesFile))
 *         }
 *         
 *         buildConfigField(
 *             "String",
 *             "SUPABASE_URL",
 *             "\"${localProperties.getProperty("SUPABASE_URL", "")}\""
 *         )
 *         buildConfigField(
 *             "String",
 *             "SUPABASE_ANON_KEY",
 *             "\"${localProperties.getProperty("SUPABASE_ANON_KEY", "")}\""
 *         )
 *     }
 * }
 * ```
 */
