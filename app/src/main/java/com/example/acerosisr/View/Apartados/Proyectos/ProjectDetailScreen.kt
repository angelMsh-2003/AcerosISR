package com.example.acerosisr.View.Apartados.Proyectos

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.acerosisr.Navigation.AppScreen
import com.example.acerosisr.Navigation.Navigation
import com.example.acerosisr.ui.theme.AcerosISRTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(navController: Navigation, projectId: Int?) {
    // In a real app, fetch project details using projectId
    val project = projectId?.let {
        // Mock data for demonstration
        when (it) {
            1 -> Project(1, "Construcción de nave industrial", "Cliente A", "En progreso", 101)
            2 -> Project(2, "Rehabilitación de estructura metálica", "Cliente B", "Finalizado", 102)
            3 -> Project(3, "Fabricación de portones", "Cliente C", "Cancelado", 101)
            else -> null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(project?.descripcion ?: "Detalle del Proyecto") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, "Atrás")
                    }
                },
                actions = {
                    if (project != null) {
                        IconButton(onClick = { navController.navigateTo(AppScreen.CreateEditProject.createRoute(project.id)) }) {
                            Icon(Icons.Filled.Edit, "Editar Proyecto")
                        }
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
            if (project != null) {
                Text(text = "Descripción: ${project.descripcion}", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Cliente: ${project.cliente}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Estado: ${project.estado}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Registrado por Empleado ID: ${project.registradoPor}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Fecha de Registro: ${project.fechaRegistro}", style = MaterialTheme.typography.bodyMedium)
                // TODO: Display associated tasks and their progress
                // TODO: Option to view project report if finished
            } else {
                Text("Proyecto no encontrado.", style = MaterialTheme.typography.titleLarge)
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun ProjectDetailScreenPreview() {
//    AcerosISRTheme {
//        ProjectDetailScreen(navController = rememberNavController(), projectId = 1)
//    }
//}
