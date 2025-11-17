package com.example.acerosisr.View.Apartados.NuevoEmpleado

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.acerosisr.Navigation.Navigation

// Assuming Employee data class is defined in EmployeeListScreen.kt or a common data file
// For simplicity, defining it here if not globally accessible
//data class Employee(val id: Int, val name: String, val role: String, val email: String? = null)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeDetailScreen(navController: Navigation, employeeId: Int?) {
    var employeeName by remember { mutableStateOf("") }
    var employeeRole by remember { mutableStateOf("") }
    var employeeEmail by remember { mutableStateOf("") }

    val isEditing = employeeId != null && employeeId != -1

    // Mock data for demonstration
    val employee = if (isEditing && employeeId == 101) {
        Employee(101, "Juan Pérez", "admin", "juan.perez@example.com")
    } else null

    if (isEditing && employee != null) {
        employeeName = employee.name
        employeeRole = employee.role
        employeeEmail = employee.email
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Detalle/Editar Empleado" else "Nuevo Empleado") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, "Atrás")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = employeeName,
                onValueChange = { employeeName = it },
                label = { Text("Nombre del Empleado") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = isEditing // Assuming name is not editable after creation
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = employeeRole,
                onValueChange = { employeeRole = it },
                label = { Text("Rol (admin, trabajador, primario)") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = isEditing && employeeRole != "admin" // Example: only admin can change roles
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = employeeEmail,
                onValueChange = { employeeEmail = it },
                label = { Text("Correo Electrónico") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { /* TODO: Implement save logic */ }, modifier = Modifier.fillMaxWidth()) {
                Text(if (isEditing) "Guardar Cambios" else "Crear Empleado")
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun EmployeeDetailScreenPreview() {
//    AcerosISRTheme {
//        EmployeeDetailScreen(object : Navigation {
//            override fun navigateTo(route: AppScreen.MaterialMovement) {}
//            override fun popBackStack() {}
//        }, employeeId = 101) // Preview existing employee
//    }
//}
