package com.example.acerosisr.View.Apartados.Materiales

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.acerosisr.Model.MaterialMovementReport
import com.example.acerosisr.Navigation.Navigation
import com.example.acerosisr.ViewModel.MaterialsViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaterialReportScreen(
    navController: Navigation,
    materialsViewModel: MaterialsViewModel
) {
    val reports by materialsViewModel.movementsReport.collectAsState()
    val isLoading by materialsViewModel.isLoading.collectAsState()

    // Estado para el Popup de detalle
    var showDetailDialog by remember { mutableStateOf(false) }
    var selectedReport by remember { mutableStateOf<MaterialMovementReport?>(null) }

    LaunchedEffect(Unit) {
        materialsViewModel.loadMovementsReport()
    }

    // VENTANA EMERGENTE (POPUP DETALLE)
    if (showDetailDialog && selectedReport != null) {
        val item = selectedReport!!
        AlertDialog(
            onDismissRequest = { showDetailDialog = false },
            icon = { Icon(Icons.Filled.Info, contentDescription = null) },
            title = { Text("Detalle del Movimiento") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    DetailRow("ID Movimiento", "#${item.movimientoId}")
                    DetailRow("Material", item.materialName)
                    DetailRow("Tipo", item.tipoMovimiento.uppercase())
                    DetailRow("Cantidad", "${item.cantidad}")
                    DetailRow("Fecha", formatDateRaw(item.fecha))
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    Text("Observaciones:", fontWeight = FontWeight.Bold)
                    Text(item.observaciones ?: "Sin observaciones", style = MaterialTheme.typography.bodyMedium)
                }
            },
            confirmButton = {
                TextButton(onClick = { showDetailDialog = false }) {
                    Text("Cerrar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reporte de Movimientos") },
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
                    items(reports) { report ->
                        ReportItemCard(report = report) {
                            selectedReport = report
                            showDetailDialog = true
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReportItemCard(
    report: MaterialMovementReport,
    onClick: () -> Unit
) {
    val isEntrada = report.tipoMovimiento.lowercase().contains("entrada")
    val icon = if (isEntrada) Icons.Filled.Info else Icons.Filled.Close
    val iconColor = if (isEntrada) Color(0xFF2E7D32) else Color(0xFFC62828) // Verde o Rojo
    val cardContainer = if(isEntrada) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)

    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = cardContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono visual del tipo de movimiento
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Nombre del Material
                Text(
                    text = report.materialName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    // Tipo de Movimiento
                    Text(
                        text = report.tipoMovimiento.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.bodyMedium,
                        color = iconColor
                    )

                    // Fecha formateada
                    Text(
                        text = formatDateRaw(report.fecha),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = "$label:", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}

// Función auxiliar para formatear la fecha
// Entrada esperada: "2025-11-18T19:19:03.043963"
// Salida deseada: "18/11/2025 - 19:19"
fun formatDateRaw(dateString: String): String {
    return try {
        // Asumiendo que la fecha viene con fracción de segundos o formato ISO estándar
        // Ajusta el patrón de entrada según lo que devuelve exactamente tu backend si varía
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        // Si el string es muy largo por los microsegundos, cortamos
        val cleanDate = if (dateString.length > 19) dateString.substring(0, 19) else dateString

        val date = inputFormat.parse(cleanDate)
        val outputFormat = SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault())

        if (date != null) outputFormat.format(date) else dateString
    } catch (e: Exception) {
        dateString // Si falla, retorna el string original
    }
}