package com.loc.taskmanager.ui.screens.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit, // Giriş başarılı olduğunda yönlendirme yapacak fonksiyon
    onNavigateToRegister: () -> Unit, // Kayıt ol sayfasına geçiş yapacak fonksiyon
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // 4. Yönlendirme Akışı: Giriş başarılı olursa ana sayfaya git
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            viewModel.resetState() // Ekran verilerini temizle
            onLoginSuccess() // Yönlendirmeyi tetikle
        }
    }

    // Ekranı dikey eksende ortalayarak çizen ana kutu
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Başlık
        Text(
            text = "Görev Yöneticisi",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Hata mesajı varsa göster
        if (uiState.errorMessage != null) {
            Text(
                text = uiState.errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // 2. Kullanıcı Etkileşimi: E-posta Girdisi
        OutlinedTextField(
            value = uiState.email,
            onValueChange = { viewModel.updateEmail(it) },
            label = { Text("E-posta") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading,
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Kullanıcı Etkileşimi: Şifre Girdisi
        OutlinedTextField(
            value = uiState.password,
            onValueChange = { viewModel.updatePassword(it) },
            label = { Text("Şifre") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading,
            singleLine = true,
            visualTransformation = PasswordVisualTransformation() // Şifreyi yıldızlı gösterir
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 3. İşlem Akışı: Giriş Butonu
        Button(
            onClick = { viewModel.login() },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = !uiState.isLoading
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("Giriş Yap")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Kayıt sayfasına geçiş butonu (TextButton)
        Text(
            text = "Hesabınız yok mu? Kayıt Olun",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable(
                enabled = !uiState.isLoading,
                onClick = onNavigateToRegister
            )
        )
    }
}