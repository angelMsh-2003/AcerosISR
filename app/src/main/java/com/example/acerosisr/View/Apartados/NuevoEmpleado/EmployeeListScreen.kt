package com.example.acerosisr.View.Apartados.NuevoEmpleado

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.acerosisr.Navigation.AppScreen // Added import
import com.example.acerosisr.Navigation.Navigation // Added import
import com.example.acerosisr.ui.theme.BackgroundColor
import com.example.acerosisr.ui.theme.PrimaryColor

data class Employee(val id: Int, val name: String, val role: String, val email: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeListScreen(navController: Navigation) { // Changed NavHostController to Navigation
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestionar Empleados") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) { // Used popBackStack from Navigation interface
                        Icon(Icons.Filled.ArrowBack, "Atrás")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                // Navigate to screen to add new employee
                navController.navigateTo(AppScreen.EmployeeDetail.createRoute(-1)) // Used navigateTo
            }) {
                Icon(Icons.Filled.Add, "Agregar Empleado")
            }
        }
    ) { paddingValues ->
        val sampleEmployees = listOf(
            Employee(101, "Juan Pérez", "admin", "juan.perez@example.com"),
            Employee(102, "María García", "trabajador", "juan.perez@example.com"),
            Employee(103, "Carlos López", "primario", "juan.perez@example.com")
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(BackgroundColor)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(sampleEmployees) { employee ->
                EmployeeListItem(employee = employee) {
                    navController.navigateTo(AppScreen.EmployeeDetail.createRoute(employee.id)) // Used navigateTo
                }
            }
        }
    }
}

@Composable
fun EmployeeListItem(employee: Employee, onClick: (Employee) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick(employee) }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = employee.name, style = MaterialTheme.typography.titleMedium)
                Text(text = "Rol: ${employee.role}", style = MaterialTheme.typography.bodySmall)
            }
            // Example for an action button for each employee
            IconButton(onClick = { /* TODO: Implement edit/delete actions */ }) {
                Icon(Icons.Default.Add, contentDescription = "More actions", tint = PrimaryColor)
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun EmployeeListScreenPreview() {
//    AcerosISRTheme {
//        // Need a mock Navigation instance for preview
//        EmployeeListScreen(object : Navigation { // Mock Navigation
//            override fun navigateTo(route: AppScreen.MaterialMovement) {
//                // Do nothing for preview
//            }
//            override fun popBackStack() {
//                // Do nothing for preview
//            }
//        })
//    }
//}
