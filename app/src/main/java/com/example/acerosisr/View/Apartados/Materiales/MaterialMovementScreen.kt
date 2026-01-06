package com.example.acerosisr.View.Apartados.Materiales

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.acerosisr.Navigation.Navigation
import com.example.acerosisr.ViewModel.MaterialsViewModel
import com.example.acerosisr.ui.theme.PrimaryColor
import com.example.acerosisr.ui.theme.TextColorWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaterialMovementScreen(
    navController: Navigation,
    materialsViewModel: MaterialsViewModel
) {
    // Campos del formulario
    var materialName by remember { mutableStateOf("") }

    // TIPO (dropdown)
    val tipos = listOf("tubo", "soldadura", "lamina", "herramienta", "otro")
    var tipoExpanded by remember { mutableStateOf(false) }
    var selectedTipo by remember { mutableStateOf(tipos.first()) }

    // UNIDAD DE MEDIDA (dropdown)
    data class UnidadMedidaOption(val value: String, val label: String)

    val unidades = listOf(
        UnidadMedidaOption("pieza", "Pieza"),
        UnidadMedidaOption("kg", "Kilogramos (kg)"),
        UnidadMedidaOption("ton", "Toneladas (ton)"),
        UnidadMedidaOption("m", "Metros (m)"),
        UnidadMedidaOption("lt", "Litros (lt)")
    )
    var umExpanded by remember { mutableStateOf(false) }
    var selectedUM by remember { mutableStateOf(unidades.first()) }

    var stock by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // Mensajes de UI
    var localError by remember { mutableStateOf<String?>(null) }
    var localSuccess by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar nuevo material") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Atrás")
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
            // TIPO (dropdown)
            ExposedDropdownMenuBox(
                expanded = tipoExpanded,
                onExpandedChange = { tipoExpanded = !tipoExpanded }
            ) {
                OutlinedTextField(
                    value = selectedTipo,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tipo de material") },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = tipoExpanded,
                    onDismissRequest = { tipoExpanded = false }
                ) {
                    tipos.forEach { tipo ->
                        DropdownMenuItem(
                            text = { Text(tipo) },
                            onClick = {
                                selectedTipo = tipo
                                tipoExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // NOMBRE
            OutlinedTextField(
                value = materialName,
                onValueChange = { materialName = it },
                label = { Text("Nombre del Material") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // UNIDAD DE MEDIDA (dropdown)
            ExposedDropdownMenuBox(
                expanded = umExpanded,
                onExpandedChange = { umExpanded = !umExpanded }
            ) {
                OutlinedTextField(
                    value = selectedUM.label,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Unidad de medida") },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = umExpanded,
                    onDismissRequest = { umExpanded = false }
                ) {
                    unidades.forEach { um ->
                        DropdownMenuItem(
                            text = { Text(um.label) },
                            onClick = {
                                selectedUM = um
                                umExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // STOCK ACTUAL
            OutlinedTextField(
                value = stock,
                onValueChange = { stock = it },
                label = { Text("Stock inicial") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // DESCRIPCIÓN
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción (opcional)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Mensajes
            localError?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
            }
            localSuccess?.let {
                Text(it, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = {
                    localError = null
                    localSuccess = null

                    materialsViewModel.createMaterialFromForm(
                        tipo = selectedTipo,
                        nombre = materialName,
                        unidadMedida = selectedUM.value,
                        stock = stock,
                        descripcion = description,
                        onSuccess = {
                            localSuccess = "Material registrado correctamente"
                            // Si quieres regresar directo:
                            navController.popBackStack()
                        },
                        onError = { msg ->
                            localError = msg
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryColor,
                    contentColor = TextColorWhite,
                    disabledContainerColor = PrimaryColor.copy(alpha = 0.5f),
                    disabledContentColor = TextColorWhite.copy(alpha = 0.5f)
                )
            ) {
                Text("Guardar material")
            }
        }
    }
}
