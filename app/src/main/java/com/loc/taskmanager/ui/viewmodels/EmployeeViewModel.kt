package com.loc.taskmanager.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.loc.taskmanager.model.domain.repository.TaskRepository
import com.loc.taskmanager.ui.screens.employee.EmployeeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.loc.taskmanager.model.domain.utils.Result
/**
 * @HiltViewModel: Hilt'e bu sınıfın bir ViewModel olduğunu ve bağımlılık enjekte edilebileceğini söyler.
 * @Inject constructor: TaskRepository arayüzünü AppModule'den otomatik olarak alır.
 */
@HiltViewModel
class EmployeeViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel() {

    // Arayüzün dinleyeceği değişken. Sadece ViewModel içinden (Mutable) değiştirilebilir.
    private val _uiState = MutableStateFlow(EmployeeUiState())
    // Dışarıya (Arayüze) sadece okunabilir (Read-Only) olarak sunulur.
    val uiState: StateFlow<EmployeeUiState> = _uiState.asStateFlow()

    /**
     * Bu fonksiyon ekran ilk açıldığında (veya kullanıcı giriş yaptığında) çağrılır.
     * Parametre olarak giriş yapan çalışanın Firebase ID'sini alır.
     */
    fun loadEmployeeTasks(employeeId: String) {
        viewModelScope.launch {
            // Repository'deki Flow'u dinlemeye (collect) başlıyoruz.
            repository.getTasksForEmployee(employeeId).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        // Yükleniyor durumu: UI'da bir progress bar gösterilmesini sağlar
                        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                    }
                    is Result.Success -> {
                        // Başarılı durumu: Gelen listeyi State'e ekle ve yükleniyor ikonunu kaldır
                        _uiState.update {
                            it.copy(isLoading = false, tasks = result.data, errorMessage = null)
                        }
                    }
                    is Result.Error -> {
                        // Hata durumu: Hata mesajını State'e yaz
                        _uiState.update {
                            it.copy(isLoading = false, errorMessage = result.exception.localizedMessage)
                        }
                    }
                }
            }
        }
    }

    /**
     * Kullanıcı ekrandaki bir görevin durumunu değiştirmek istediğinde çağrılır (Örn: "Yapılıyor" butonu).
     */
    fun changeTaskStatus(taskId: String, newStatus: String) {
        viewModelScope.launch {
            // Firestore güncellemesi yapılır.
            // Not: İşlem başarılı olduğunda listeyi manuel olarak güncellememize GEREK YOKTUR!
            // Çünkü Firestore 'addSnapshotListener' anlık olarak değişimi algılayıp yukarıdaki 'loadEmployeeTasks'
            // içindeki 'Result.Success' bloğuna yeni listeyi otomatik fırlatacaktır.
            repository.updateTaskStatus(taskId, newStatus)
        }
    }
}