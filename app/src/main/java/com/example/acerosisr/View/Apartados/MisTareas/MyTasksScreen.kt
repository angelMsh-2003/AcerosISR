package com.example.acerosisr.View.Apartados.Tareas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.acerosisr.Funtion.CustomAlertDialog
import com.example.acerosisr.Model.Material
import com.example.acerosisr.Model.UsuarioTareaEspecifica
import com.example.acerosisr.Navigation.Navigation
import com.example.acerosisr.ViewModel.MaterialUsageItem
import com.example.acerosisr.ViewModel.TareasViewModel
import com.example.acerosisr.ViewModel.UserViewModel
import com.example.acerosisr.ui.theme.PrimaryColor
import com.example.acerosisr.ui.theme.SecondaryColor
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTasksScreen(
    navController: Navigation,
    tareasViewModel: TareasViewModel,
    userViewModel: UserViewModel
) {
    val myTasks by tareasViewModel.myTasks.collectAsState()
    val isLoading by tareasViewModel.isLoading.collectAsState()
    val alertMsg by tareasViewModel.alertMessage.collectAsState()
    val actualUser by userViewModel.actualUser.collectAsState()
    var currentDbId by remember { mutableStateOf<Long?>(null) }
    val availableMaterials by tareasViewModel.availableMaterials.collectAsState()

    // Estados de la UI
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Pendiente", "En proceso", "Completada")

    // Estado para el Popup de Detalle/Edición
    var showDetailDialog by remember { mutableStateOf(false) }
    var showMaterialReportDialog by remember { mutableStateOf(false) } // Nuevo Dialogo
    var selectedTask by remember { mutableStateOf<UsuarioTareaEspecifica?>(null) }



    LaunchedEffect(Unit) {
        userViewModel.loadActualUser()
    }
    // Cargar materiales en segundo plano por si se necesitan
    LaunchedEffect(Unit) {
        tareasViewModel.loadMaterialsForReport()
    }

    // 2. Flujo: Usuario Cargado -> Obtener ID Real -> Cargar Tareas
    LaunchedEffect(actualUser, selectedTab) {
        actualUser?.let { user ->
            println("Usuario actual cargado: ${user.UserId}") // NumEmpleado

            // Petición Intermedia: Obtener ID de Base de Datos
            val dbId = userViewModel.fetchDbIdForUser(user.UserId)

            if (dbId != null) {
                println("ID real de base de datos obtenido: $dbId")
                currentDbId = dbId
                // Cargar tareas con el ID correcto
                tareasViewModel.loadMyTasks(dbId)
            } else {
                println("Error: No se pudo obtener el ID de base de datos")
            }
        }
    }

    // Filtrado local según la pestaña activa
    val filteredTasks = myTasks.filter { task ->
        // Normalizamos strings para evitar errores por mayúsculas/minúsculas
        task.estado.equals(tabs[selectedTab], ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Tareas") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, "Regresar")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {

            // BARRA DE NAVEGACIÓN (TABS)
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = PrimaryColor
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                    )
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                // LISTA DE TAREAS
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (filteredTasks.isEmpty()) {
                        item {
                            Text(
                                text = "No hay tareas en estado '${tabs[selectedTab]}'",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                    items(filteredTasks) { task ->
                        MyTaskItem(task) {
                            selectedTask = task
                            showDetailDialog = true
                        }
                    }
                }
            }
        }
    }

    // VENTANA EMERGENTE (DETALLE Y CAMBIO DE ESTADO)
    if (showDetailDialog && selectedTask != null) {
        TaskDetailDialog(
            task = selectedTask!!,
            onDismiss = { showDetailDialog = false },
            onSaveStatus = { newStatus ->
                showDetailDialog = false // Cerramos el detalle primero

                if (selectedTask!!.estado == "En proceso" && newStatus == "Completada") {
                    // INTERCEPCIÓN: Abrir reporte de materiales
                    showMaterialReportDialog = true
                } else {
                    // Cambio normal (ej. Pendiente -> En proceso)
                    currentDbId?.let { dbId ->
                        tareasViewModel.updateMyTaskStatus(selectedTask!!.tareaId, newStatus, dbId)
                    }
                }
            }
        )
    }

    // --- DIALOGO 2: REPORTE DE MATERIALES (COMPLETAR TAREA) ---
    if (showMaterialReportDialog && selectedTask != null) {
        MaterialConsumptionDialog(
            materialsList = availableMaterials,
            onDismiss = { showMaterialReportDialog = false },
            onConfirm = { usedMaterials ->
                currentDbId?.let { dbId ->
                    tareasViewModel.completeTaskWithMaterials(
                        tareaId = selectedTask!!.tareaId,
                        usedMaterials = usedMaterials,
                        currentEmployeeId = dbId
                    )
                }
                showMaterialReportDialog = false
            }
        )
    }


    // Alerta de confirmación simple
    if (alertMsg.isNotBlank()) {
        CustomAlertDialog(
            showDialog = true,
            onDismiss = { tareasViewModel.clearAlertMessage() },
            messages = alertMsg,
            color = PrimaryColor,
            icon = androidx.compose.material.icons.Icons.Filled.Flag // Icono genérico
        )
    }
}

@Composable
fun MyTaskItem(
    task: UsuarioTareaEspecifica,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.proyectoTitulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = task.comentarios ?: "Sin descripción",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            // Indicador visual simple
            Surface(
                color = when(task.estado) {
                    "Pendiente" -> Color(0xFFFFF3E0) // Naranja claro
                    "En proceso" -> Color(0xFFE3F2FD) // Azul claro
                    "Completada" -> Color(0xFFE8F5E9) // Verde claro
                    else -> Color.LightGray
                },
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = task.estado,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Black
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailDialog(
    task: UsuarioTareaEspecifica,
    onDismiss: () -> Unit,
    onSaveStatus: (String) -> Unit
) {
    // Lógica de estados permitidos
    val currentStatus = task.estado
    val availableOptions = remember(currentStatus) {
        when (currentStatus) {
            "Pendiente" -> listOf("En proceso") // Solo puede avanzar a En proceso
            "En proceso" -> listOf("Completada") // Solo puede avanzar a Completada
            "Completada" -> emptyList() // No puede moverse
            else -> emptyList()
        }
    }

    var selectedOption by remember { mutableStateOf(currentStatus) } // Inicialmente muestra el actual
    var isExpanded by remember { mutableStateOf(false) }
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Detalle de Tarea") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

                // Proyecto
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Work, null, tint = SecondaryColor)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(text = task.proyectoTitulo, fontWeight = FontWeight.Bold)
                        Text(text = task.proyectoDescripcion, style = MaterialTheme.typography.bodySmall)
                    }
                }
                Divider()

                // Fechas
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.CalendarToday, null, tint = SecondaryColor)
                    Spacer(modifier = Modifier.width(8.dp))
                    val fInicio = task.fechaInicio?.let { dateFormat.format(it) } ?: "N/A"
                    val fFin = task.fechaFin?.let { dateFormat.format(it) } ?: "N/A"
                    Text("Del $fInicio al $fFin", style = MaterialTheme.typography.bodyMedium)
                }

                // Comentarios / Especificaciones
                Row(verticalAlignment = Alignment.Top) {
                    Icon(Icons.Filled.Description, null, tint = SecondaryColor)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = task.comentarios ?: "Sin especificaciones", style = MaterialTheme.typography.bodyMedium)
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text("Estado Actual:", fontWeight = FontWeight.Bold)

                // MENU DESPLEGABLE DE ESTADO
                ExposedDropdownMenuBox(
                    expanded = isExpanded,
                    onExpandedChange = {
                        // Solo expandir si hay opciones válidas para cambiar
                        if (availableOptions.isNotEmpty()) isExpanded = !isExpanded
                    }
                ) {
                    OutlinedTextField(
                        value = selectedOption,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            if (availableOptions.isNotEmpty()) {
                                Icon(Icons.Filled.ArrowDropDown, null)
                            }
                        },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline
                        ),
                        enabled = availableOptions.isNotEmpty() // Deshabilitado si es Completada
                    )

                    ExposedDropdownMenu(
                        expanded = isExpanded,
                        onDismissRequest = { isExpanded = false }
                    ) {
                        // Mostramos las opciones permitidas
                        availableOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    selectedOption = option
                                    isExpanded = false
                                }
                            )
                        }
                    }
                }

                if (availableOptions.isEmpty() && currentStatus != "Completada") {
                    Text("No hay cambios de estado permitidos desde aquí.", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                }
            }
        },
        confirmButton = {
            // Botón Guardar solo si cambió el estado
            if (selectedOption != currentStatus) {
                Button(onClick = { onSaveStatus(selectedOption) }) {
                    Text("Guardar")
                }
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaterialConsumptionDialog(
    materialsList: List<Material>,
    onDismiss: () -> Unit,
    onConfirm: (List<MaterialUsageItem>) -> Unit
) {
    // Estado local del formulario
    var selectedMaterial by remember { mutableStateOf<Material?>(null) }
    var quantityText by remember { mutableStateOf("") }
    var isExpanded by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    // Lista de materiales agregados en memoria
    val addedMaterials = remember { mutableStateListOf<MaterialUsageItem>() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Reporte de Materiales") },
        text = {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Selecciona los materiales usados para completar la tarea:", style = MaterialTheme.typography.bodySmall)

                // 1. Dropdown Selección Material
                ExposedDropdownMenuBox(
                    expanded = isExpanded,
                    onExpandedChange = { isExpanded = !isExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedMaterial?.nombre ?: "Seleccionar material",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(),
                        label = { Text("Material") }
                    )
                    ExposedDropdownMenu(
                        expanded = isExpanded,
                        onDismissRequest = { isExpanded = false }
                    ) {
                        materialsList.forEach { material ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(material.nombre, fontWeight = FontWeight.Bold)
                                        Text("Stock: ${material.stockActual} ${material.unidadMedida}", style = MaterialTheme.typography.bodySmall)
                                    }
                                },
                                onClick = {
                                    selectedMaterial = material
                                    isExpanded = false
                                }
                            )
                        }
                    }
                }

                // 2. Cantidad
                if (selectedMaterial != null) {
                    OutlinedTextField(
                        value = quantityText,
                        onValueChange = { if(it.all { char -> char.isDigit() || char == '.' }) quantityText = it },
                        label = { Text("Cantidad (${selectedMaterial?.unidadMedida})") },
                        placeholder = { Text("Max: ${selectedMaterial?.stockActual}") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        isError = errorMsg != null
                    )
                    if (errorMsg != null) {
                        Text(errorMsg!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
                    }

                    // Botón Agregar a la lista
                    Button(
                        onClick = {
                            val qty = quantityText.toDoubleOrNull()
                            val stock = selectedMaterial?.stockActual ?: 0.0

                            if (qty == null || qty <= 0) {
                                errorMsg = "Ingresa una cantidad válida"
                            } else if (qty > stock) {
                                errorMsg = "No hay suficiente stock (Disp: $stock)"
                            } else {
                                // Agregar
                                addedMaterials.add(MaterialUsageItem(selectedMaterial!!, qty))
                                // Reset inputs
                                selectedMaterial = null
                                quantityText = ""
                                errorMsg = null
                            }
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Icon(Icons.Filled.Add, null)
                        Spacer(Modifier.width(4.dp))
                        Text("Agregar")
                    }
                }

                Divider()

                // 3. Lista de Agregados
                if (addedMaterials.isNotEmpty()) {
                    Text("Materiales a reportar:", fontWeight = FontWeight.Bold)
                    LazyColumn(modifier = Modifier.heightIn(max = 150.dp)) {
                        items(addedMaterials) { item ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("- ${item.material.nombre}: ${item.cantidadUsada} ${item.material.unidadMedida}", style = MaterialTheme.typography.bodyMedium)
                                IconButton(onClick = { addedMaterials.remove(item) }, modifier = Modifier.size(24.dp)) {
                                    Icon(Icons.Filled.Delete, null, tint = Color.Red)
                                }
                            }
                        }
                    }
                } else {
                    Text("Ningún material agregado (Se puede guardar sin materiales si no se usaron)", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(addedMaterials.toList()) },
                colors = ButtonDefaults.buttonColors(containerColor = SecondaryColor)
            ) {
                Text("Finalizar Tarea")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}