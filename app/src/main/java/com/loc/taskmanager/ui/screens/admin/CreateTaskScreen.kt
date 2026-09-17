package com.loc.taskmanager.ui.screens.admin


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskScreen(
    adminId: String,
    onNavigateBack: () -> Unit, // İşlem başarılı olduğunda önceki sayfaya dönmek için callback
    viewModel: CreateTaskViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // 4. Sonuç Başarılıysa ekranı kapat
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Yeni Görev Oluştur") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hata mesajı gösterimi
            if (uiState.errorMessage != null) {
                Text(text = uiState.errorMessage!!, color = MaterialTheme.colorScheme.error)
            }

            // Başlık Girdisi
            OutlinedTextField(
                value = uiState.title,
                onValueChange = { viewModel.updateTitle(it) },
                label = { Text("Görev Başlığı") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            )

            // Açıklama Girdisi
            OutlinedTextField(
                value = uiState.description,
                onValueChange = { viewModel.updateDescription(it) },
                label = { Text("Görev Açıklaması") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                enabled = !uiState.isLoading
            )

            // Not: Gerçek projede buraya Firestore'dan çekilen çalışanların listelendiği bir DropdownMenu eklenmelidir.
            // Şimdilik test amaçlı manuel ID girilecek bir alan bırakıyoruz.
            OutlinedTextField(
                value = uiState.assignedToId,
                onValueChange = { viewModel.updateAssignedEmployee(it) },
                label = { Text("Atanacak Çalışan ID'si") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            )

            Spacer(modifier = Modifier.weight(1f))

            // Kaydet Butonu
            Button(
                onClick = { viewModel.createTask(adminId) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Görevi Oluştur")
                }
            }
        }
    }
}