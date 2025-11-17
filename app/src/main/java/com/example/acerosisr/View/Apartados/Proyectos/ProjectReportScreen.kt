package com.example.acerosisr.View.Apartados.Proyectos

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
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
import com.example.acerosisr.Navigation.Navigation
import com.example.acerosisr.ui.theme.AcerosISRTheme

// Assuming a data class for material movements related to a project
data class ProjectMaterialMovement(
    val materialName: String,
    val cantidadUsada: Double,
    val fechaUso: String,
    val observaciones: String?
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectReportScreen(navController: Navigation, projectId: Int?) {
    // In a real app, fetch project and its material movements using projectId
    val project = projectId?.let {
        // Mock project data
        when (it) {
            2 -> Project(2, "Rehabilitación de estructura metálica", "Cliente B", "Finalizado", 102)
            else -> null
        }
    }

    val materialMovements = if (projectId == 2) listOf(
        ProjectMaterialMovement("Lámina Lisa Cal. 16", 10.0, "2023-10-20", "Corte para paneles"),
        ProjectMaterialMovement("Soldadura 7018", 2.5, "2023-10-21", null)
    ) else emptyList() // Mock data

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reporte de Proyecto: ${project?.descripcion ?: "N/A"}") },
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
            if (project != null) {
                Text(text = "Cliente: ${project.cliente}", style = MaterialTheme.typography.titleMedium)
                Text(text = "Estado: ${project.estado}", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Movimientos de Material:", style = MaterialTheme.typography.titleLarge)
                LazyColumn {
                    items(materialMovements) { movement ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = "Material: ${movement.materialName}")
                                Text(text = "Cantidad Usada: ${movement.cantidadUsada}")
                                Text(text = "Fecha de Uso: ${movement.fechaUso}")
                                movement.observaciones?.let { Text(text = "Obs: $it") }
                            }
                        }
                    }
                }
            } else {
                Text("Proyecto no encontrado.", style = MaterialTheme.typography.titleLarge)
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun ProjectReportScreenPreview() {
//    AcerosISRTheme {
//        ProjectReportScreen(navController = rememberNavController(), projectId = 2)
//    }
//}
