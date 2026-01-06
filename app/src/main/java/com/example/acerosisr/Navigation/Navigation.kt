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
import com.example.acerosisr.View.Apartados.Materiales.MaterialReceptionScreen
import com.example.acerosisr.View.Apartados.Materiales.MaterialReportScreen
import com.example.acerosisr.View.Apartados.Materiales.WorkerMaterialsScreen
import com.example.acerosisr.View.Apartados.NuevoEmpleado.EmpleadosListaScreen
import com.example.acerosisr.View.Apartados.Proyectos.ProjectsListScreen
import com.example.acerosisr.View.Apartados.Proyectos.ProjectDetailScreen
import com.example.acerosisr.View.Apartados.Proyectos.ProjectReportScreen
import com.example.acerosisr.View.Apartados.Proyectos.CreateProjectScreen
import com.example.acerosisr.View.Apartados.Proyectos.EditDeleteProjectScreen
import com.example.acerosisr.View.Apartados.Tareas.MyTasksScreen
import com.example.acerosisr.View.Apartados.Tareas.ProjectTasksScreen
import com.example.acerosisr.View.Apartados.Tareas.ProjectsTasksOverviewScreen
import com.example.acerosisr.View.Inicio.RecuperarPasswordScreen
import com.example.acerosisr.View.Menu.EditUserScreen
import com.example.acerosisr.View.Menu.MenuInicio
import com.example.acerosisr.ViewModel.MaterialsViewModel
import com.example.acerosisr.ViewModel.ProjectsViewModel
import com.example.acerosisr.other.AppCloser

interface Navigation {
    fun navigateTo(route: AppScreen) // Rutas simples (Objetos)
    fun navigateTo(route: String)    // Rutas con argumentos (Strings generados)
    fun popBackStack()
}

private class NavigationImpl(private val navController: NavHostController) : Navigation {

    override fun navigateTo(route: AppScreen) {
        // Secciones principales que limpian el stack hasta el Menú
        val isRootSection = when (route) {
            AppScreen.MaterialsList,
            AppScreen.ProjectTasksList,
            AppScreen.ProjectsList,
            AppScreen.EmployeeList,
            AppScreen.ActivitiesProgress -> true
            else -> false
        }

        if (isRootSection) {
            navController.navigate(route.route) {
                launchSingleTop = true
                restoreState = false
                popUpTo(AppScreen.MenuInicio.route) {
                    inclusive = false
                }
            }
        } else {
            // Navegación estándar (Detalles, formularios, etc.)
            navController.navigate(route.route) {
                // Recomendado: Evita duplicados al dar click rápido
                launchSingleTop = true
            }
        }
    }

    // AQUI ESTÁ EL CAMBIO QUE BUSCAS PARA LAS RUTAS CON ARGUMENTOS
    override fun navigateTo(route: String) {
        navController.navigate(route) {
            // 1. Evita que se abran 2 veces si das doble click rápido
            launchSingleTop = true

            // 2. (OPCIONAL) Detección de rutas "Raíz" con parámetros
            // Si por alguna razón una ruta con ID (ej. ir directo a un proyecto desde una notificación)
            // debiera limpiar el historial, puedes detectarla así:

            /* if (route.startsWith("project_tasks_screen")) {
                popUpTo(AppScreen.MenuInicio.route) { inclusive = false }
            }
            */

            // Nota: Por lo general, las pantallas con ID son "Detalles" y NO deben
            // limpiar el historial (para que el botón Atrás funcione y regreses a la lista).
        }
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
    materialsViewModel: MaterialsViewModel,
    projectsViewModel : ProjectsViewModel,
    startDestination: String = AppScreen.Login.route
) {
    val appNavigation = NavigationImpl(navController)

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Authentication
        composable(AppScreen.Login.route) { Login(navController = appNavigation, loginModel = userViewModel) }
        composable(AppScreen.Register.route) { Registro(navController = appNavigation, loginModel = userViewModel) }

        // Main App entry point after login
        composable(AppScreen.Home.route) { InicioApp(navController = appNavigation, userViewModel = userViewModel) }
        composable(AppScreen.MenuInicio.route) {
            MenuInicio(navController = appNavigation, appCloser = appCloser, userViewModel = userViewModel)
        }
        composable(AppScreen.RecoveryPassword.route) { RecuperarPasswordScreen(navController = appNavigation, userViewModel = userViewModel) }


        // Materials
        composable(AppScreen.MaterialsList.route) { MaterialListScreen(navController = appNavigation, materialsViewModel=  materialsViewModel) }
        composable(AppScreen.MaterialDetail.route) { backStackEntry ->
            val materialId = backStackEntry.arguments?.getString("materialId")?.toIntOrNull()
            MaterialDetailScreen(navController = appNavigation, materialId = materialId, materialsViewModel = materialsViewModel)
        }
        composable(AppScreen.WorkerMaterialsList.route) {
            WorkerMaterialsScreen(navController = appNavigation, materialsViewModel = materialsViewModel)
        }

        composable(AppScreen.MaterialReception.route) { backStackEntry ->
            val materialId = backStackEntry.arguments?.getString("materialId")?.toIntOrNull()
            MaterialReceptionScreen(navController = appNavigation, materialId = materialId, materialsViewModel = materialsViewModel)
        }
        composable(AppScreen.MaterialMovement.route) { MaterialMovementScreen(navController = appNavigation, materialsViewModel= materialsViewModel) }
        composable(AppScreen.MaterialReport.route) { MaterialReportScreen(navController = appNavigation, materialsViewModel= materialsViewModel) }

        // Projects
        composable(AppScreen.ProjectsList.route) { ProjectsListScreen(navController = appNavigation, projectsViewModel = projectsViewModel) }
        composable(AppScreen.ProjectDetail.route) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId")?.toIntOrNull()
            ProjectDetailScreen(navController = appNavigation, projectId = projectId, projectsViewModel = projectsViewModel)
        }
        composable(AppScreen.CreateProject.route) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId")?.toIntOrNull()
            CreateProjectScreen(navController = appNavigation, projectsViewModel = projectsViewModel , currentUserId = 1)
        }
        composable(AppScreen.ProjectReport.route) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId")?.toIntOrNull()
            ProjectReportScreen(navController = appNavigation, projectId = projectId)
        }
        composable(AppScreen.EditProject.route) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId")?.toIntOrNull()
            EditDeleteProjectScreen(navController = appNavigation, projectId = projectId, projectsViewModel = projectsViewModel)
        }

        // Tasks
        composable(AppScreen.ProjectTasksList.route) { ProjectsTasksOverviewScreen(navController = appNavigation, tareasViewModel = tareasViewModel) }
        composable(AppScreen.ProjectTasksScreen.route) { backStackEntry ->
            val projectId = backStackEntry.arguments
                ?.getString("projectId")
                ?.toIntOrNull()

            ProjectTasksScreen(
                navController = appNavigation,
                projectId = projectId ?: 0,           // o mejor, hazlo nullable en la screen
                tareasViewModel = tareasViewModel
            )
        }
        composable(AppScreen.MyTasksScreen.route) { MyTasksScreen(navController = appNavigation, tareasViewModel = tareasViewModel, userViewModel = userViewModel) }
        composable(AppScreen.EditProfile.route) {
            EditUserScreen(navController = appNavigation, userViewModel = userViewModel)
        }

//        //composable(AppScreen.TasksList.route) { AsignacionTareasScreen(navController = appNavigation, tareasViewModel = tareasViewModel) }
//        composable(AppScreen.TaskDetail.route) { backStackEntry ->
//            val taskId = backStackEntry.arguments?.getString("taskId")?.toIntOrNull()
//            EditarTareaScreen(navController = appNavigation, taskId = taskId, tareasViewModel = tareasViewModel)
//        }
//        composable(AppScreen.CreateEditTask.route) { backStackEntry ->
//            val taskId = backStackEntry.arguments?.getString("taskId")?.toIntOrNull()
//            AgregarTareaScreen(navController = appNavigation, tareasViewModel = tareasViewModel)
//        }

        // Employees/Users
        composable(AppScreen.EmployeeList.route) { EmpleadosListaScreen(navController = appNavigation, userViewModel= userViewModel) }

        // Activities Progress
    }
}