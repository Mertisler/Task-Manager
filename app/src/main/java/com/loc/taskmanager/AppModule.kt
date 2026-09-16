package com.loc.taskmanager

import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * @Module: Hilt'e bu sınıfın bağımlılık ürettiğini söyler.
 * @InstallIn(SingletonComponent::class): Bu modülde üretilen nesnelerin uygulamanın yaşam döngüsü boyunca (Application Scope)
 * sadece tek bir defa oluşturulacağını (Singleton) belirtir.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Firebase Firestore Nesnesini Sağlayan Fonksiyon
     *
     * @Provides: Hilt'e bir nesnenin nasıl oluşturulacağını gösterir.
     * @Singleton: Uygulama çalıştığı sürece bu fonksiyondan sadece 1 adet Firestore nesnesi üretilir
     * ve her yere o dağıtılır. Bu sayede bellek dostu bir yapı kurarız.
     */
    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    /**
     * TaskRepository Arayüzünü Sağlayan Fonksiyon
     *
     * Eğer bir sınıfın (Örn: ViewModel) Constructor'ında 'TaskRepository' yazıyorsa,
     * Hilt bu fonksiyona gelir. Bu fonksiyon da arayüzün (interface) somut uygulamasını (TaskRepositoryImpl) döndürür.
     *
     * Parametre olarak (firestore) alıyor, çünkü TaskRepositoryImpl bunu istiyor.
     * Hilt bu parametreyi bir üstteki 'provideFirebaseFirestore' fonksiyonundan otomatik alıp buraya koyar.
     */
    @Provides
    @Singleton
    fun provideTaskRepository(firestore: FirebaseFirestore): TaskRepository {
        return TaskRepositoryImpl(firestore)
    }
}