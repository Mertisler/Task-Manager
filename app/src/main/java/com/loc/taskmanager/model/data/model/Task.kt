package com.loc.taskmanager.model.data.model

data class Task(
    val id: String = "",               // Firestore Document ID (Görev belgesinin kendi kimliği)
    val title: String = "",            // Görev başlığı
    val description: String = "",      // Görev detayı
    val assignedToId: String = "",     // Görevin atandığı çalışanın Firebase UID'si (İlişkiyi kuran alan)
    val createdById: String = "",      // Görevi oluşturan yöneticinin Firebase UID'si
    val status: String = "Bekliyor",   // Görev durumu: "Bekliyor", "Yapılıyor", "Tamamlandı"
    val createdAt: Long = System.currentTimeMillis() // Tarihe göre sıralama yapmak için zaman damgası
)
