package com.example.acerosisr.View.Inicio

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.acerosisr.Funtion.CustomAlertDialog
import com.example.acerosisr.Navigation.AppScreen
import com.example.acerosisr.Navigation.Navigation
import com.example.acerosisr.R
import com.example.acerosisr.ViewModel.UserViewModel
import com.example.acerosisr.ui.theme.*

@Composable
fun Login (navController : Navigation, loginModel : UserViewModel) {
    var queryNumUser by remember { mutableStateOf("") }
    var queryPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    // Observers
    val alertMessage by loginModel.alertResult.collectAsState()
    val isLoading by loginModel.isLoading.collectAsState() // Escuchamos el estado de carga

    var showDialog by remember { mutableStateOf(false) }
    var dialogText by remember { mutableStateOf("") }
    var dialogColor by remember { mutableStateOf(ErrorColor) }
    var dialogIcon by remember { mutableStateOf(Icons.Default.Warning) }

    LaunchedEffect(alertMessage) {
        when (alertMessage) {
            "Good" -> {
                showDialog = true
                dialogText = "Inicio de sesión exitoso"
                dialogColor = PrimaryColor
                dialogIcon = Icons.Default.Check
                loginModel.clearAlertResult()
            }
            "ErrorLogin" -> {
                showDialog = true
                dialogText = "Usuario y/o contraseña\nincorrectos"
                dialogColor = ErrorColor
                dialogIcon = Icons.Default.Warning
                loginModel.clearAlertResult()
            }
        }
    }

    // CONTENEDOR PRINCIPAL (BOX) PARA MANEJAR CAPAS
    Box(modifier = Modifier.fillMaxSize()) {

        // --- CAPA 1: CONTENIDO DEL LOGIN ---
        Column (modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(BackgroundColor)
            .padding(horizontal = 40.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.size(210.dp)) {
                Image(
                    painterResource(id = R.drawable.isr_logo),
                    contentDescription = "Imagen_Logo",
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(Modifier.height(10.dp))
            Text("¡Bienvenido!", style = MaterialTheme.typography.headlineLarge, color = PrimaryColor)
            Spacer(Modifier.height(40.dp))

            // Inputs
            OutlinedTextField(
                value = queryNumUser,
                onValueChange = { if (it.length <= 7) queryNumUser = it },
                label = {Text("Número de empleado", textAlign = TextAlign.Center)},
                shape = RoundedCornerShape(5.dp),
                leadingIcon = { Icon(Icons.Default.Person, "Usuario", tint = PrimaryColor) },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                singleLine = true,
                enabled = !isLoading // Deshabilitar si carga
            )
            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = queryPassword,
                onValueChange = { queryPassword = it },
                label = {Text("Contraseña", textAlign = TextAlign.Center)},
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }, enabled = !isLoading) {
                        Image(
                            painterResource(id = if (isPasswordVisible) R.drawable.visibility else R.drawable.visibility_off),
                            contentDescription = if (isPasswordVisible) "Hide password" else "Show password"
                        )
                    }
                },
                shape = RoundedCornerShape(5.dp),
                leadingIcon = { Icon(Icons.Default.Lock, "Pass", tint = PrimaryColor) },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors(),
                singleLine = true,
                enabled = !isLoading
            )

            Spacer(Modifier.height(10.dp))

            // Botón Olvidé Contraseña
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                TextButton(
                    onClick = { navController.navigateTo(AppScreen.RecoveryPassword.route) },
                    enabled = !isLoading
                ) {
                    Text(
                        text = "¿Olvidaste tu contraseña?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isLoading) Color.Gray else PrimaryColor,
                        textAlign = TextAlign.End
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            // Mensajes de error en texto (validaciones locales)
            if (alertMessage != "Good" && alertMessage != "ErrorLogin" && alertMessage.isNotBlank()) {
                Text(alertMessage, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center, color = ErrorColor)
            }

            Spacer(Modifier.height(20.dp))

            // Botón Iniciar Sesión
            OutlinedButton(
                onClick = {
                    if (queryNumUser.isBlank() || queryPassword.isBlank()) {
                        loginModel.setAlertResult("Llena todos los campos")
                    } else {
                        loginModel.validUserExisting(queryNumUser, queryPassword, navController)
                    }
                },
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = PrimaryColor,
                    contentColor = TextColorWhite,
                    disabledContainerColor = PrimaryColor.copy(alpha = 0.6f)
                ),
                modifier = Modifier.height(50.dp).width(230.dp),
                enabled = !isLoading
            ) {
                Text("Iniciar Sesión", style = MaterialTheme.typography.bodyLarge)
            }

            Spacer(Modifier.height(20.dp))
            Text("¿No tienes cuenta?", style = MaterialTheme.typography.labelLarge, color = PrimaryColor)
            Spacer(Modifier.height(3.dp))

            OutlinedButton(
                onClick = {navController.navigateTo(AppScreen.Register)},
                colors = ButtonDefaults.outlinedButtonColors(containerColor = BackgroundColor, contentColor = TextColorBlue),
                modifier = Modifier.height(50.dp),
                enabled = !isLoading
            ){
                Text("Crear cuenta", fontSize = 15.sp)
            }
        }

        // --- CAPA 2: LOADING OVERLAY MODERNO ---
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    //.background(Color.Black.copy(alpha = 0.6f)) // Fondo oscuro para enfoque
                    .clickable(
                        indication = null, // Sin efecto ripple
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = { /* Absorbe clicks para bloquear UI trasera */ }
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Spinner Flotante
                CircularProgressIndicator(
                    color = Color.White, // Blanco para máximo contraste sobre fondo oscuro
                    modifier = Modifier.size(64.dp), // Tamaño generoso
                    strokeWidth = 5.dp // Grosor moderno
                )
            }
        }

        // Alerta popup (se mantiene encima de todo)
        CustomAlertDialog(
            showDialog = showDialog,
            onDismiss = { showDialog = false },
            messages = dialogText,
            color = dialogColor,
            icon = dialogIcon
        )
    }
}

@Composable
fun textFieldColors() = TextFieldDefaults.colors(
    focusedTextColor = PrimaryColor,
    unfocusedTextColor = PrimaryColor,
    focusedContainerColor = BackgroundColorTwo,
    unfocusedContainerColor = BackgroundColorTwo,
    focusedLabelColor = TextColorBlack,
    unfocusedLabelColor = TextColorBlack,
    cursorColor = PrimaryColor,
    selectionColors = TextSelectionColors(PrimaryColor, PrimaryColor.copy(alpha = 0.3f)),
    focusedIndicatorColor = PrimaryColor,
    unfocusedIndicatorColor = PrimaryColor,
    disabledContainerColor = BackgroundColorTwo, // Mismo fondo que cuando está habilitado
    disabledTextColor = PrimaryColor,            // Mismo color de texto
    disabledLabelColor = TextColorBlack,         // Mismo color de etiqueta
    disabledIndicatorColor = PrimaryColor,       // Mismo borde
    disabledLeadingIconColor = PrimaryColor,     // Mismo color de icono (Usuario/Candado)
    disabledTrailingIconColor = PrimaryColor,    // Mismo color de icono (Ojo)
    disabledPlaceholderColor = PrimaryColor      // Por si usas placeholder
)