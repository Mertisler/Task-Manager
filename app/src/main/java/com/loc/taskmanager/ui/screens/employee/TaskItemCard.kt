package com.loc.taskmanager.ui.screens.employee

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.loc.taskmanager.model.data.model.Task

@Composable
fun TaskItemCard(
    task: Task,
    onStatusChangeClick: (String) -> Unit // Butona tıklandığında üst katmana haber verecek fonksiyon
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Başlık
            Text(
                text = task.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Açıklama
            Text(
                text = task.description,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Mevcut Durum Göstergesi
            Text(
                text = "Durum: ${task.status}",
                color = if (task.status == "Tamamlandı") Color.Green else Color.Blue,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // İşlem Butonları (Durum Güncelleme)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (task.status == "Bekliyor") {
                    Button(onClick = { onStatusChangeClick("Yapılıyor") }) {
                        Text("Başla")
                    }
                } else if (task.status == "Yapılıyor") {
                    Button(
                        onClick = { onStatusChangeClick("Tamamlandı") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                    ) {
                        Text("Tamamla")
                    }
                }
            }
        }
    }
}