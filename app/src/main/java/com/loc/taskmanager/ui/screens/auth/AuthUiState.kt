package com.loc.taskmanager.ui.screens.auth

data class AuthUiState(
    val name: String = "",        // Sadece kayıt olurken kullanılacak
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)
