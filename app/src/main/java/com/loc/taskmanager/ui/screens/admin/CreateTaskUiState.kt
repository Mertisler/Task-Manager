package com.loc.taskmanager.ui.screens.admin

data class CreateTaskUiState(
    val title: String = "",
    val description: String = "",
    val assignedToId: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)
