package com.loc.taskmanager.ui.screens.employee

import com.loc.taskmanager.model.data.model.Task

data class EmployeeUiState(
    val isLoading: Boolean = false,
    val tasks: List<Task> = emptyList(),
    val errorMessage: String? = null
)
