package com.example.acerosisr.Navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.acerosisr.ViewModel.TareasViewModel
import com.example.acerosisr.ViewModel.UserViewModel
import com.example.acerosisr.View.Inicio.InicioApp
import com.example.acerosisr.View.Inicio.Login
import com.example.acerosisr.View.Inicio.Registro
import com.example.acerosisr.View.Apartados.Materiales.MaterialListScreen
import com.example.acerosisr.View.Apartados.Materiales.MaterialDetailScreen
import com.example.acerosisr.View.Apartados.Materiales.MaterialMovementScreen
import com.example.acerosisr.View.Apartados.Proyectos.ProjectsListScreen
import com.example.acerosisr.View.Apartados.Proyectos.ProjectDetailScreen
import com.example.acerosisr.View.Apartados.Proyectos.CreateEditProjectScreen
import com.example.acerosisr.View.Apartados.Proyectos.ProjectReportScreen
import com.example.acerosisr.View.Apartados.Tareas.AsignacionTareasScreen
import com.example.acerosisr.View.Apartados.Tareas.EditarTareaScreen
import com.example.acerosisr.View.Apartados.Tareas.AgregarTareaScreen
import com.example.acerosisr.View.Apartados.Tareas.TaskConsumptionScreen
import com.example.acerosisr.View.Apartados.NuevoEmpleado.EmployeeListScreen
import com.example.acerosisr.View.Apartados.NuevoEmpleado.EmployeeDetailScreen
import com.example.acerosisr.View.Apartados.ProcesoActividades.ActivitiesProgressScreen
import com.example.acerosisr.View.Menu.MenuInicio
import com.example.acerosisr.other.AppCloser

interface Navigation {
    fun navigateTo(route: AppScreen) // For simple routes
    fun navigateTo(route: String)   // Overload for routes with arguments
    fun popBackStack()
}

private class NavigationImpl(private val navController: NavHostController) : Navigation {
    override fun navigateTo(route: AppScreen) {
        navController.navigate(route.route)
    }

    override fun navigateTo(route: String) { // Implementation for the String overload
        navController.navigate(route)
    }

    override fun popBackStack() {
        navController.popBackStack()
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    userViewModel: UserViewModel,
    tareasViewModel: TareasViewModel,
    appCloser: AppCloser,
    startDestination: String = AppScreen.Login.route
) {
    val appNavigation = NavigationImpl(navController)

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // ... (resto del código de AppNavHost sin cambios) ...
        // Authentication
        composable(AppScreen.Login.route) { Login(navController = appNavigation, loginModel = userViewModel) }
        composable(AppScreen.Register.route) { Registro(navController = appNavigation, loginModel = userViewModel) }

        // Main App entry point after login
        composable(AppScreen.Home.route) { InicioApp(navController = appNavigation, userViewModel = userViewModel) }
        composable(AppScreen.MenuInicio.route) {
            MenuInicio(navController = appNavigation, appCloser = appCloser, userViewModel = userViewModel)
        }

        // Materials
        composable(AppScreen.MaterialsList.route) { MaterialListScreen(navController = appNavigation) }
        composable(AppScreen.MaterialDetail.route) { backStackEntry ->
            val materialId = backStackEntry.arguments?.getString("materialId")?.toIntOrNull()
            MaterialDetailScreen(navController = appNavigation, materialId = materialId)
        }
        composable(AppScreen.MaterialMovement.route) { MaterialMovementScreen(navController = appNavigation) }

        // Projects
        composable(AppScreen.ProjectsList.route) { ProjectsListScreen(navController = appNavigation) }
        composable(AppScreen.ProjectDetail.route) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId")?.toIntOrNull()
            ProjectDetailScreen(navController = appNavigation, projectId = projectId)
        }
        composable(AppScreen.CreateEditProject.route) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId")?.toIntOrNull()
            CreateEditProjectScreen(navController = appNavigation, projectId = projectId)
        }
        composable(AppScreen.ProjectReport.route) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId")?.toIntOrNull()
            ProjectReportScreen(navController = appNavigation, projectId = projectId)
        }

        // Tasks
        composable(AppScreen.TasksList.route) { AsignacionTareasScreen(navController = appNavigation, tareasViewModel = tareasViewModel) }
        composable(AppScreen.TaskDetail.route) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId")?.toIntOrNull()
            EditarTareaScreen(navController = appNavigation, taskId = taskId, tareasViewModel = tareasViewModel)
        }
        composable(AppScreen.CreateEditTask.route) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId")?.toIntOrNull()
            AgregarTareaScreen(navController = appNavigation, tareasViewModel = tareasViewModel)
        }
        composable(AppScreen.TaskConsumption.route) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId")?.toIntOrNull()
            TaskConsumptionScreen(navController = appNavigation, taskId = taskId)
        }

        // Employees/Users
        composable(AppScreen.EmployeeList.route) { EmployeeListScreen(navController = appNavigation) }
        composable(AppScreen.EmployeeDetail.route) { backStackEntry ->
            val employeeId = backStackEntry.arguments?.getString("employeeId")?.toIntOrNull()
            EmployeeDetailScreen(navController = appNavigation, employeeId = employeeId)
        }
        composable(AppScreen.EditProfile.route) { /* TODO: Implement EditProfileScreen */ }

        // Activities Progress
        composable(AppScreen.ActivitiesProgress.route) { ActivitiesProgressScreen(navController = appNavigation, tareasViewModel = tareasViewModel) }
    }
}
