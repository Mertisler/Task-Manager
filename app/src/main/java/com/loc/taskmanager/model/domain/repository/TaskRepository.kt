package com.loc.taskmanager.model.domain.repository

import com.loc.taskmanager.model.data.model.Task
import com.loc.taskmanager.model.domain.utils.Result
import kotlinx.coroutines.flow.Flow

interface TaskRepository {

    // 1. Çalışan için: Sadece kendi ID'si ile eşleşen görevleri gerçek zamanlı dinler
    fun getTasksForEmployee(employeeId: String): Flow<Result<List<Task>>>

    // 2. Yönetici için: Sistemdeki tüm görevleri listeler (isteğe bağlı olarak çalışan bazlı filtrelenebilir)
    fun getAllTasks(): Flow<Result<List<Task>>>

    // 3. Yönetici için: Firestore'a yeni bir görev ekler. (Tek seferlik işlem olduğu için suspend function)
    suspend fun addTask(task: Task): Result<Unit>

    // 4. Çalışan için: Sadece görevin 'status' alanını ("Yapılıyor", "Tamamlandı" olarak) günceller.
    suspend fun updateTaskStatus(taskId: String, newStatus: String): Result<Unit>
}