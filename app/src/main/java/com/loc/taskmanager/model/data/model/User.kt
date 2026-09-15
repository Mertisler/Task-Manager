package com.loc.taskmanager.model.data.model

data class User(
    val id: String = "",           // Firebase Auth UID
    val name: String = "",         // Kullanıcının adı
    val email: String = "",        // E-posta adresi
    val role: String = "Employee"  // Varsayılan rol "Employee". Admin paneli/veri tabanından "Admin" yapılabilir.
)
