package com.example.acerosisr.Navigation

sealed class AppScreen(val route: String) {
    // Authentication
    object Login : AppScreen("login_screen")
    object Register : AppScreen("register_screen")
    object RecoveryPassword : AppScreen("recovery_password")

    // Main App Screens
    object Home : AppScreen("home_screen") // This could be InicioApp.kt

    // Menu Navigation
    object MenuInicio : AppScreen("menu_inicio_screen") // The main menu structure

    // Apartados (Sections)
    // Materials (formerly Materials and Herramientas)
    object MaterialsList : AppScreen("materials_list_screen")
    object WorkerMaterialsList : AppScreen("worker_materials_list_screen") // ✅ NUEVO: Para Trabajador (Solo lectura)

    object MaterialDetail : AppScreen("material_detail_screen/{materialId}") {
        fun createRoute(materialId: Int?) = "material_detail_screen/$materialId"
    }
    object MaterialReception : AppScreen("material_reception_screen/{materialId}") {
        fun createRoute(materialId: Int?) = "material_reception_screen/$materialId"
    }
    object MaterialMovement : AppScreen("material_movement_screen")
    object MaterialReport : AppScreen("material_report_screen")

    // Tasks
    object ProjectTasksList : AppScreen("project_tasks_list_screen")
    object ProjectTasksScreen : AppScreen("project_tasks_screen/{projectId}") {
        fun createRoute(projectId: Int): String = "project_tasks_screen/$projectId"
    }
    object MyTasksScreen : AppScreen("my_tasks_screen")


    // Projects
    object ProjectsList : AppScreen("projects_list_screen")
    object ProjectDetail : AppScreen("project_detail_screen/{projectId}") {
        fun createRoute(projectId: Int) = "project_detail_screen/$projectId"
    }
    object CreateProject : AppScreen("create_edit_project_screen/{projectId}") {
        fun createRoute(projectId: Int?) = "create_edit_project_screen/${projectId ?: "new"}"
    }
    object ProjectReport : AppScreen("project_report_screen/{projectId}") {
        fun createRoute(projectId: Int) = "project_report_screen/$projectId"
    }
    object EditProject : AppScreen("edit_project_screen/{projectId}") {
        fun editRoute(projectId: Int?) = "edit_project_screen/${projectId ?: "new"}"
    }

    // Employees/Users
    object EmployeeList : AppScreen("employee_list_screen")
    object EditProfile : AppScreen("edit_profile_screen")


    // Activities Progress (Added)
    object ActivitiesProgress : AppScreen("activities_progress_screen")
}
