package com.example.acerosisr.View.Apartados.Materiales

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.acerosisr.Navigation.AppScreen
import com.example.acerosisr.Navigation.Navigation // Added import
import com.example.acerosisr.ui.theme.AcerosISRTheme
import com.example.acerosisr.ui.theme.PrimaryColor // Added import for theme colors
import com.example.acerosisr.ui.theme.TextColorWhite // Added import for theme colors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaterialMovementScreen(navController: Navigation) { // Changed NavHostController to Navigation
    var materialName by remember { mutableStateOf("") }
    var materialType by remember { mutableStateOf("") }
    var unitOfMeasure by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var costUnit by remember { mutableStateOf("") }
    var movementType by remember { mutableStateOf("entrada") }
    var observations by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar Material o Movimiento") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) { // Used popBackStack from Navigation interface
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
            // Input para nuevo material (si aplica, o selección de existente)
            OutlinedTextField(
                value = materialName,
                onValueChange = { materialName = it },
                label = { Text("Nombre del Material") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = materialType,
                onValueChange = { materialType = it },
                label = { Text("Tipo (tubo, soldadura, etc.)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = unitOfMeasure,
                onValueChange = { unitOfMeasure = it },
                label = { Text("Unidad de Medida (pieza, kg, etc.)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción (opcional)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Selector de tipo de movimiento
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                FilterChip(
                    selected = movementType == "entrada",
                    onClick = { movementType = "entrada" },
                    label = { Text("Entrada") }
                )
                FilterChip(
                    selected = movementType == "salida",
                    onClick = { movementType = "salida" },
                    label = { Text("Salida") }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = quantity,
                onValueChange = { quantity = it },
                label = { Text("Cantidad") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (movementType == "entrada") {
                OutlinedTextField(
                    value = costUnit,
                    onValueChange = { costUnit = it },
                    label = { Text("Costo Unitario") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            OutlinedTextField(
                value = observations,
                onValueChange = { observations = it },
                label = { Text("Observaciones (opcional)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { /* TODO: Implement save logic */ }, 
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryColor,
                    contentColor = TextColorWhite,
                    disabledContainerColor = PrimaryColor.copy(alpha = 0.5f),
                    disabledContentColor = TextColorWhite.copy(alpha = 0.5f)
                )
            ) {
                Text("Guardar Movimiento")
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun MaterialMovementScreenPreview() {
//    AcerosISRTheme {
//        // Need a mock Navigation instance for preview
//        MaterialMovementScreen(object : Navigation { // Mock Navigation
//            override fun navigateTo(route: AppScreen.MaterialMovement) {
//                // Do nothing for preview
//            }
//            override fun popBackStack() {
//                // Do nothing for preview
//            }
//        })
//    }
//}
