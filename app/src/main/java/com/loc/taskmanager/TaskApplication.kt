package com.loc.taskmanager

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * @HiltAndroidApp: Hilt'i tetikleyen ana anotasyondur.
 * Uygulama çalıştığı anda tüm bağımlılıkları (Repository, Firestore vb.) oluşturmak için hazırlık yapar.
 */
@HiltAndroidApp
class TaskApplication : Application()