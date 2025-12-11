package com.agrogeocolector.ui.samples

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agrogeocolector.data.local.dao.SoilSampleDao
import com.agrogeocolector.data.local.entity.SoilSample
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel de exemplo para a tela de lista de amostras.
 * 
 * Demonstra o uso correto de:
 * - Flow para dados reativos
 * - StateFlow para UI state
 * - Coroutines para operações assíncronas
 * - Hilt para injeção de dependências
 */
@HiltViewModel
class SamplesViewModel @Inject constructor(
    private val soilSampleDao: SoilSampleDao
) : ViewModel() {
    
    // Estado da UI
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    
    // Amostras (Flow reativo do Room)
    val samples: StateFlow<List<SoilSample>> = soilSampleDao
        .getAllSamples()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    // Contador de amostras não sincronizadas
    private val _unsyncedCount = MutableStateFlow(0)
    val unsyncedCount: StateFlow<Int> = _unsyncedCount.asStateFlow()
    
    init {
        loadData()
        observeUnsyncedCount()
    }
    
    private fun loadData() {
        viewModelScope.launch {
            try {
                _uiState.value = UiState.Loading
                // Room Flow já carrega automaticamente
                _uiState.value = UiState.Success
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Erro desconhecido")
            }
        }
    }
    
    private fun observeUnsyncedCount() {
        viewModelScope.launch {
            while (true) {
                _unsyncedCount.value = soilSampleDao.getUnsyncedCount()
                kotlinx.coroutines.delay(5000) // Atualiza a cada 5s
            }
        }
    }
    
    /**
     * Adiciona uma nova amostra e agenda sincronização.
     */
    fun addSample(
        latitude: Double,
        longitude: Double,
        altitude: Double? = null,
        accuracy: Float? = null,
        note: String = "",
        photoPath: String? = null,
        farmId: String? = null,
        fieldId: String? = null
    ) {
        viewModelScope.launch {
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
                
                // Salva no banco local
                soilSampleDao.insertSample(sample)
                
                // IMPORTANTE: Agenda sincronização automática
                // Esta chamada garante que a amostra será enviada ao Supabase
                // assim que houver conexão com internet
                com.agrogeocolector.data.sync.SyncManager.syncAfterSave(
                    getApplication<android.app.Application>()
                )
                
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Erro ao salvar: ${e.message}")
            }
        }
    }
    
    /**
     * Força sincronização imediata.
     * Chamado quando usuário clica em "Sincronizar Agora".
     */
    fun forceSyncNow() {
        com.agrogeocolector.data.sync.SyncManager.syncNow(
            getApplication<android.app.Application>()
        )
    }
    
    /**
     * Atualiza uma amostra existente.
     */
    fun updateSample(sample: SoilSample) {
        viewModelScope.launch {
            try {
                soilSampleDao.updateSample(sample)
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Erro ao atualizar: ${e.message}")
            }
        }
    }
    
    /**
     * Deleta uma amostra.
     */
    fun deleteSample(sample: SoilSample) {
        viewModelScope.launch {
            try {
                // Deleta a foto do sistema de arquivos
                sample.photoPath?.let { path ->
                    com.agrogeocolector.util.ImageFileUtils.deleteImageFile(path)
                }
                
                // Deleta do banco
                soilSampleDao.deleteSample(sample)
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Erro ao deletar: ${e.message}")
            }
        }
    }
    
    /**
     * Busca amostras de uma fazenda específica.
     */
    fun getSamplesByFarm(farmId: String): StateFlow<List<SoilSample>> {
        return soilSampleDao
            .getSamplesByFarm(farmId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }
    
    /**
     * Estados possíveis da UI.
     */
    sealed class UiState {
        object Loading : UiState()
        object Success : UiState()
        data class Error(val message: String) : UiState()
    }
}
