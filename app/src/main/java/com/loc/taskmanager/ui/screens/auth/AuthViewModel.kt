package com.loc.taskmanager.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.loc.taskmanager.model.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.loc.taskmanager.model.domain.utils.Result

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository

) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun updateName(newName: String) {
        _uiState.update { it.copy(name = newName, errorMessage = null) }
    }

    fun updateEmail(newEmail: String) {
        _uiState.update { it.copy(email = newEmail, errorMessage = null) }
    }

    fun updatePassword(newPassword: String) {
        _uiState.update { it.copy(password = newPassword, errorMessage = null) }
    }

    fun login() {
        val state = _uiState.value

        // Girdi Kontrolü
        if (state.email.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "E-posta ve şifre boş bırakılamaz.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // Repository üzerinden Firebase Auth Login isteği
            when (val result = repository.login(state.email, state.password)) {
                is Result.Success -> {
                    // Başarılı girişte state güncellenir, UI bunu dinleyip yönlendirme yapar
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = result.exception.localizedMessage)
                    }
                }
                is Result.Loading -> Unit
            }
        }
    }

    fun register() {
        val state = _uiState.value

        // Girdi Kontrolü
        if (state.name.isBlank() || state.email.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Lütfen tüm alanları doldurun.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // Repository üzerinden Firebase Auth Kayıt ve Firestore User oluşturma isteği
            when (val result = repository.register(state.name, state.email, state.password)) {
                is Result.Success -> {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = result.exception.localizedMessage)
                    }
                }
                is Result.Loading -> Unit
            }
        }
    }

    // Başarılı giriş/kayıt sonrası state'i sıfırlamak için (Geri dönüldüğünde eski veriler kalmasın diye)
    fun resetState() {
        _uiState.update { AuthUiState() }
    }
}