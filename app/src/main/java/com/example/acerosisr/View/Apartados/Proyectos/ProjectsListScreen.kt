package com.example.acerosisr.View.Apartados.Proyectos

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.acerosisr.Model.Project
import com.example.acerosisr.Navigation.AppScreen
import com.example.acerosisr.Navigation.Navigation
import com.example.acerosisr.ViewModel.ProjectsViewModel
import com.example.acerosisr.ui.theme.AcerosISRTheme
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectsListScreen(
    navController: Navigation,
    projectsViewModel: ProjectsViewModel
) {
    val projects by projectsViewModel.projects.collectAsState()
    val isLoading by projectsViewModel.isLoading.collectAsState()
    val errorMessage by projectsViewModel.errorMessage.collectAsState()

    LaunchedEffect(Unit) {
        projectsViewModel.loadProjects()
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Proyectos") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigateTo(AppScreen.CreateProject.createRoute(null))
            }) {
                Icon(Icons.Filled.Add, "Crear Nuevo Proyecto")
            }
        }
    ) { paddingValues ->

        // Aquí puedes mostrar loading / error si quieres

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(projects) { project ->
                ProjectListItem(project = project) {
                    navController.navigateTo(AppScreen.ProjectDetail.createRoute(project.id ?: 0))
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
            Text(text = project.titulo, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Descripción: ${project.descripcion}", style = MaterialTheme.typography.bodySmall)
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
