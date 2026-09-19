package com.loc.taskmanager.ui.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.loc.taskmanager.model.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.loc.taskmanager.model.domain.utils.Result


@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    // Gidilecek hedefin rota adını (String) tutar
    private val _startDestination = MutableStateFlow<String?>(null)
    val startDestination: StateFlow<String?> = _startDestination.asStateFlow()

    init {
        checkUserStatus()
    }

    private fun checkUserStatus() {
        val userId = authRepository.getCurrentUserId()

        if (userId == null) {
            // Oturum yoksa doğrudan giriş ekranına at
            _startDestination.value = "login_screen"
        } else {
            // Oturum varsa Firestore'dan rolü çek
            viewModelScope.launch {
                when (val result = authRepository.getUserRole(userId)) {
                    is Result.Success -> {
                        if (result.data == "Admin") {
                            _startDestination.value = "admin_dashboard"
                        } else {
                            _startDestination.value = "employee_dashboard"
                        }
                    }
                    is Result.Error -> {
                        // Rol çekerken hata olursa güvenlik için çıkış yap ve login'e yönlendir
                        authRepository.logout()
                        _startDestination.value = "login_screen"
                    }
                    is Result.Loading -> Unit
                }
            }
        }
    }
}