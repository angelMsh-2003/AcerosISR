package com.example.acerosisr.Navigation

sealed class AppScreen(val route: String) {
    // Authentication
    object Login : AppScreen("login_screen")
    object Register : AppScreen("register_screen")

    // Main App Screens
    object Home : AppScreen("home_screen") // This could be InicioApp.kt

    // Menu Navigation
    object MenuInicio : AppScreen("menu_inicio_screen") // The main menu structure

    // Apartados (Sections)
    // Materials (formerly Materials and Herramientas)
    object MaterialsList : AppScreen("materials_list_screen")
    object MaterialDetail : AppScreen("material_detail_screen/{materialId}") {
        fun createRoute(materialId: Int) = "material_detail_screen/$materialId"
    }
    object MaterialMovement : AppScreen("material_movement_screen")

    // Tasks
    object TasksList : AppScreen("tasks_list_screen")
    object TaskDetail : AppScreen("task_detail_screen/{taskId}") {
        fun createRoute(taskId: Int) = "task_detail_screen/$taskId"
    }
    object CreateEditTask : AppScreen("create_edit_task_screen/{taskId}") {
        fun createRoute(taskId: Int?) = "create_edit_task_screen/${taskId ?: "new"}"
    }
    object TaskConsumption : AppScreen("task_consumption_screen/{taskId}") {
        fun createRoute(taskId: Int) = "task_consumption_screen/$taskId"
    }

    // Projects
    object ProjectsList : AppScreen("projects_list_screen")
    object ProjectDetail : AppScreen("project_detail_screen/{projectId}") {
        fun createRoute(projectId: Int) = "project_detail_screen/$projectId"
    }
    object CreateEditProject : AppScreen("create_edit_project_screen/{projectId}") {
        fun createRoute(projectId: Int?) = "create_edit_project_screen/${projectId ?: "new"}"
    }
    object ProjectReport : AppScreen("project_report_screen/{projectId}") {
        fun createRoute(projectId: Int) = "project_report_screen/$projectId"
    }

    // Employees/Users
    object EmployeeList : AppScreen("employee_list_screen")
    object EmployeeDetail : AppScreen("employee_detail_screen/{employeeId}") {
        fun createRoute(employeeId: Int) = "employee_detail_screen/$employeeId"
    }
    object EditProfile : AppScreen("edit_profile_screen") // For any user to edit their own profile

    // Activities Progress (Added)
    object ActivitiesProgress : AppScreen("activities_progress_screen")
}
