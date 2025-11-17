package com.example.acerosisr.View.Apartados.Tareas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.acerosisr.Navigation.AppScreen
import com.example.acerosisr.Navigation.Navigation
import com.example.acerosisr.ViewModel.TareasViewModel
import com.example.acerosisr.ui.theme.AcerosISRTheme
import com.example.acerosisr.ui.theme.BackgroundColor
import com.example.acerosisr.ui.theme.BackgroundColorTwo
import com.example.acerosisr.ui.theme.PrimaryColor
import com.example.acerosisr.ui.theme.TextColorWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AsignacionTareasScreen(navController: Navigation, tareasViewModel: TareasViewModel) { // Changed to Navigation and added TareasViewModel
    var projectName by remember { mutableStateOf("") }
    var taskDescription by remember { mutableStateOf("") }
    var assignedEmployee by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Asignar Tareas a Proyecto") },
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
                .background(BackgroundColorTwo)
        ) {
            OutlinedTextField(
                value = projectName,
                onValueChange = { projectName = it },
                label = { Text("Proyecto (ID o Nombre)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = taskDescription,
                onValueChange = { taskDescription = it },
                label = { Text("Descripción de la Tarea") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = assignedEmployee,
                onValueChange = { assignedEmployee = it },
                label = { Text("Empleado a Asignar (ID o Nombre)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { /* Save logic */ }, modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors( // Corrected: Used ButtonDefaults.buttonColors
                    containerColor = PrimaryColor,
                    contentColor = TextColorWhite
                )
            ) {
                Text("Asignar Tarea")
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun AsignacionTareasScreenPreview() {
//    AcerosISRTheme {
//        // Corrected Preview with mock Navigation and a placeholder TareasViewModel
//        AsignacionTareasScreen(
//            navController = object : Navigation {
//                override fun navigateTo(route: AppScreen) {}
//                override fun popBackStack() {}
//            },
//            tareasViewModel = TareasViewModel(TareasRepositoryImpl(ApiService("mock-api-url"))) // Provide a mock ViewModel
//        )
//    }
//}
