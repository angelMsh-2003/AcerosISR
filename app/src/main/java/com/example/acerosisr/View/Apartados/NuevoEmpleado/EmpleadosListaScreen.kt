package com.example.acerosisr.View.Apartados.NuevoEmpleado

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.acerosisr.Funtion.CustomAlertDialog
import com.example.acerosisr.Model.Empleados
import com.example.acerosisr.Navigation.Navigation
import com.example.acerosisr.ViewModel.UserViewModel
import com.example.acerosisr.ui.theme.PrimaryColor

// MAPEO DE CARGOS (Backend -> UI Label)
fun mapCargoToUI(backendCargo: String): String {
    return when (backendCargo.lowercase()) {
        "dueño" -> "Administrativo"
        "admin" -> "Admin"
        "trabajador" -> "Trabajador"
        else -> backendCargo.replaceFirstChar { it.uppercase() }
    }
}

// MAPEO INVERSO (UI Label -> Backend)
fun mapUIToBackendCargo(uiLabel: String): String {
    return when (uiLabel) {
        "Administrativo" -> "dueño"
        "Admin" -> "admin"
        "Trabajador" -> "trabajador"
        else -> "trabajador" // Fallback seguro
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmpleadosListaScreen(
    navController: Navigation,
    userViewModel: UserViewModel
) {
    val employees by userViewModel.employeesList.collectAsState()
    val isLoading by userViewModel.isLoading.collectAsState()
    val alertMessage by userViewModel.alertResult.collectAsState()

    // Estados para Popup de Edición
    var showEditDialog by remember { mutableStateOf(false) }
    var selectedEmployee by remember { mutableStateOf<Empleados?>(null) }

    // Variables temporales para editar
    var tempNumEmpleado by remember { mutableStateOf("") }
    var tempEstado by remember { mutableStateOf(1L) } // 1: Disponible, 0: No disponible
    var tempCargoUI by remember { mutableStateOf("Trabajador") } // Valor UI por defecto

    var isEstadoExpanded by remember { mutableStateOf(false) }
    var isCargoExpanded by remember { mutableStateOf(false) }

    var showSuccessDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        userViewModel.loadAllEmployees()
    }

    LaunchedEffect(alertMessage) {
        if (alertMessage == "UserModified") {
            showEditDialog = false
            showSuccessDialog = true
            userViewModel.clearAlertResult()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lista de Empleados") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Regresar")
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
                    items(employees) { employee ->
                        EmployeeItem(employee = employee) {
                            selectedEmployee = employee
                            tempNumEmpleado = employee.NumEmpleado.toString()
                            tempEstado = employee.Estado
                            // Convertimos el cargo backend al cargo UI al abrir el popup
                            tempCargoUI = mapCargoToUI(employee.Cargo)
                            showEditDialog = true
                        }
                    }
                }
            }
        }
    }

    // --- VENTANA EMERGENTE DE EDICIÓN ---
    if (showEditDialog && selectedEmployee != null) {
        val emp = selectedEmployee!!
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            icon = { Icon(Icons.Filled.Edit, null, tint = PrimaryColor) },
            title = { Text("Editar Empleado", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    InfoLabelValue("ID Base:", "#${emp.IdEmpleado}")
                    InfoLabelValue("No. empleado: ", emp.NumEmpleado.toString())
                    InfoLabelValue("Nombre:", emp.NombreEmpleado)
                    InfoLabelValue("Correo:", emp.Correo ?: "Sin correo")


                    Divider()

                    // 1. Campo Número
//                    OutlinedTextField(
//                        value = tempNumEmpleado,
//                        onValueChange = { if (it.length <= 7 && it.all { c -> c.isDigit() }) tempNumEmpleado = it },
//                        label = { Text("Número de Empleado") },
//                        leadingIcon = { Icon(Icons.Filled.Person, null) },
//                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//                        singleLine = true,
//                        modifier = Modifier.fillMaxWidth()
//                    )

                    // 2. Campo Cargo (Dropdown)
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = tempCargoUI,
                            onValueChange = {},
                            label = { Text("Cargo") },
                            readOnly = true,
                            trailingIcon = { Icon(Icons.Filled.ArrowDropDown, null, Modifier.clickable { isCargoExpanded = !isCargoExpanded }) },
                            leadingIcon = { Icon(Icons.Filled.Build, null) },
                            modifier = Modifier.fillMaxWidth().clickable { isCargoExpanded = true },
                            enabled = false,
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledBorderColor = MaterialTheme.colorScheme.outline,
                                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        DropdownMenu(
                            expanded = isCargoExpanded,
                            onDismissRequest = { isCargoExpanded = false },
                            modifier = Modifier.fillMaxWidth(0.7f)
                        ) {
                            listOf("Administrativo", "Admin", "Trabajador").forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        tempCargoUI = option
                                        isCargoExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // 3. Campo Estado (Dropdown)
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = if (tempEstado == 1L) "Disponible" else "No disponible",
                            onValueChange = {},
                            label = { Text("Estado") },
                            readOnly = true,
                            trailingIcon = { Icon(Icons.Filled.ArrowDropDown, null, Modifier.clickable { isEstadoExpanded = !isEstadoExpanded }) },
                            leadingIcon = { Icon(if (tempEstado == 1L) Icons.Filled.CheckCircle else Icons.Filled.Warning, null) },
                            modifier = Modifier.fillMaxWidth().clickable { isEstadoExpanded = true },
                            enabled = false,
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledBorderColor = MaterialTheme.colorScheme.outline,
                                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledLeadingIconColor = if (tempEstado == 1L) Color(0xFF2E7D32) else Color(0xFFC62828),
                                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        DropdownMenu(
                            expanded = isEstadoExpanded,
                            onDismissRequest = { isEstadoExpanded = false },
                            modifier = Modifier.fillMaxWidth(0.7f)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Disponible", color = Color(0xFF2E7D32)) },
                                onClick = { tempEstado = 1L; isEstadoExpanded = false }
                            )
                            DropdownMenuItem(
                                text = { Text("No disponible", color = Color(0xFFC62828)) },
                                onClick = { tempEstado = 0L; isEstadoExpanded = false }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        emp.IdEmpleado?.let { dbId ->
                            // Convertimos el valor UI al valor Backend antes de enviar
                            val backendCargo = mapUIToBackendCargo(tempCargoUI)

                            userViewModel.adminUpdateEmployee(
                                dbId = dbId,
                                newNumEmpleado = tempNumEmpleado,
                                newEstado = tempEstado,
                                newCargo = backendCargo // Enviamos "dueño", "admin" o "trabajador"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Guardar Cambios")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showEditDialog = false }, modifier = Modifier.fillMaxWidth()) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (showSuccessDialog) {
        CustomAlertDialog(
            showDialog = true,
            onDismiss = { showSuccessDialog = false },
            messages = "Se modificó este usuario correctamente",
            color = PrimaryColor,
            icon = Icons.Filled.CheckCircle
        )
    }
}

@Composable
fun EmployeeItem(employee: Empleados, onClick: () -> Unit) {
    val isAvailable = employee.Estado == 1L
    val statusText = if (isAvailable) "Disponible" else "No disponible"
    val statusColor = if (isAvailable) Color(0xFF2E7D32) else Color(0xFFC62828)
    val bgColor = if (isAvailable) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)

    // Mapeo para mostrar en la lista
    val cargoUI = mapCargoToUI(employee.Cargo)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = employee.NombreEmpleado,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "No. ${employee.NumEmpleado}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                // Mostrar el Cargo
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Person, null, modifier = Modifier.size(16.dp), tint = PrimaryColor)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = cargoUI,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = PrimaryColor
                    )
                }
            }

            Surface(
                color = bgColor,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, statusColor.copy(alpha = 0.5f))
            ) {
                Text(
                    text = statusText,
                    color = statusColor,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun InfoLabelValue(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontWeight = FontWeight.Bold, modifier = Modifier.width(80.dp), style = MaterialTheme.typography.bodyMedium)
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}