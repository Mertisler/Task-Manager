package com.loc.taskmanager.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.loc.taskmanager.ui.screens.admin.CreateTaskScreen
import com.loc.taskmanager.ui.screens.employee.EmployeeDashboardScreen


@Composable
fun AppNavigation() {
    // Tüm ekran geçişlerini yönetecek olan denetleyici nesne
    val navController = rememberNavController()

    // Uygulama açıldığında ilk hangi sayfanın görüneceğini (startDestination) belirliyoruz.
    // Gerçek bir projede burası "LoginScreen" olur, şimdilik test amaçlı doğrudan Employee ekranını başlatıyoruz.
    NavHost(navController = navController, startDestination = "employee_dashboard") {

        // Çalışan Ekranı Rotası
        composable("employee_dashboard") {
            EmployeeDashboardScreen(
                employeeId = "test_calisan_id", // Normalde bu ID Login ekranından parametre olarak gelir
            )
        }

        // Yönetici Görev Oluşturma Ekranı Rotası
        composable("admin_create_task") {
            CreateTaskScreen(
                adminId = "test_admin_id",
                onNavigateBack = {
                    // İşlem başarılı olduğunda (görev eklendiğinde) bir önceki sayfaya dön
                    navController.popBackStack()
                }
            )
        }
    }
}