package com.example.acerosisr.View.Apartados.Materiales

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.materialIcon
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.acerosisr.Model.Material // Added import
import com.example.acerosisr.Navigation.AppScreen
import com.example.acerosisr.Navigation.Navigation
import com.example.acerosisr.ViewModel.MaterialsViewModel
import com.google.android.libraries.play.games.inputmapping.Input

// Removed the local data class Material definition

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaterialListScreen(
    navController: Navigation,
    materialsViewModel: MaterialsViewModel
) {
    val materials by materialsViewModel.materials.collectAsState()
    val isLoading by materialsViewModel.isLoading.collectAsState()
    val error by materialsViewModel.errorMessage.collectAsState()

    // ESTADOS PARA LA VENTANA EMERGENTE (POPUP)
    var showDialog by remember { mutableStateOf(false) }
    var selectedMaterial by remember { mutableStateOf<Material?>(null) }

    LaunchedEffect(Unit) {
        materialsViewModel.loadMaterials()
    }

    // --- DIALOGO EXISTENTE (Gestión Material) ---
    if (showDialog && selectedMaterial != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            icon = { Icon(Icons.Filled.Create, contentDescription = null) },
            title = { Text(text = "Gestionar Material") },
            text = {
                Column {
                    Text(
                        text = selectedMaterial!!.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("¿Qué deseas hacer con este material?")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false
                        navController.navigateTo(
                            AppScreen.MaterialReception.createRoute(selectedMaterial!!.id)
                        )
                    }
                ) {
                    Text("Recepción")
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        showDialog = false
                        navController.navigateTo(
                            AppScreen.MaterialDetail.createRoute(selectedMaterial!!.id)
                        )
                    }
                ) {
                    Text("Detalles / Editar")
                }
            }
        )
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Materiales") }) },
        floatingActionButton = {
            // Columna para poner los botones uno encima del otro, o Fila para lado a lado.
            // Usaremos columna para estilo "Speed Dial" clásico.
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // BOTÓN NUEVO: REPORTE
                SmallFloatingActionButton(
                    onClick = {
                        // Navegar a la nueva vista de Reportes
                        // Asegúrate de agregar "material_report" a tu Navigation Graph
                        navController.navigateTo(AppScreen.MaterialReport)
                    },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ) {
                    Icon(Icons.Filled.Menu, "Ver Reporte de Movimientos")
                }

                // BOTÓN EXISTENTE: AGREGAR MATERIAL
                FloatingActionButton(onClick = {
                    navController.navigateTo(AppScreen.MaterialMovement)
                }) {
                    Icon(Icons.Filled.Add, "Agregar Material")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (!error.isNullOrBlank()) {
                Text(error ?: "", color = MaterialTheme.colorScheme.error)
            }

            LazyColumn {
                items(materials) { material ->
                    MaterialListItem(material = material) { clickedMaterial ->
                        selectedMaterial = clickedMaterial
                        showDialog = true
                    }
                }
            }
        }
    }
}
@Composable
fun MaterialListItem(material: Material, onClick: (Material) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick(material) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = material.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Badge(containerColor = MaterialTheme.colorScheme.secondaryContainer) {
                    Text(
                        text = "${material.stockActual} ${material.unidadMedida}",
                        modifier = Modifier.padding(4.dp),
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Tipo: ${material.tipo}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)

            material.descripcion?.let {
                if (it.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = it, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}