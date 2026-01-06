package com.example.acerosisr.View.Apartados.Proyectos

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.acerosisr.Navigation.AppNavHost
import com.example.acerosisr.Navigation.AppScreen
import com.example.acerosisr.Navigation.Navigation
import com.example.acerosisr.ViewModel.ProjectsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDeleteProjectScreen(
    navController: Navigation,
    projectId: Int?,
    projectsViewModel: ProjectsViewModel
) {
    val project by projectsViewModel.selectedProject.collectAsState()
    val isLoading by projectsViewModel.isLoading.collectAsState()
    val globalError by projectsViewModel.errorMessage.collectAsState()


    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var cliente by remember { mutableStateOf("") }

    // Dropdown para estado
    val estados = listOf("En progreso", "Finalizado", "Cancelado")
    var estadoExpanded by remember { mutableStateOf(false) }
    var estado by remember { mutableStateOf(estados.first()) }

    var localError by remember { mutableStateOf<String?>(null) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    // Cargar detalle al entrar
    LaunchedEffect(projectId) {
        if (projectId != null) {
            projectsViewModel.loadProjectDetail(projectId)
        }
    }

    // Cuando llegue el proyecto del backend, inicializamos campos
    LaunchedEffect(project) {
        project?.let { p ->
            titulo = p.titulo
            descripcion = p.descripcion
            cliente = p.cliente
            estado = if (p.estado in estados) p.estado else "En progreso"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Editar proyecto")
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    if (project != null && project?.id != null) {
                        IconButton(onClick = { showDeleteConfirm = true }) {
                            Icon(
                                Icons.Filled.Delete,
                                contentDescription = "Eliminar proyecto",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
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
            when {
                projectId == null -> {
                    Text(
                        text = "Proyecto no válido.",
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                isLoading && project == null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Cargando proyecto...")
                    }
                }

                project == null && globalError != null -> {
                    Text(
                        text = globalError ?: "Error al cargar proyecto",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                project != null -> {
                    // CAMPOS EDITABLES

                    OutlinedTextField(
                        value = titulo,
                        onValueChange = { titulo = it },
                        label = { Text("Titulo") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = cliente,
                        onValueChange = { cliente = it },
                        label = { Text("Cliente") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = descripcion,
                        onValueChange = { descripcion = it },
                        label = { Text("Descripción") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        maxLines = 4
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // ESTADO (dropdown)
                    ExposedDropdownMenuBox(
                        expanded = estadoExpanded,
                        onExpandedChange = { estadoExpanded = !estadoExpanded }
                    ) {
                        OutlinedTextField(
                            value = estado,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Estado") },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = estadoExpanded)
                            },
                            colors = ExposedDropdownMenuDefaults.textFieldColors()
                        )

                        ExposedDropdownMenu(
                            expanded = estadoExpanded,
                            onDismissRequest = { estadoExpanded = false }
                        ) {
                            estados.forEach { opcion ->
                                DropdownMenuItem(
                                    text = { Text(opcion) },
                                    onClick = {
                                        estado = opcion
                                        estadoExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Mensajes de error
                    val errorToShow = localError ?: globalError
                    if (errorToShow != null) {
                        Text(
                            text = errorToShow,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // BOTÓN GUARDAR CAMBIOS
                    Button(
                        onClick = {
                            localError = null
                            val tituloTrim = titulo.trim()
                            val descTrim = descripcion.trim()
                            val cliTrim = cliente.trim()

                            if (tituloTrim.isEmpty()) {
                                localError = "El campo título es obligatorio"
                                return@Button
                            }

                            if (cliTrim.isEmpty()) {
                                localError = "El campo cliente es obligatorio"
                                return@Button
                            }
                            if (descTrim.isEmpty()) {
                                localError = "La descripción del proyecto es obligatoria"
                                return@Button
                            }

                            val id = project?.id
                            if (id == null) {
                                localError = "Id de proyecto inválido"
                                return@Button
                            }

                            projectsViewModel.updateProject(
                                id = id,
                                titulo = tituloTrim,
                                descripcion = descTrim,
                                cliente = cliTrim,
                                estado = estado,
                                registradoPor = project?.registradoPor,
                                onSuccess = {
                                    navController.popBackStack()
                                },
                                onError = { msg ->
                                    localError = msg
                                }
                            )
                        },
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(Icons.Filled.Done, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Guardar cambios")
                    }

                    if (isLoading) {
                        Spacer(modifier = Modifier.height(12.dp))
                        RowCenteredLoading()
                    }
                }
            }
        }
    }

    // Diálogo de confirmación de borrado
    if (showDeleteConfirm && project?.id != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Eliminar proyecto") },
            text = { Text("¿Seguro que quieres eliminar este proyecto? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteConfirm = false
                    projectsViewModel.deleteProject(
                        id = project!!.id!!,
                        onSuccess = {
                            navController.popBackStack()
                            // 2: cerrar la pantalla de detalle (la del proyecto que ya no existe)
                            navController.popBackStack()
                        },
                        onError = { msg ->
                            localError = msg
                        }
                    )
                }) {
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

@Composable
private fun RowCenteredLoading() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(8.dp))
        Text("Procesando...")
    }
}
