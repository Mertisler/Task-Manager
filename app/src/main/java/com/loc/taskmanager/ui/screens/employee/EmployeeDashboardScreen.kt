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
    employeeId: String, // Giriş yapan kullanıcının ID'si (Örn: Navigation parametresi olarak gelir)
    viewModel: EmployeeViewModel = hiltViewModel() // Hilt, ViewModel'ı otomatik olarak oluşturup buraya verir
) {
    // ViewModel'daki StateFlow'u Compose'un anlayacağı bir State'e dönüştürüyoruz
    val uiState by viewModel.uiState.collectAsState()

    // Ekran ilk açıldığında çalışacak olan blok (Sadece bir kere tetiklenir)
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
        }
    ) { paddingValues ->
        // Ekranın ana kutusu
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            // 1. DURUM: Yükleniyor
            if (uiState.isLoading) {
                CircularProgressIndicator()
            }
            // 2. DURUM: Hata var
            else if (uiState.errorMessage != null) {
                Text(
                    text = "Hata oluştu: ${uiState.errorMessage}",
                    color = MaterialTheme.colorScheme.error
                )
            }
            // 3. DURUM: Liste Boş
            else if (uiState.tasks.isEmpty()) {
                Text(text = "Şu anda atanmış bir göreviniz bulunmuyor.")
            }
            // 4. DURUM: Görevler başarıyla geldi
            else {
                // Performanslı liste görünümü (Sadece ekranda görünenleri çizer)
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Listedeki her bir eleman için TaskItemCard bileşenini çağır
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