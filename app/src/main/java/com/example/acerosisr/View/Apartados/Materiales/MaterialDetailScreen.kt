package com.example.acerosisr.View.Apartados.Materiales

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.acerosisr.Model.Material
import com.example.acerosisr.Navigation.Navigation
import com.example.acerosisr.ViewModel.MaterialsViewModel
import com.example.acerosisr.ui.theme.PrimaryColor
import com.example.acerosisr.ui.theme.TextColorWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaterialDetailScreen(
    navController: Navigation,
    materialId: Int?,
    materialsViewModel: MaterialsViewModel
) {
    val selectedMaterial by materialsViewModel.selectedMaterial.collectAsState()
    val isLoading by materialsViewModel.isLoading.collectAsState()
    val errorMessage by materialsViewModel.errorMessage.collectAsState()
    val globalError by materialsViewModel.errorMessage.collectAsState()

    val tipos = listOf("tubo", "soldadura", "lamina", "herramienta", "otro")
    val unidades = listOf("pieza", "kg", "ton", "m", "lt")

    var tipoExpanded by remember { mutableStateOf(false) }
    var unidadExpanded by remember { mutableStateOf(false) }


    var nombre by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("") }
    var unidadMedida by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }

    var localError by remember { mutableStateOf<String?>(null) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    // Cargar material
    LaunchedEffect(materialId) {
        materialId?.let { materialsViewModel.loadMaterialDetail(it) }
    }

    // Setea campos cuando llega el material
    LaunchedEffect(selectedMaterial) {
        selectedMaterial?.let { m ->
            nombre = m.nombre
            tipo = m.tipo
            unidadMedida = m.unidadMedida
            stock = m.stockActual.toString()
            descripcion = m.descripcion ?: ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(selectedMaterial?.nombre ?: "Detalle del Material") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator()
                return@Column
            }

//            if (!errorMessage.isNullOrBlank()) {
//                Text(errorMessage ?: "", color = MaterialTheme.colorScheme.error)
//            }

            val mat = selectedMaterial
            if (mat == null) {
                Text("Material no encontrado")
                return@Column
            }

            // ---------- CAMPOS EDITABLES ----------
            OutlinedTextField(
                value = nombre, onValueChange = { nombre = it },
                label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            ExposedDropdownMenuBox(
                expanded = tipoExpanded,
                onExpandedChange = { tipoExpanded = !tipoExpanded }
            ) {
                OutlinedTextField(
                    value = tipo,
                    onValueChange = { }, // no se edita a mano
                    readOnly = true,
                    label = { Text("Tipo") },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = tipoExpanded)
                    },
                    colors = ExposedDropdownMenuDefaults.textFieldColors()
                )

                ExposedDropdownMenu(
                    expanded = tipoExpanded,
                    onDismissRequest = { tipoExpanded = false }
                ) {
                    tipos.forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion) },
                            onClick = {
                                tipo = opcion
                                tipoExpanded = false
                            }
                        )
                    }
                }
            }


            Spacer(Modifier.height(8.dp))

            ExposedDropdownMenuBox(
                expanded = unidadExpanded,
                onExpandedChange = { unidadExpanded = !unidadExpanded }
            ) {
                OutlinedTextField(
                    value = unidadMedida,
                    onValueChange = { }, // no se edita a mano
                    readOnly = true,
                    label = { Text("Unidad de medida") },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = unidadExpanded)
                    },
                    colors = ExposedDropdownMenuDefaults.textFieldColors()
                )

                ExposedDropdownMenu(
                    expanded = unidadExpanded,
                    onDismissRequest = { unidadExpanded = false }
                ) {
                    unidades.forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion) },
                            onClick = {
                                unidadMedida = opcion
                                unidadExpanded = false
                            }
                        )
                    }
                }
            }


            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = stock,
                onValueChange = { stock = it },
                label = { Text("Stock Actual") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            localError?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            // ---------- BOTONES PUT / DELETE ----------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // PUT - Guardar
                Button(
                    onClick = {
                        localError = null

                        val stockValue = stock.toDoubleOrNull()
                        if (stockValue == null || stockValue < 0) {
                            localError = "Stock inválido"
                            return@Button
                        }

                        val updated = Material(
                            id = mat.id,
                            tipo = tipo,
                            nombre = nombre,
                            unidadMedida = unidadMedida,
                            stockActual = stockValue,
                            descripcion = descripcion.ifBlank { null }
                        )

                        materialsViewModel.updateMaterial(
                            updated,
                            onSuccess = { navController.popBackStack() },
                            onError = { localError = it }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryColor,
                        contentColor = TextColorWhite
                    )
                ) {
                    Icon(Icons.Default.Done, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Guardar")
                }

                // DELETE - Eliminar
                OutlinedButton(
                    onClick = { showDeleteConfirm = true },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(Icons.Default.Delete, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Eliminar")
                }
            }

            // ---------- DIÁLOGO DE CONFIRMACIÓN ----------
            if (showDeleteConfirm) {
                AlertDialog(
                    onDismissRequest = { showDeleteConfirm = false },
                    title = { Text("Eliminar Material") },
                    text = { Text("¿Estás seguro de eliminar este material?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showDeleteConfirm = false
                                materialsViewModel.deleteMaterial(
                                    id = mat.id,
                                    onSuccess = { navController.popBackStack() },
                                    onError = { /* mostrar error */ }
                                )
                            }
                        ) {
                            Text("Eliminar", color = MaterialTheme.colorScheme.error)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteConfirm = false }) {
                            Text("Cancelar")
                        }
                    }
                )
            }
        }
    }
    val errorToShow = localError ?: globalError
    if (errorToShow != null) {
        AlertDialog(
            onDismissRequest = {
                localError = null
                materialsViewModel.clearError()
            },
            title = { Text("No se pudo eliminar") },
            text = { Text(errorToShow) },
            confirmButton = {
                TextButton(onClick = {
                    localError = null
                    materialsViewModel.clearError()
                }) {
                    Text("Aceptar")
                }
            }
        )
    }
}
