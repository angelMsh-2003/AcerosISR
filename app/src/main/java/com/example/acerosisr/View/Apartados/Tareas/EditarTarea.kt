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
import com.example.acerosisr.Data.ApiService
import com.example.acerosisr.Data.TareasRepositoryImpl
import com.example.acerosisr.Navigation.AppScreen
import com.example.acerosisr.Navigation.Navigation
import com.example.acerosisr.ViewModel.TareasViewModel
import com.example.acerosisr.ui.theme.AcerosISRTheme
import com.example.acerosisr.ui.theme.BackgroundColor
import com.example.acerosisr.ui.theme.PrimaryColor
import com.example.acerosisr.ui.theme.TextColorWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarTareaScreen(navController: Navigation, taskId: Int?, tareasViewModel: TareasViewModel) { // Added TareasViewModel
    // Observe the selected task from the ViewModel
    val selectedTask by tareasViewModel.selectedTask.collectAsState()

    // Use LaunchedEffect to load the task when the screen is first composed
    LaunchedEffect(taskId) {
        taskId?.let { tareasViewModel.getTaskById(it.toLong()) }
    }

    var taskName by remember { mutableStateOf("") }
    var taskDescription by remember { mutableStateOf("") }
    var taskStatus by remember { mutableStateOf("") }

    // Update local state when the selected task from ViewModel changes
    LaunchedEffect(selectedTask) {
        selectedTask?.let {
            taskName = it.comentarios ?: ""
            taskDescription = it.comentarios ?: "" // Assuming description is in comentarios
            taskStatus = it.estado
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Tarea ${taskId ?: ""}") },
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
                .background(BackgroundColor)
        ) {
            OutlinedTextField(
                value = taskName,
                onValueChange = { taskName = it },
                label = { Text("Nombre de la Tarea") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = taskDescription,
                onValueChange = { taskDescription = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Dropdown for status
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = taskStatus,
                    onValueChange = { /* read-only */ },
                    readOnly = true,
                    label = { Text("Estado") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(text = { Text("Pendiente") }, onClick = { taskStatus = "Pendiente"; expanded = false })
                    DropdownMenuItem(text = { Text("En proceso") }, onClick = { taskStatus = "En proceso"; expanded = false })
                    DropdownMenuItem(text = { Text("Completada") }, onClick = { taskStatus = "Completada"; expanded = false })
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { /* TODO: Save logic -> Call tareasViewModel.updateTask(...) */ }, modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryColor,
                    contentColor = TextColorWhite
                )
            ) {
                Text("Guardar Cambios")
            }
        }
    }
}
//
//@Preview(showBackground = true)
//@Composable
//fun EditarTareaScreenPreview() {
//    AcerosISRTheme {
//        EditarTareaScreen(
//            navController = object : Navigation {
//                override fun navigateTo(route: AppScreen.MaterialMovement) {}
//                override fun popBackStack() {}
//            },
//            taskId = 1,
//            tareasViewModel = TareasViewModel(TareasRepositoryImpl(ApiService("mock-api-url")))
//        )
//    }
//}
