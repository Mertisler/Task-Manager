package com.loc.taskmanager.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.loc.taskmanager.ui.screens.admin.AdminDashboardScreen
import com.loc.taskmanager.ui.screens.admin.CreateTaskScreen
import com.loc.taskmanager.ui.screens.auth.LoginScreen
import com.loc.taskmanager.ui.screens.auth.RegisterScreen
import com.loc.taskmanager.ui.screens.employee.EmployeeDashboardScreen
import com.loc.taskmanager.ui.screens.splash.SplashViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "splash_screen") {

        // --- 1. YÖNLENDİRİCİ (SPLASH) EKRANI ---
        composable("splash_screen") {
            val splashViewModel: SplashViewModel = hiltViewModel()
            val destination by splashViewModel.startDestination.collectAsState()

            // Hedef belirlendiğinde yönlendirmeyi yap ve splash'i geçmişten sil
            LaunchedEffect(destination) {
                destination?.let { route ->
                    navController.navigate(route) {
                        popUpTo("splash_screen") { inclusive = true }
                    }
                }
            }

            // Karar verilene kadar ekranda sadece bir yüklenme ikonu görünür
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        // --- 2. AUTH EKRANLARI ---
        composable("login_screen") {
            LoginScreen(
                onLoginSuccess = {
                    // Giriş başarılı olunca Splash'e gönder, o rolü bulup doğru sayfaya atsın
                    navController.navigate("splash_screen") {
                        popUpTo("login_screen") { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate("register_screen") }
            )
        }

        composable("register_screen") {
            RegisterScreen(
                onRegisterSuccess = {
                    // Kayıt başarılı olunca Splash'e gönder
                    navController.navigate("splash_screen") {
                        popUpTo("login_screen") { inclusive = true }
                    }
                },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        // --- 3. ÇALIŞAN EKRANLARI ---
        composable("employee_dashboard") {
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            EmployeeDashboardScreen(
                employeeId = currentUserId,
                onNavigateToAdmin = { /* Çalışanların admin paneline geçiş butonu olmamalı, bu parametreyi kaldırabilir veya boş bırakabilirsin */ }
            )
        }

        // --- 4. YÖNETİCİ EKRANLARI ---
        composable("admin_dashboard") {
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            AdminDashboardScreen(
                adminId = currentUserId,
                onNavigateToCreateTask = {
                    navController.navigate("admin_create_task")
                }
            )
        }

        composable("admin_create_task") {
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            CreateTaskScreen(
                adminId = currentUserId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}