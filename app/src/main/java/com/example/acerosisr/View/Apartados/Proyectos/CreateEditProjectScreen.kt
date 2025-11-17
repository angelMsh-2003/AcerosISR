package com.example.acerosisr.View.Apartados.Proyectos

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.acerosisr.Navigation.Navigation
import com.example.acerosisr.ui.theme.AcerosISRTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEditProjectScreen(navController: Navigation, projectId: Int?) {
    var description by remember { mutableStateOf("") }
    var client by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("En progreso") }

    val isEditing = projectId != null && projectId != -1 // Assuming -1 or null for new project

    // In a real app, fetch existing project data if isEditing is true
    LaunchedEffect(projectId) {
        if (isEditing) {
            // Simulate fetching data
            val existingProject = when (projectId) {
                1 -> Project(1, "Construcción de nave industrial", "Cliente A", "En progreso", 101)
                else -> null
            }
            existingProject?.let { 
                description = it.descripcion
                client = it.cliente
                status = it.estado
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Editar Proyecto" else "Crear Nuevo Proyecto") },
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
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción del Proyecto") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = client,
                onValueChange = { client = it },
                label = { Text("Cliente") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Simple dropdown for status
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = status,
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
                    DropdownMenuItem(text = { Text("En progreso") }, onClick = { status = "En progreso"; expanded = false })
                    DropdownMenuItem(text = { Text("Finalizado") }, onClick = { status = "Finalizado"; expanded = false })
                    DropdownMenuItem(text = { Text("Cancelado") }, onClick = { status = "Cancelado"; expanded = false })
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { /* TODO: Implement save/update logic */ }, modifier = Modifier.fillMaxWidth()) {
                Text(if (isEditing) "Guardar Cambios" else "Crear Proyecto")
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun CreateEditProjectScreenPreview() {
//    AcerosISRTheme {
//        CreateEditProjectScreen(navController = rememberNavController(), projectId = null)
//    }
//}
