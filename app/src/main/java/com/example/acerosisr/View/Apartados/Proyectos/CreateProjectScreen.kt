package com.example.acerosisr.View.Apartados.Proyectos

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.acerosisr.Navigation.Navigation
import com.example.acerosisr.ViewModel.ProjectsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProjectScreen(
    navController: Navigation,
    projectsViewModel: ProjectsViewModel,
    currentUserId: Int? // empleado_id del usuario que inició sesión
) {
    val isLoading by projectsViewModel.isLoading.collectAsState()
    val globalError by projectsViewModel.errorMessage.collectAsState()
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var cliente by remember { mutableStateOf("") }

    // estado siempre "En progreso" al crear
    val estadoPorDefecto = "En progreso"

    var localError by remember { mutableStateOf<String?>(null) }
    var showDiscardDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    val hasChanges = descripcion.isNotBlank() || cliente.isNotBlank()

    LaunchedEffect(Unit) {
        // limpiar errores al entrar
        projectsViewModel.clearError()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo proyecto") },
                navigationIcon = {
                    TextButton(onClick = {
                        if (hasChanges) {
                            showDiscardDialog = true
                        } else {
                            navController.popBackStack()
                        }
                    }) {
                        Text("Cancelar")
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Titulo") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

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
                label = { Text("Descripción del proyecto") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 4
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Estado solo informativo, no editable aquí
            OutlinedTextField(
                value = estadoPorDefecto,
                onValueChange = { },
                readOnly = true,
                label = { Text("Estado inicial") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val descTrim = descripcion.trim()
                    val cliTrim = cliente.trim()
                    val nombreTrim = nombre.trim()

                    if (nombreTrim.isEmpty()) {
                        localError = "El campo nombre es obligatorio"
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

                    projectsViewModel.createProject(
                        titulo = nombreTrim,
                        descripcion = descTrim,
                        cliente = cliTrim,
                        registradoPor = currentUserId, // puede ser null si aún no manejas sesión
                        onSuccess = {
                            navController.popBackStack()
                        },
                        onError = { msg ->
                            localError = msg
                        }
                    )
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar proyecto")
            }

            if (isLoading) {
                Spacer(modifier = Modifier.height(16.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Guardando proyecto...",
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }

    // Diálogo de descartar cambios
    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            title = { Text("Descartar cambios") },
            text = { Text("Tienes cambios sin guardar. ¿Quieres salir sin guardar?") },
            confirmButton = {
                TextButton(onClick = {
                    showDiscardDialog = false
                    navController.popBackStack()
                }) {
                    Text("Salir")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Diálogo de error
    val errorToShow = localError ?: globalError
    if (errorToShow != null) {
        AlertDialog(
            onDismissRequest = {
                localError = null
                projectsViewModel.clearError()
            },
            title = { Text("Error") },
            text = { Text(errorToShow) },
            confirmButton = {
                TextButton(onClick = {
                    localError = null
                    projectsViewModel.clearError()
                }) {
                    Text("Aceptar")
                }
            }
        )
    }
}
