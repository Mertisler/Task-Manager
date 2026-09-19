package com.loc.taskmanager.ui.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.loc.taskmanager.model.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.loc.taskmanager.model.domain.utils.Result
import com.loc.taskmanager.ui.screens.admin.AdminUiState


@HiltViewModel
class AdminViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    init {
        // ViewModel oluşturulduğu anda tüm görevleri çekmeye başla
        loadAllTasks()
    }

    private fun loadAllTasks() {
        viewModelScope.launch {
            // Sadece bir çalışana ait olanı değil, TÜM görevleri getiren repository fonksiyonu
            repository.getAllTasks().collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                    }
                    is Result.Success -> {
                        _uiState.update {
                            it.copy(isLoading = false, tasks = result.data, errorMessage = null)
                        }
                    }
                    is Result.Error -> {
                        _uiState.update {
                            it.copy(isLoading = false, errorMessage = result.exception.localizedMessage)
                        }
                    }
                }
            }
        }
    }
}