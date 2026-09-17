package com.loc.taskmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.loc.taskmanager.ui.AppNavigation
import com.loc.taskmanager.ui.theme.TaskManagerTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * @AndroidEntryPoint: Bu aktivitenin içindeki Compose ekranlarının (ve ViewModel'ların)
 * Hilt üzerinden bağımlılık alabilmesine izin verir.
 */
@AndroidEntryPoint

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Uygulamanın renk ve yazı tiplerini belirleyen ana tema sarmalayıcısı
            TaskManagerTheme {
                // Arayüzün arka planını belirleyen kapsayıcı
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Ekranları yönetecek olan Navigasyon fonksiyonumuzu çağırıyoruz
                    AppNavigation()
                }
            }
        }
    }
}