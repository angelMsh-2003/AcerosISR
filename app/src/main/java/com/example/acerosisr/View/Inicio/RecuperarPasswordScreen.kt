package com.example.acerosisr.View.Inicio

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.acerosisr.Funtion.CustomAlertDialog
import com.example.acerosisr.Navigation.AppScreen
import com.example.acerosisr.Navigation.Navigation
import com.example.acerosisr.R
import com.example.acerosisr.ViewModel.UserViewModel
import com.example.acerosisr.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecuperarPasswordScreen(
    navController: Navigation,
    userViewModel: UserViewModel
) {
    // Estados del ViewModel
    val step by userViewModel.recoveryStep.collectAsState()
    val alertMessage by userViewModel.alertResult.collectAsState()

    // Estados Locales Formulario Paso 1
    var numEmpleado by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }

    // Estados Locales Formulario Paso 2
    var pass1 by remember { mutableStateOf("") }
    var pass2 by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isPasswordVisibleRepeate by remember { mutableStateOf(false) }


    // Alertas
    var showDialog by remember { mutableStateOf(false) }
    var dialogText by remember { mutableStateOf("") }
    var dialogColor by remember { mutableStateOf(PrimaryColor) }
    var dialogIcon by remember { mutableStateOf(Icons.Default.Check) }
    var successExit by remember { mutableStateOf(false) } // Para salir al login al cerrar el dialogo

    LaunchedEffect(Unit) {
        userViewModel.resetRecoveryFlow()
    }

    LaunchedEffect(alertMessage) {
        if (alertMessage.isNotBlank()) {
            if (alertMessage == "PasswordUpdated") {
                dialogText = "Contraseña almacenada"
                dialogColor = PrimaryColor
                dialogIcon = Icons.Default.Check
                showDialog = true
                successExit = true
            } else if (alertMessage != "Good" && alertMessage != "ErrorLogin") {
                // Errores generales mostrados como alerta o texto rojo
                // Si es un error crítico mostramos popup
                // Para simplificar, errores de validación local se muestran en texto abajo
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo o Icono Header
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null,
            tint = PrimaryColor,
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Recuperar Contraseña",
            style = MaterialTheme.typography.headlineMedium,
            color = TextColorBlack,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))

        if (step == 0) {
            // ================= PASO 1: VALIDAR CREDENCIALES =================
            Text("Ingresa tus datos para validar tu identidad", textAlign = TextAlign.Center, color = PrimaryColor)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = numEmpleado,
                onValueChange = { if (it.length <= 7) numEmpleado = it },
                label = { Text("Número de Empleado") },
                leadingIcon = { Icon(Icons.Default.Face, contentDescription = null, tint = PrimaryColor) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = defaultTextFieldColors()
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                label = { Text("Correo Electrónico") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = PrimaryColor) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = defaultTextFieldColors()
            )

            ErrorText(alertMessage)

            Spacer(modifier = Modifier.height(24.dp))
            ButtonPrimary(text = "Validar Datos") {
                userViewModel.validateRecoveryUser(numEmpleado, correo)
            }

        } else {
            // ================= PASO 2: NUEVA CONTRASEÑA =================
            Text("Usuario validado. Ingresa tu nueva contraseña.", textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = pass1,
                onValueChange = { pass1 = it },
                label = { Text("Nueva Contraseña") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = PrimaryColor) },
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Image(
                            painterResource(id = if (isPasswordVisible) R.drawable.visibility else R.drawable.visibility_off),
                            contentDescription = if (isPasswordVisible) "Hide password" else "Show password"
                        )
                    }
                },
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = defaultTextFieldColors()
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = pass2,
                onValueChange = { pass2 = it },
                label = { Text("Confirmar Contraseña") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = PrimaryColor) },
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisibleRepeate = !isPasswordVisibleRepeate }) {
                        Image(
                            painterResource(id = if (isPasswordVisibleRepeate) R.drawable.visibility else R.drawable.visibility_off),
                            contentDescription = if (isPasswordVisibleRepeate) "Hide password" else "Show password"
                        )
                    }
                },
                visualTransformation = if (isPasswordVisibleRepeate) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = defaultTextFieldColors()
            )

            ErrorText(alertMessage)

            Spacer(modifier = Modifier.height(24.dp))
            ButtonPrimary(text = "Cambiar Contraseña") {
                userViewModel.confirmRecoveryPassword(pass1, pass2, navController)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = { navController.navigateTo(AppScreen.Login) }) {
            Text("Cancelar y volver al Login", color = PrimaryColor)
        }
    }

    // Alerta Emergente
    CustomAlertDialog(
        showDialog = showDialog,
        onDismiss = {
            showDialog = false
            userViewModel.clearAlertResult()
            if (successExit) {
                navController.navigateTo(AppScreen.Login)
            }
        },
        messages = dialogText,
        color = dialogColor,
        icon = dialogIcon
    )
}

// Componentes Auxiliares UI para limpieza
@Composable
fun ButtonPrimary(text: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        colors = ButtonDefaults.outlinedButtonColors(containerColor = PrimaryColor, contentColor = TextColorWhite),
        modifier = Modifier.fillMaxWidth().height(50.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(text, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun ErrorText(msg: String) {
    if (msg.isNotBlank() && msg != "PasswordUpdated" && msg != "Good") {
        Spacer(modifier = Modifier.height(8.dp))
        Text(msg, color = ErrorColor, textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun defaultTextFieldColors() = TextFieldDefaults.colors(
    focusedTextColor = PrimaryColor,
    unfocusedTextColor = PrimaryColor,
    focusedContainerColor = BackgroundColorTwo,
    unfocusedContainerColor = BackgroundColorTwo,
    focusedLabelColor = TextColorBlack,
    unfocusedLabelColor = TextColorBlack,
    cursorColor = PrimaryColor,
    selectionColors = TextSelectionColors(PrimaryColor, PrimaryColor.copy(alpha = 0.3f)),
    focusedIndicatorColor = PrimaryColor,
    unfocusedIndicatorColor = PrimaryColor
)

@Composable
fun VisibilityIcon(visible: Boolean, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            painter = painterResource(id = if (visible) R.drawable.visibility else R.drawable.visibility_off),
            contentDescription = "Toggle Password"
        )
    }
}