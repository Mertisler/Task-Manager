package com.loc.taskmanager.ui.screens.employee

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.loc.taskmanager.ui.viewmodels.EmployeeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeDashboardScreen(
    employeeId: String,
    onNavigateToAdmin: () -> Unit,
    viewModel: EmployeeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(key1 = employeeId) {
        viewModel.loadEmployeeTasks(employeeId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Görevlerim") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToAdmin() },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Text("+", color = Color.White, style = MaterialTheme.typography.titleLarge)
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator()
            }
            else if (uiState.errorMessage != null) {
                Text(
                    text = "Hata oluştu: ${uiState.errorMessage}",
                    color = MaterialTheme.colorScheme.error
                )
            }
            else if (uiState.tasks.isEmpty()) {
                Text(text = "Şu anda atanmış bir göreviniz bulunmuyor.")
            }
            else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.tasks) { task ->
                        TaskItemCard(
                            task = task,
                            onStatusChangeClick = { newStatus ->
                                viewModel.changeTaskStatus(task.id, newStatus)
                            }
                        )
                    }
                }
            }
        }
    }
}