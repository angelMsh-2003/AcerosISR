package com.example.acerosisr.View.Apartados.Materiales

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.acerosisr.Model.Material
import com.example.acerosisr.Navigation.Navigation
import com.example.acerosisr.ViewModel.MaterialsViewModel
import com.example.acerosisr.ui.theme.PrimaryColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkerMaterialsScreen(
    navController: Navigation,
    materialsViewModel: MaterialsViewModel
) {
    val materials by materialsViewModel.materials.collectAsState()
    val isLoading by materialsViewModel.isLoading.collectAsState()

    // Estado para el Popup de Detalle (Solo Lectura)
    var showDetailDialog by remember { mutableStateOf(false) }
    var selectedMaterial by remember { mutableStateOf<Material?>(null) }

    LaunchedEffect(Unit) {
        materialsViewModel.loadMaterials()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Materiales Disponibles") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, "Regresar")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (materials.isEmpty()) {
                        item {
                            Text("No hay materiales registrados", modifier = Modifier.padding(16.dp))
                        }
                    }
                    items(materials) { material ->
                        MaterialReadOnlyItem(material) {
                            selectedMaterial = material
                            showDetailDialog = true
                        }
                    }
                }
            }
        }
    }

    // --- POPUP DETALLE (SOLO LECTURA) ---
    if (showDetailDialog && selectedMaterial != null) {
        val mat = selectedMaterial!!
        AlertDialog(
            onDismissRequest = { showDetailDialog = false },
            icon = { Icon(Icons.Filled.Inventory2, null, tint = PrimaryColor) },
            title = { Text(mat.nombre, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    DetailRow("ID:", "#${mat.id}")
                    DetailRow("Tipo:", mat.tipo.uppercase())
                    DetailRow("Stock Actual:", "${mat.stockActual} ${mat.unidadMedida}")
                    Divider()
                    Text("Descripción:", fontWeight = FontWeight.SemiBold)
                    Text(
                        text = if (mat.descripcion.isNullOrBlank()) "Sin descripción" else mat.descripcion,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            },
            confirmButton = {
                Button(onClick = { showDetailDialog = false }) {
                    Text("Cerrar")
                }
            }
        )
    }
}

@Composable
fun MaterialReadOnlyItem(material: Material, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono o inicial
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = PrimaryColor.copy(alpha = 0.1f),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.Info, null, tint = PrimaryColor)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = material.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${material.stockActual} ${material.unidadMedida}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if(material.stockActual > 0) Color(0xFF2E7D32) else Color.Red
                )
            }
        }
    }
}
