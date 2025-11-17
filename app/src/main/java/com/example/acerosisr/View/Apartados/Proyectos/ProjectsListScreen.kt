package com.example.acerosisr.View.Apartados.Proyectos

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.acerosisr.Navigation.AppScreen
import com.example.acerosisr.Navigation.Navigation
import com.example.acerosisr.ui.theme.AcerosISRTheme
import java.util.Date

data class Project(val id: Int, val descripcion: String, val cliente: String, val estado: String, val registradoPor: Int, val fechaRegistro: Date = Date())

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectsListScreen(navController: Navigation) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Proyectos") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigateTo(AppScreen.CreateEditProject.createRoute(null))
            }) {
                Icon(Icons.Filled.Add, "Crear Nuevo Proyecto")
            }
        }
    ) { paddingValues ->
        val sampleProjects = listOf(
            Project(1, "Construcción de nave industrial", "Cliente A", "En progreso", 101),
            Project(2, "Rehabilitación de estructura metálica", "Cliente B", "Finalizado", 102),
            Project(3, "Fabricación de portones", "Cliente C", "Cancelado", 101)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(sampleProjects) { project ->
                ProjectListItem(project = project) {
                    navController.navigateTo(AppScreen.ProjectDetail.createRoute(project.id))
                }
            }
        }
    }
}

@Composable
fun ProjectListItem(project: Project, onClick: (Project) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick(project) }
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(text = "Descripción: ${project.descripcion}", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Cliente: ${project.cliente}", style = MaterialTheme.typography.bodySmall)
            Text(text = "Estado: ${project.estado}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun ProjectsListScreenPreview() {
//    AcerosISRTheme {
//        ProjectsListScreen(navController = rememberNavController())
//    }
//}
