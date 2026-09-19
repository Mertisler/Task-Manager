package com.loc.taskmanager.model.domain.repository

import com.loc.taskmanager.model.domain.utils.Result
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.loc.taskmanager.model.data.model.User
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<String> {
        return try {
            // 1. Firebase Auth üzerinden giriş isteği at
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid

            if (userId != null) {
                Result.Success(userId)
            } else {
                Result.Error(Exception("Kullanıcı kimliği alınamadı."))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun register(name: String, email: String, password: String): Result<String> {
        return try {
            // 1. Firebase Auth'ta yeni kullanıcı oluştur
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid

            if (userId != null) {
                // 2. Firestore'a kaydedilecek User modelini oluştur (Varsayılan rol: Employee)
                val newUser = User(
                    id = userId,
                    name = name,
                    email = email,
                    role = "Employee"
                )

                // 3. Firestore'da 'users' koleksiyonuna belge ID'si olarak UID'yi vererek kaydet
                firestore.collection("users")
                    .document(userId).set(newUser).await()

                Result.Success(userId)
            } else {
                Result.Error(Exception("Kullanıcı oluşturulamadı."))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun logout() {
        firebaseAuth.signOut()
    }

    override fun getCurrentUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }

    override suspend fun getUserRole(userId: String): Result<String> {
        return try {
            // UID'ye sahip belgeyi Firestore'dan getir
            val document = firestore.collection("users").document(userId).get().await()
            // Belge içindeki 'role' alanını oku, bulamazsa varsayılan olarak "Employee" ata
            val role = document.getString("role") ?: "Employee"
            Result.Success(role)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}