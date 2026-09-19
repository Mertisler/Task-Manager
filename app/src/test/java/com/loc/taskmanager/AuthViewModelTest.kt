package com.loc.taskmanager

import com.loc.taskmanager.model.domain.repository.AuthRepository
import com.loc.taskmanager.ui.screens.auth.AuthViewModel
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import io.mockk.coEvery
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.advanceUntilIdle
import com.loc.taskmanager.model.domain.utils.Result


@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    // Sahte Repository ve Test edilecek ViewModel
    private lateinit var mockRepository: AuthRepository
    private lateinit var viewModel: AuthViewModel

    // Coroutines testleri için test dağıtıcısı (Main thread'i taklit eder)
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        // Test başlamadan önce Main thread'i test dispatcher ile değiştir
        Dispatchers.setMain(testDispatcher)
        // Repository'nin sahte (mock) halini üret (Gerçek veri tabanına bağlanmaz)
        mockRepository = mockk(relaxed = true)
        // ViewModel'a sahte repository'yi enjekte et
        viewModel = AuthViewModel(mockRepository)
    }

    @After
    fun tearDown() {
        // Test bitince Main thread'i sıfırla
        Dispatchers.resetMain()
    }

    @Test
    fun `login with empty email and password updates state with error message`() {
        // 1. ARRANGE (Hazırla): Kullanıcı arayüzde girdileri boş bırakmış gibi davran
        viewModel.updateEmail("")
        viewModel.updatePassword("")

        // 2. ACT (Harekete Geç): Giriş fonksiyonunu tetikle
        viewModel.login()

        // 3. ASSERT (Doğrula): State'in beklenen hata mesajını verdiğini doğrula
        val currentState = viewModel.uiState.value
        assertEquals("E-posta ve şifre boş bırakılamaz.", currentState.errorMessage)
        assertEquals(false, currentState.isLoading)
    }

    @Test
    fun `login with valid credentials updates state to success`() = runTest {
        // 1. Hazırlık (Arrange): Doğru bilgiler girildiğinde Başarılı (Success) dönmesini sağla
        val testEmail = "test@test.com"
        val testPassword = "password123"
        val fakeUid = "user_12345"

        // suspend fonksiyonları taklit etmek için coEvery kullanılır
        coEvery { mockRepository.login(testEmail, testPassword) } returns Result.Success(fakeUid)

        viewModel.updateEmail(testEmail)
        viewModel.updatePassword(testPassword)

        // 2. Tetikleme (Act): Giriş işlemini başlat ve Coroutine'in bitmesini bekle
        viewModel.login()
        advanceUntilIdle()

        // 3. Doğrulama (Assert): State'in başarı durumuna geçtiğini onayla
        val currentState = viewModel.uiState.value
        assertEquals(true, currentState.isSuccess)
        assertEquals(false, currentState.isLoading)
        assertEquals(null, currentState.errorMessage)
    }

    @Test
    fun `login with invalid credentials updates state with error message`() = runTest {
        // 1. Hazırlık (Arrange): Yanlış bilgiler girildiğinde Hata (Error) dönmesini sağla
        val testEmail = "test@test.com"
        val testPassword = "wrongpassword"
        val exceptionMessage = "Şifre yanlış."

        coEvery { mockRepository.login(testEmail, testPassword) } returns Result.Error(Exception(exceptionMessage))

        viewModel.updateEmail(testEmail)
        viewModel.updatePassword(testPassword)

        // 2. Tetikleme (Act): Giriş işlemini başlat ve bekle
        viewModel.login()
        advanceUntilIdle()

        // 3. Doğrulama (Assert): State'in hata durumuna geçtiğini onayla
        val currentState = viewModel.uiState.value
        assertEquals(false, currentState.isSuccess)
        assertEquals(false, currentState.isLoading)
        assertEquals(exceptionMessage, currentState.errorMessage)
    }

}