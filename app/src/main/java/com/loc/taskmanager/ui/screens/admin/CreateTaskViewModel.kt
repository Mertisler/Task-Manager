package com.loc.taskmanager.ui.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.loc.taskmanager.model.data.model.Task
import com.loc.taskmanager.model.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import com.loc.taskmanager.model.domain.utils.Result
@HiltViewModel
class CreateTaskViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateTaskUiState())
    val uiState: StateFlow<CreateTaskUiState> = _uiState.asStateFlow()

    // Formdaki metinler değiştikçe State'i güncelleyen fonksiyonlar
    fun updateTitle(newTitle: String) {
        _uiState.update { it.copy(title = newTitle, errorMessage = null) }
    }

    fun updateDescription(newDesc: String) {
        _uiState.update { it.copy(description = newDesc, errorMessage = null) }
    }

    fun updateAssignedEmployee(employeeId: String) {
        _uiState.update { it.copy(assignedToId = employeeId, errorMessage = null) }
    }

    // Görevi oluşturma ve veri tabanına yazma işlemi
    fun createTask(adminId: String) {
        val currentState = _uiState.value

        // 1. Doğrulama (Validation)
        if (currentState.title.isBlank() || currentState.description.isBlank() || currentState.assignedToId.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Lütfen tüm alanları doldurun.") }
            return
        }

        // 2. İstek (Request) Akışı
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val newTask = Task(
                // Firestore otomatik ID de verebilir ancak manuel UUID atamak çevrimdışı senaryolarda avantaj sağlar
                id = UUID.randomUUID().toString(),
                title = currentState.title,
                description = currentState.description,
                assignedToId = currentState.assignedToId,
                createdById = adminId
            )

            // 3. Sonuç (Result) Akışı
            when (val result = repository.addTask(newTask)) {
                is Result.Success -> {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = result.exception.localizedMessage)
                    }
                }
                is Result.Loading -> Unit // Bu projede addTask suspend olduğu için Loading kullanmıyoruz
            }
        }
    }
}