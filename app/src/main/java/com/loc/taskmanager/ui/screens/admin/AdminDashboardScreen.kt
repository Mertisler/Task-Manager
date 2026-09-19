package com.loc.taskmanager.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.loc.taskmanager.model.data.model.Task


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    adminId: String,
    onNavigateToCreateTask: () -> Unit, // Görev oluşturma ekranına geçiş komutu
    viewModel: AdminViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Yönetim Paneli - Tüm Görevler") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToCreateTask() },
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
            } else if (uiState.errorMessage != null) {
                Text(
                    text = "Hata oluştu: ${uiState.errorMessage}",
                    color = MaterialTheme.colorScheme.error
                )
            } else if (uiState.tasks.isEmpty()) {
                Text(text = "Sistemde henüz oluşturulmuş bir görev bulunmuyor.")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.tasks) { task ->
                        AdminTaskItemCard(task = task)
                    }
                }
            }
        }
    }
}

// Yöneticinin listesinde görünecek kart bileşeni
@Composable
fun AdminTaskItemCard(task: Task) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = task.description, style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(16.dp))

            // Çalışan bilgisi ve durum yan yana
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Atanan ID: ${task.assignedToId}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Text(
                    text = task.status,
                    color = if (task.status == "Tamamlandı") Color.Green else Color.Blue,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}