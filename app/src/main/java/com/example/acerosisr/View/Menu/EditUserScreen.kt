package com.example.acerosisr.View.Menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.acerosisr.Funtion.CustomAlertDialog
import com.example.acerosisr.Navigation.Navigation
import com.example.acerosisr.R
import com.example.acerosisr.ViewModel.UserViewModel
import com.example.acerosisr.ui.theme.PrimaryColor
import com.example.acerosisr.ui.theme.SecondaryColor
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditUserScreen(
    navController: Navigation,
    userViewModel: UserViewModel
) {
    val actualUser by userViewModel.actualUser.collectAsState()
    val userProfile by userViewModel.userProfile.collectAsState()
    val isLoading by userViewModel.isLoading.collectAsState()
    val alertMsg by userViewModel.alertResult.collectAsState()

    var isPasswordVisibleInView by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { userViewModel.loadActualUser() }
    LaunchedEffect(actualUser) {
        actualUser?.let { userViewModel.loadUserProfile(it.UserId) }
    }

    // Procesamiento de datos
    val nombre = userProfile?.NombreEmpleado ?: "Cargando..."
    val numEmpleado = userProfile?.NumEmpleado?.toString() ?: "..."
    val rawCargo = userProfile?.Cargo ?: ""
    val displayCargo = when (rawCargo.lowercase().trim()) {
        "dueño", "dueno" -> "Administrador"
        "admin" -> "Administrador"
        "trabajador" -> "Trabajador"
        else -> rawCargo.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
    }
    val correo = userProfile?.Correo ?: "Sin correo"

    // ✅ OBTENCIÓN ROBUSTA DEL PASSWORD
    val passwordReal = userProfile?.PasswordPlaintext.takeUnless { it.isNullOrBlank() }
        ?: userProfile?.Password_hash.takeUnless { it.isNullOrBlank() }
        ?: "No disponible"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Información", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, "Regresar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryColor)
            )
        },
        containerColor = PrimaryColor
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.align(Alignment.Center))
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Perfil
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(Color.White.copy(alpha = 0.1f), CircleShape)
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Person, null, tint = Color.White, modifier = Modifier.fillMaxSize())
                }
                Spacer(modifier = Modifier.height(24.dp))

                // Tarjeta de Información
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ReadOnlyField(icon = Icons.Filled.Badge, label = "Número de Empleado", value = numEmpleado)
                        ReadOnlyField(icon = Icons.Filled.Face, label = "Nombre", value = nombre)
                        ReadOnlyField(icon = Icons.Filled.Work, label = "Cargo", value = displayCargo)
                        HorizontalDivider(color = Color.LightGray)

                        // Correo
                        EditableFieldRow(
                            icon = Icons.Filled.Email,
                            label = "Correo Electrónico",
                            value = correo,
                            onEdit = { showEditDialog = true }
                        )

                        // Contraseña con Ojo
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Lock, null, tint = PrimaryColor)
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Contraseña", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                                Text(
                                    // ✅ LÓGICA VISUAL FINAL
                                    text = if (isPasswordVisibleInView) passwordReal else "••••••••",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (passwordReal == "No disponible" && isPasswordVisibleInView) Color.Red else PrimaryColor
                                )
                            }
                            IconButton(onClick = { isPasswordVisibleInView = !isPasswordVisibleInView }) {
                                Image(
                                    painter = painterResource(id = if (isPasswordVisibleInView) R.drawable.visibility else R.drawable.visibility_off),
                                    contentDescription = "Ver contraseña"
                                )
                            }
                            IconButton(onClick = { showEditDialog = true }) {
                                Icon(Icons.Filled.Edit, "Editar", tint = SecondaryColor)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showEditDialog && userProfile != null) {
        EditInfoDialog(
            currentEmail = userProfile?.Correo ?: "",
            isLoading = isLoading,
            onDismiss = { showEditDialog = false },
            onSave = { email, p1, p2 ->
                // Solo enviamos si cambió respecto al actual
                val finalEmail = if (email != userProfile?.Correo) email else ""
                userViewModel.updateMyProfile(
                    numEmpleado = userProfile!!.NumEmpleado,
                    email = finalEmail,
                    pass1 = p1,
                    pass2 = p2
                )
                showEditDialog = false
            }
        )
    }

    if (alertMsg.isNotBlank()) {
        CustomAlertDialog(
            showDialog = true,
            onDismiss = { userViewModel.clearAlertResult() },
            messages = alertMsg,
            color = if(alertMsg.contains("exitosamente") || alertMsg.contains("modificado")) SecondaryColor else MaterialTheme.colorScheme.error,
            icon = if(alertMsg.contains("exitosamente") || alertMsg.contains("modificado")) Icons.Filled.CheckCircle else Icons.Filled.Warning
        )
    }
}

@Composable
fun ReadOnlyField(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = PrimaryColor)
        Spacer(Modifier.width(12.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
            Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, color = PrimaryColor)
        }
    }
}

@Composable
fun EditableFieldRow(icon: ImageVector, label: String, value: String, onEdit: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = PrimaryColor)
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
            Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, color = PrimaryColor)
        }
        IconButton(onClick = onEdit) {
            Icon(Icons.Filled.Edit, "Editar", tint = SecondaryColor)
        }
    }
}

@Composable
fun EditInfoDialog(
    currentEmail: String,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var email by remember { mutableStateOf(currentEmail) }
    var pass1 by remember { mutableStateOf("") }
    var pass2 by remember { mutableStateOf("") }

    var isPass1Visible by remember { mutableStateOf(false) }
    var isPass2Visible by remember { mutableStateOf(false) }

    // Colores para fondo oscuro
    val darkBgTextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        focusedBorderColor = Color.White,
        unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
        focusedLabelColor = Color.White,
        unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
        cursorColor = Color.White,
        focusedTrailingIconColor = Color.White,
        unfocusedTrailingIconColor = Color.White.copy(alpha = 0.7f)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PrimaryColor, // ✅ Fondo Oscuro
        title = { Text("Actualizar Información", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Deja la contraseña vacía si no deseas cambiarla.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f)
                )

                // Correo
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(),
                    colors = darkBgTextFieldColors
                )

                HorizontalDivider(color = Color.White.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 8.dp))

                // Contraseña (Opcional)
                OutlinedTextField(
                    value = pass1,
                    onValueChange = { pass1 = it },
                    label = { Text("Nueva Contraseña") },
                    singleLine = true,
                    colors = darkBgTextFieldColors,
                    visualTransformation = if (isPass1Visible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { isPass1Visible = !isPass1Visible }) {
                            Image(
                                painter = painterResource(id = if (isPass1Visible) R.drawable.visibility else R.drawable.visibility_off),
                                contentDescription = "Toggle"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                if (pass1.isNotEmpty()) {
                    OutlinedTextField(
                        value = pass2,
                        onValueChange = { pass2 = it },
                        label = { Text("Confirmar Contraseña") },
                        singleLine = true,
                        colors = darkBgTextFieldColors,
                        visualTransformation = if (isPass2Visible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { isPass2Visible = !isPass2Visible }) {
                                Image(
                                    painter = painterResource(id = if (isPass2Visible) R.drawable.visibility else R.drawable.visibility_off),
                                    contentDescription = "Toggle"
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        "Min 6 caracteres, Mayus, Minus y Num.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(email, pass1, pass2) },
                colors = ButtonDefaults.buttonColors(containerColor = SecondaryColor)
            ) {
                Text("Guardar Cambios", color = Color.White)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
            ) {
                Text("Cancelar")
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}