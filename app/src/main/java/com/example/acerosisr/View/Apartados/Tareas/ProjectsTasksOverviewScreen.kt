package com.example.acerosisr.View.Apartados.Tareas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.acerosisr.Model.ProjectTasksSummary
import com.example.acerosisr.Navigation.AppScreen
import com.example.acerosisr.Navigation.Navigation
import com.example.acerosisr.ViewModel.TareasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectsTasksOverviewScreen(
    navController: Navigation,
    tareasViewModel: TareasViewModel
) {
    val proyectos by tareasViewModel.projectsTasksSummary.collectAsState()
    val isLoading by tareasViewModel.isLoading.collectAsState()
    val errorMessage by tareasViewModel.errorMessage.collectAsState()

    // Cargar resumen de tareas por proyecto al entrar
    LaunchedEffect(Unit) {
        tareasViewModel.loadProjectsTasksSummary()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tareas por proyecto") }
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
                isLoading && proyectos.isEmpty() -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Cargando proyectos...")
                    }
                }

                errorMessage != null && proyectos.isEmpty() -> {
                    Text(
                        text = errorMessage ?: "Error al cargar proyectos",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                proyectos.isEmpty() -> {
                    Text(
                        text = "No hay proyectos registrados.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(proyectos) { resumen ->
                            ProjectTasksCard(
                                resumen = resumen,
                                onClick = {
                                    navController.navigateTo(
                                        AppScreen.ProjectTasksScreen.createRoute(resumen.proyectoId)
                                    )
                                }
                            )

                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProjectTasksCard(
    resumen: ProjectTasksSummary,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Encabezado: ID, cliente, estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Proyecto #${resumen.proyectoId}",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = resumen.titulo,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Text(
                    text = resumen.estado,
                    style = MaterialTheme.typography.bodyMedium,
                    color = when (resumen.estado) {
                        "Finalizado" -> Color(0xFF2E7D32) // verde fuerte
                        "Cancelado" -> Color(0xFFC62828) // rojo
                        else -> Color(0xFF1565C0)       // azul
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (!resumen.tareasAsignadas) {
                // Sin tareas asignadas
                Text(
                    text = "Sin tareas asignadas",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                // Barra de progreso segmentada
                SegmentedTasksBar(resumen = resumen)

                Spacer(modifier = Modifier.height(8.dp))

                // Texto con porcentajes
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Pendientes: ${resumen.tareasPendientes}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFFBC02D) // amarillo
                    )
                    Text(
                        text = "En proceso: ${resumen.tareasEnProceso}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF1976D2) // azul
                    )
                    Text(
                        text = "Completadas: ${resumen.tareasCumplidas}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF2E7D32) // verde
                    )
                }
            }
        }
    }
}

@Composable
private fun SegmentedTasksBar(
    resumen: ProjectTasksSummary
) {
    val total = (resumen.tareasPendientes + resumen.tareasEnProceso + resumen.tareasCumplidas)
        .coerceAtLeast(1) // evitar división entre 0

    val pendingWeight = resumen.tareasPendientes.toFloat() / total.toFloat()
    val inProgressWeight = resumen.tareasEnProceso.toFloat() / total.toFloat()
    val doneWeight = resumen.tareasCumplidas.toFloat() / total.toFloat()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(12.dp)
    ) {
        if (pendingWeight > 0f) {
            Box(
                modifier = Modifier
                    .weight(pendingWeight)
                    .fillMaxHeight()
                    .background(Color(0xFFFBC02D)) // amarillo
            )
        }
        if (inProgressWeight > 0f) {
            Box(
                modifier = Modifier
                    .weight(inProgressWeight)
                    .fillMaxHeight()
                    .background(Color(0xFF1976D2)) // azul
            )
        }
        if (doneWeight > 0f) {
            Box(
                modifier = Modifier
                    .weight(doneWeight)
                    .fillMaxHeight()
                    .background(Color(0xFF2E7D32)) // verde
            )
        }
    }
}
