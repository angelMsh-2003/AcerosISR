package com.example.acerosisr.View.Apartados.Proyectos

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.acerosisr.Navigation.AppNavHost
import com.example.acerosisr.Navigation.AppScreen
import com.example.acerosisr.Navigation.Navigation
import com.example.acerosisr.ViewModel.ProjectsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(
    navController: Navigation,
    projectId: Int?,
    projectsViewModel: ProjectsViewModel
) {
    val project by projectsViewModel.selectedProject.collectAsState()
    val isLoading by projectsViewModel.isLoading.collectAsState()
    val errorMessage by projectsViewModel.errorMessage.collectAsState()

    // Cuando cambie el id, cargamos el detalle desde el backend
    LaunchedEffect(projectId) {
        if (projectId != null) {
            projectsViewModel.loadProjectDetail(projectId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(project?.titulo ?: "Detalle del proyecto")
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    // Acción para editar (más adelante puedes usar una pantalla Create/Edit)
                    if (project != null && project!!.id != null) {
                        IconButton(
                            onClick = {
                                // Ajusta esta ruta si tienes una pantalla específica de edición
                                navController.navigateTo(

                                    AppScreen.EditProject.editRoute(project!!.id)
                                )
                            }
                        ) {
                            Icon(Icons.Filled.Edit, contentDescription = "Editar proyecto")
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
            when {
                projectId == null -> {
                    Text(
                        text = "Proyecto no válido.",
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                isLoading -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Cargando proyecto...")
                    }
                }

                errorMessage != null -> {
                    Text(
                        text = errorMessage ?: "Error desconocido",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                project != null -> {
                    Text(
                        text = "Título:",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(
                        text = project!!.titulo,
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Descripción:",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(
                        text = project!!.descripcion,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Cliente:",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(
                        text = project!!.cliente,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Estado:",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(
                        text = project!!.estado,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Registrado por (empleado_id):",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(
                        text = project!!.registradoPor?.toString() ?: "Sin registrar",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Fecha de registro:",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(
                        text = project!!.fechaRegistro,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                else -> {
                    Text(
                        text = "Proyecto no encontrado.",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
        }
    }
}
