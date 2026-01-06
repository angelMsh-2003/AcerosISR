package com.example.acerosisr.View.Apartados.Materiales

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.acerosisr.Navigation.Navigation
import com.example.acerosisr.ViewModel.MaterialsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaterialReceptionScreen(
    navController: Navigation,
    materialId: Int?,
    materialsViewModel: MaterialsViewModel
) {
    // Cargamos la info del material para mostrar el nombre en el título
    LaunchedEffect(materialId) {
        materialsViewModel.loadMaterialDetail(materialId)
    }

    val selectedMaterial by materialsViewModel.selectedMaterial.collectAsState()
    val isLoading by materialsViewModel.isLoading.collectAsState()
    val errorMessage by materialsViewModel.errorMessage.collectAsState()

    // Estados del formulario
    var cantidad by remember { mutableStateOf("") }
    var costoUnitario by remember { mutableStateOf("") }
    var observaciones by remember { mutableStateOf("") }

    // Estado local para errores de validación visual
    var localError by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Recepción de Mercancía")
                        if (selectedMaterial != null) {
                            Text(
                                text = selectedMaterial!!.nombre,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, "Regresar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Tarjeta del Formulario
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Registrar Entrada",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Campo Cantidad
                    OutlinedTextField(
                        value = cantidad,
                        onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) cantidad = it },
                        label = { Text("Cantidad a Ingresar") },
                        leadingIcon = { Icon(Icons.Filled.Info, null) },
                        suffix = {
                            Text(selectedMaterial?.unidadMedida ?: "")
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Campo Costo
                    OutlinedTextField(
                        value = costoUnitario,
                        onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) costoUnitario = it },
                        label = { Text("Costo Unitario (Aprox)") },
                        leadingIcon = { Icon(Icons.Filled.ShoppingCart, null) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Campo Observaciones
                    OutlinedTextField(
                        value = observaciones,
                        onValueChange = { observaciones = it },
                        label = { Text("Observaciones") },
                        leadingIcon = { Icon(Icons.Filled.Create, null) },
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            imeAction = ImeAction.Done
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Mensajes de Error
            if (localError != null || errorMessage != null) {
                Text(
                    text = localError ?: errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Botón de Acción
            Button(
                onClick = {
                    localError = null
                    materialsViewModel.registerEntry(
                        materialId = materialId,
                        cantidadStr = cantidad,
                        costoStr = costoUnitario,
                        observaciones = observaciones,
                        onSuccess = {
                            navController.popBackStack() // Volver a la lista tras éxito
                        },
                        onError = { msg -> localError = msg }
                    )
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(Icons.Filled.Check, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Registrar Entrada")
                }
            }
        }
    }
}