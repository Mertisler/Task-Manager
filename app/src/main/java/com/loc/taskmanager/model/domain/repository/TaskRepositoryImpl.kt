package com.loc.taskmanager.model.domain.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.loc.taskmanager.model.data.model.Task
import com.loc.taskmanager.model.domain.utils.Result
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : TaskRepository {

    // Koleksiyon adını bir değişkende tutmak yönetimi kolaylaştırır.
    private val taskCollection = firestore.collection("tasks")

    /**
     * ÇALIŞAN AKIŞI:
     * Firestore'dan sadece 'assignedToId' değeri parametre olarak gelen ID'ye eşit olanları çeker.
     * callbackFlow kullanarak verideki anlık değişiklikleri sürekli dinler (Realtime update).
     */
    override fun getTasksForEmployee(employeeId: String): Flow<Result<List<Task>>> = callbackFlow {
        // 1. Önce yükleniyor durumunu gönder
        trySend(Result.Loading)

        // 2. Firestore sorgusunu oluştur: 'assignedToId' alanı employeeId olanları filtrele ve tarihe göre sırala
        val listenerRegistration = taskCollection
            .whereEqualTo("assignedToId", employeeId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->

                // Hata varsa Flow'a hata fırlat
                if (error != null) {
                    trySend(Result.Error(error))
                    return@addSnapshotListener
                }

                // Hata yoksa belgeleri Task modeline çevir
                if (snapshot != null) {
                    val tasks = snapshot.documents.mapNotNull { document ->
                        document.toObject(Task::class.java)?.copy(id = document.id)
                        // copy(id = document.id) ile Firestore belge kimliğini (Document ID) Task'ın kendi id değişkenine atıyoruz.
                    }
                    // Başarılı sonucu Flow'a gönder
                    trySend(Result.Success(tasks))
                }
            }

        // 3. Flow iptal olduğunda (örneğin ekran kapandığında) Firestore dinleyicisini kaldır
        // Bu işlem bellek sızıntısını (memory leak) önler.
        awaitClose {
            listenerRegistration.remove()
        }
    }

    /**
     * YÖNETİCİ AKIŞI:
     * Sistemdeki tüm görevleri tarihe göre sıralayarak gerçek zamanlı getirir.
     */
    override fun getAllTasks(): Flow<Result<List<Task>>> = callbackFlow {
        trySend(Result.Loading)

        val listenerRegistration = taskCollection
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    trySend(Result.Error(error))
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val tasks = snapshot.documents.mapNotNull { document ->
                        document.toObject(Task::class.java)?.copy(id = document.id)
                    }
                    trySend(Result.Success(tasks))
                }
            }

        awaitClose {
            listenerRegistration.remove()
        }
    }

    /**
     * YÖNETİCİ AKIŞI (Tek seferlik işlem):
     * Firestore'a yeni bir görev belgesi (Document) ekler.
     */
    override suspend fun addTask(task: Task): Result<Unit> {
        return try {
            // taskCollection.add(task) Firestore'da otomatik benzersiz bir ID oluşturarak belgeyi kaydeder.
            // await() fonksiyonu, işlem bitene kadar Coroutine'i bekletir.
            taskCollection.add(task).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * ÇALIŞAN AKIŞI (Tek seferlik işlem):
     * Belirli bir görevin sadece "status" (durum) alanını günceller.
     * Tüm belgeyi ezmek (set) yerine, sadece bir alanı güncellemek (update) ağ trafiği ve güvenlik açısından en doğrusudur.
     */
    override suspend fun updateTaskStatus(taskId: String, newStatus: String): Result<Unit> {
        return try {
            // taskId'ye sahip belgeyi bul ve sadece 'status' alanını newStatus ile değiştir.
            taskCollection.document(taskId).update("status", newStatus).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}