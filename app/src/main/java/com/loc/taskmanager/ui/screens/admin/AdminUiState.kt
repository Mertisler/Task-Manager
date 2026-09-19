package com.loc.taskmanager.ui.screens.admin

import com.loc.taskmanager.model.data.model.Task

data class AdminUiState(
    val isLoading: Boolean = false,
    val tasks: List<Task> = emptyList(),
    val errorMessage: String? = null
)