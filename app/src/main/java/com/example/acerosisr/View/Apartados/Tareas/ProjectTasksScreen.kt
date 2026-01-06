package com.example.acerosisr.View.Apartados.Tareas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.acerosisr.Model.Empleados
import com.example.acerosisr.Model.Tareas
import com.example.acerosisr.Navigation.Navigation
import com.example.acerosisr.ViewModel.TareasViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectTasksScreen(
    navController: Navigation,
    projectId: Int,
    projectClientName: String? = null,
    tareasViewModel: TareasViewModel
) {
    val allTasks by tareasViewModel.taskList.collectAsState()
    val isLoading by tareasViewModel.isLoading.collectAsState()
    val alertMessage by tareasViewModel.alertMessage.collectAsState()

    // Filtramos solo las tareas de este proyecto
    val tasksForProject = allTasks.filter { it.proyectoId.toInt() == projectId }
    val employees by tareasViewModel.employees.collectAsState()
    var showEditorDialog by remember { mutableStateOf(false) }
    var taskBeingEdited by remember { mutableStateOf<Tareas?>(null) }
    var showDeleteConfirm by remember { mutableStateOf<Tareas?>(null) }

    // Cargar tareas al entrar (por ahora usamos getAllTasks, luego podemos optimizar)
    // Al entrar al proyecto:
    // - limpiamos estado viejo (tarea seleccionada, alertas, errores)
    // - cargamos nuevamente las tareas
    LaunchedEffect(projectId) {
        tareasViewModel.clearSelectedTask()
        tareasViewModel.clearAlertMessage()
        tareasViewModel.clearError()
        tareasViewModel.loadAllTasks()
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = projectClientName
                            ?.let { "Tareas: $it" }
                            ?: "Tareas proyecto #$projectId"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        },
        floatingActionButton = {
            IconButton(
                onClick = {
                    taskBeingEdited = null
                    showEditorDialog = true
                },
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = MaterialTheme.shapes.small
                    )
                    .padding(8.dp)
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Nueva tarea",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (isLoading && tasksForProject.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Cargando tareas...")
                }
            } else if (tasksForProject.isEmpty()) {
                Text(
                    text = "Aún no hay tareas para este proyecto.",
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(tasksForProject) { tarea ->
                        TaskRow(
                            tarea = tarea,
                            onToggleCompleted = { newChecked ->
                                val newStatus = if (newChecked) "Completada" else "Pendiente"
                                // Lógica rápida tipo Google Tasks: marcar completada / pendiente
                                tareasViewModel.updateTaskStatus(tarea.tareaId, newStatus)
                            },
                            onEdit = {
                                taskBeingEdited = tarea
                                showEditorDialog = true
                            },
                            onDeleteClick = {
                                showDeleteConfirm = tarea
                            }
                        )
                    }
                }
            }

            if (alertMessage.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = alertMessage,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }

    // Diálogo crear / editar tarea
    if (showEditorDialog) {
        TaskEditorDialog(
            projectId = projectId,
            tareaInicial = taskBeingEdited,
            onDismiss = {
                showEditorDialog = false
                taskBeingEdited = null
            },
            onRequestEmployees = { tareasViewModel.loadEmployees() },
            empleados = employees,
            onSaveNew = { empleadoId, estado, fechaInicioMillis, fechaFinMillis, comentarios ->
                val nueva = Tareas(
                    tareaId = 0L, // backend lo asigna
                    proyectoId = projectId.toLong(),
                    empleadoId = empleadoId,
                    estado = estado,
                    fechaInicio = Date(fechaInicioMillis),
                    fechaFin = fechaFinMillis?.let { Date(it) },
                    comentarios = comentarios
                )
                tareasViewModel.createTask(nueva)
                showEditorDialog = false
                taskBeingEdited = null
            },
            onSaveEdit = { tareaId, empleadoId, estado, fechaInicioMillis, fechaFinMillis, comentarios ->
                val actualizada = Tareas(
                    tareaId = tareaId,
                    proyectoId = projectId.toLong(),
                    empleadoId = empleadoId,
                    estado = estado,
                    fechaInicio = Date(fechaInicioMillis),
                    fechaFin = fechaFinMillis?.let { Date(it) },
                    comentarios = comentarios
                )
                tareasViewModel.updateTask(actualizada)
                showEditorDialog = false
                taskBeingEdited = null
            }
        )
    }

    // Diálogo confirmar borrado
    showDeleteConfirm?.let { tarea ->
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = null },
            title = { Text("Eliminar tarea") },
            text = {
                Text("¿Seguro que quieres eliminar esta tarea?\n\n${tarea.comentarios ?: ""}")
            },
            confirmButton = {
                TextButton(onClick = {
                    tareasViewModel.deleteTask(tarea.tareaId)
                    showDeleteConfirm = null
                }) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

/**
 * Fila de tarea estilo Google Tasks:
 * - Checkbox de completada.
 * - Comentarios como texto principal.
 * - Empleado y fechas como subtítulos.
 * - Iconos para editar y borrar.
 */
@Composable
private fun TaskRow(
    tarea: Tareas,
    onToggleCompleted: (Boolean) -> Unit,
    onEdit: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val isCompleted = tarea.estado == "Completada"

    val dateFormatter = remember {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    }

    val fechaInicioText = tarea.fechaInicio?.let { dateFormatter.format(it) } ?: "—"
    val fechaFinText = tarea.fechaFin?.let { dateFormatter.format(it) } ?: "—"

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.Top
        ) {
            IconButton(onClick = { onToggleCompleted(!isCompleted) }) {
                Icon(
                    imageVector = if (isCompleted) Icons.Filled.CheckCircle else Icons.Rounded.Check,
                    contentDescription = "Estado tarea",
                    tint = if (isCompleted) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurface
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
                    .clickable { onEdit() } // tocar texto abre editor
            ) {
                Text(
                    text = tarea.comentarios ?: "(Sin descripción)",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (isCompleted) FontWeight.Normal else FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Empleado: ${tarea.empleadoId ?: "—"}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Estado: ${tarea.estado}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Inicio: $fechaInicioText  |  Fin: $fechaFinText",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Filled.Edit, contentDescription = "Editar")
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

/**
 * Dialog de creación/edición de tarea.
 * Aquí ponemos la lógica de:
 * - Empleado (nombre + búsqueda en popup, que cablearemos después).
 * - Estado (popup con ['Pendiente','En proceso','Completada']).
 * - Fechas con DatePicker (fechaInicio >= hoy, fechaFin >= fechaInicio).
 * - Comentarios.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskEditorDialog(
    projectId: Int,
    tareaInicial: Tareas?,
    empleados: List<Empleados>,
    onRequestEmployees: () -> Unit,
    onDismiss: () -> Unit,
    onSaveNew: (empleadoId: Long, estado: String, fechaInicioMillis: Long, fechaFinMillis: Long?, comentarios: String) -> Unit,
    onSaveEdit: (tareaId: Long, empleadoId: Long, estado: String, fechaInicioMillis: Long, fechaFinMillis: Long?, comentarios: String) -> Unit
) {
    val isEdit = tareaInicial != null

    var empleadoNombre by remember { mutableStateOf("") }
    var empleadoId by remember { mutableStateOf<Long?>(null) }

    var estado by remember { mutableStateOf(tareaInicial?.estado ?: "Pendiente") }

    var fechaInicioMillis by remember {
        mutableLongStateOf(tareaInicial?.fechaInicio?.time ?: todayStartInMillis())
    }
    var fechaFinMillis by remember {
        mutableLongStateOf(tareaInicial?.fechaFin?.time ?: (tareaInicial?.fechaInicio?.time ?: todayStartInMillis()))
    }
    var showEmployeePicker by remember { mutableStateOf(false) }
    var showFechaInicioPicker by remember { mutableStateOf(false) }
    var showFechaFinPicker by remember { mutableStateOf(false) }

    var comentarios by remember { mutableStateOf(tareaInicial?.comentarios ?: "") }

    var errorMsg by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(empleados, tareaInicial) {
        if (tareaInicial?.empleadoId != null && empleadoId == null) {
            // tareaInicial.empleadoId es el ID real de la tabla empleados (IdEmpleado)
            val emp = empleados.find { it.IdEmpleado == tareaInicial.empleadoId }
            if (emp != null) {
                empleadoId = emp.IdEmpleado
                empleadoNombre = emp.NombreEmpleado
            } else {
                // fallback si no aparece en la lista actual
                empleadoId = tareaInicial.empleadoId
                empleadoNombre = "Empleado #${tareaInicial.empleadoId}"
            }
        }
    }

    // TODO: cuando cableemos empleados de backend, podremos precargar empleadoNombre según empleadoId
    // Fallback: si no encontramos el empleado en la lista, al menos mostramos el ID
    if (isEdit && tareaInicial != null && empleadoNombre.isEmpty() && tareaInicial.empleadoId != null) {
        empleadoNombre = "Empleado #${tareaInicial.empleadoId}"
        // empleadoId sigue siendo el IdEmpleado (clave primaria en backend)
        empleadoId = tareaInicial.empleadoId
    }


    val dateFormatter = remember {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (isEdit) "Editar tarea" else "Nueva tarea (Proyecto #$projectId)")
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // EMPLEADO
                Text(text = "Empleado asignado", style = MaterialTheme.typography.labelMedium)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = empleadoNombre,
                        onValueChange = { empleadoNombre = it },
                        label = { Text("Nombre de empleado") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            onRequestEmployees()      // carga desde api/empleados
                            showEmployeePicker = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Face,
                            contentDescription = "Buscar empleado"
                        )
                    }

                }
                Spacer(modifier = Modifier.height(8.dp))

                // ESTADO
                EstadoPicker(
                    estadoActual = estado,
                    onEstadoSelected = { estado = it }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // FECHAS
                Text(text = "Fechas", style = MaterialTheme.typography.labelMedium)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Inicio",
                            style = MaterialTheme.typography.bodySmall
                        )
                        TextButton(onClick = { showFechaInicioPicker = true }) {
                            Text(dateFormatter.format(Date(fechaInicioMillis)))
                        }
                    }
                    Column {
                        Text(
                            text = "Fin",
                            style = MaterialTheme.typography.bodySmall
                        )
                        TextButton(onClick = { showFechaFinPicker = true }) {
                            Text(
                                fechaFinMillis.takeIf { it >= fechaInicioMillis }?.let {
                                    dateFormatter.format(Date(it))
                                } ?: "Sin fecha fin"
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // COMENTARIOS
                OutlinedTextField(
                    value = comentarios,
                    onValueChange = { comentarios = it },
                    label = { Text("Descripción / comentarios de la tarea") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    maxLines = 4
                )

                if (errorMsg != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMsg!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    errorMsg = null

                    // Validaciones:
                    // 1) empleadoId debe ser válido (no podemos crear tarea con usuario inexistente)
                    if (empleadoId == null) {
                        errorMsg = "Debes seleccionar un empleado válido."
                        return@TextButton
                    }

                    // 2) fechas
                    val hoy = todayStartInMillis()
                    if (fechaInicioMillis < hoy) {
                        errorMsg = "La fecha de inicio no puede ser anterior a hoy."
                        return@TextButton
                    }
                    if (fechaFinMillis < fechaInicioMillis) {
                        errorMsg = "La fecha de fin no puede ser menor que la fecha de inicio."
                        return@TextButton
                    }

                    if (comentarios.isBlank()) {
                        errorMsg = "La descripción de la tarea es obligatoria."
                        return@TextButton
                    }

                    if (isEdit && tareaInicial != null) {
                        onSaveEdit(
                            tareaInicial.tareaId,
                            empleadoId!!,
                            estado,
                            fechaInicioMillis,
                            fechaFinMillis,
                            comentarios.trim()
                        )
                    } else {
                        onSaveNew(
                            empleadoId!!,
                            estado,
                            fechaInicioMillis,
                            fechaFinMillis,
                            comentarios.trim()
                        )
                    }
                }
            ) {
                Text(if (isEdit) "Guardar cambios" else "Crear tarea")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )

    // DatePickers
    if (showFechaInicioPicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = fechaInicioMillis
        )
        DatePickerDialog(
            onDismissRequest = { showFechaInicioPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selected = datePickerState.selectedDateMillis
                        val hoy = todayStartInMillis()
                        if (selected != null && selected >= hoy) {
                            fechaInicioMillis = selected
                            if (fechaFinMillis < fechaInicioMillis) {
                                fechaFinMillis = fechaInicioMillis
                            }
                            showFechaInicioPicker = false
                        }
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showFechaInicioPicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showFechaFinPicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = fechaFinMillis
        )
        DatePickerDialog(
            onDismissRequest = { showFechaFinPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selected = datePickerState.selectedDateMillis
                        if (selected != null && selected >= fechaInicioMillis) {
                            fechaFinMillis = selected
                            showFechaFinPicker = false
                        }
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showFechaFinPicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
    if (showEmployeePicker) {
        EmployeePickerDialog(
            empleados = empleados,
            onSelect = { empleado ->
                // Usamos el IdEmpleado (clave primaria real en la tabla empleados)
                empleadoId = empleado.IdEmpleado
                empleadoNombre = empleado.NombreEmpleado
                showEmployeePicker = false
            },
            onDismiss = { showEmployeePicker = false }
        )
    }

}

@Composable
private fun EstadoPicker(
    estadoActual: String,
    onEstadoSelected: (String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    val opciones = listOf("Pendiente", "En proceso", "Completada")

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = "Estado", style = MaterialTheme.typography.labelMedium)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDialog = true }
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.small
                )
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Text(text = estadoActual)
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Selecciona el estado") },
            text = {
                Column {
                    opciones.forEach { opcion ->
                        Text(
                            text = opcion,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onEstadoSelected(opcion)
                                    showDialog = false
                                }
                                .padding(vertical = 8.dp)
                        )
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cerrar")
                }
            }
        )
    }
}
@Composable
private fun EmployeePickerDialog(
    empleados: List<Empleados>,
    onSelect: (Empleados) -> Unit,
    onDismiss: () -> Unit
) {
    var query by remember { mutableStateOf("") }

    val filtered = if (query.isBlank()) {
        empleados
    } else {
        empleados.filter {
            it.NombreEmpleado.contains(query, ignoreCase = true)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleccionar empleado") },
        text = {
            Column {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    label = { Text("Buscar por nombre") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (filtered.isEmpty()) {
                    Text("No se encontraron empleados.")
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 240.dp)
                    ) {
                        items(filtered) { emp ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSelect(emp) }
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(emp.NombreEmpleado)
                                    Text(
                                        text = "Num: ${emp.NumEmpleado}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                Text(
                                    text = emp.Cargo,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}

/**
 * Devuelve el inicio del día de hoy (00:00) en millis, para comparar fechas.
 */
private fun todayStartInMillis(): Long {
    val cal = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return cal.timeInMillis
}
