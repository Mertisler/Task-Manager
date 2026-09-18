package com.loc.taskmanager.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.loc.taskmanager.ui.screens.admin.CreateTaskScreen
import com.loc.taskmanager.ui.screens.auth.LoginScreen
import com.loc.taskmanager.ui.screens.auth.RegisterScreen
import com.loc.taskmanager.ui.screens.employee.EmployeeDashboardScreen


@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // İsteğe Bağlı Mantık: Kullanıcı zaten giriş yapmışsa doğrudan dashboard'dan başlat
    val currentUser = FirebaseAuth.getInstance().currentUser
    val startRoute = if (currentUser != null) "employee_dashboard" else "login_screen"

    NavHost(navController = navController, startDestination = startRoute) {

        // 1. Giriş Yap Ekranı
        composable("login_screen") {
            LoginScreen(
                onLoginSuccess = {
                    // Giriş başarılıysa dashboard'a git ve login sayfasını geçmişten sil (popUpTo)
                    navController.navigate("employee_dashboard") {
                        popUpTo("login_screen") { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register_screen")
                }
            )
        }

        // 2. Kayıt Ol Ekranı
        composable("register_screen") {
            RegisterScreen(
                onRegisterSuccess = {
                    // Kayıt başarılıysa dashboard'a git ve tüm auth geçmişini sil
                    navController.navigate("employee_dashboard") {
                        popUpTo("login_screen") { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    // Geri dön
                    navController.popBackStack()
                }
            )
        }

        composable("employee_dashboard") {
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

            EmployeeDashboardScreen(
                employeeId = currentUserId,
                onNavigateToAdmin = {
                    navController.navigate("admin_create_task")
                }
            )
        }

        // 4. Yönetici Görev Ekleme Ekranı
        composable("admin_create_task") {
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

            CreateTaskScreen(
                adminId = currentUserId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}