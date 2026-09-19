package com.loc.taskmanager.model.domain.repository

import com.loc.taskmanager.model.domain.utils.Result

interface AuthRepository {
    // Giriş yapma işlemi. Başarılı olursa kullanıcının Firebase UID'sini döndürür.
    suspend fun login(email: String, password: String): Result<String>

    // Kayıt olma işlemi. Firebase Auth'ta hesap oluşturup Firestore'a profil verisini kaydeder.
    suspend fun register(name: String, email: String, password: String): Result<String>

    // Sistemden çıkış yapma işlemi.
    fun logout()

    // Oturum açık olan mevcut kullanıcının UID'sini getirir (Uygulama açılışında otomatik giriş için).
    fun getCurrentUserId(): String?

    // ... diğer fonksiyonlar (login, register vb.)
    suspend fun getUserRole(userId: String): Result<String>
}