package com.example.acerosisr.View.Inicio

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.acerosisr.Funtion.CustomAlertDialog
import com.example.acerosisr.Navigation.AppScreen
import com.example.acerosisr.Navigation.Navigation
import com.example.acerosisr.R
import com.example.acerosisr.ViewModel.UserViewModel
import com.example.acerosisr.ui.theme.*

@Composable
fun ValidarNumUsuario (navController: Navigation, loginModel: UserViewModel) {
    var queryNumUser by remember { mutableStateOf("") }
    var showDialogError by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    val alertMessage by loginModel.alertResult.collectAsState()

    LaunchedEffect(alertMessage) {
        if (alertMessage.isNotEmpty()) {
            when (alertMessage) {
                "NoExistUser" -> showDialogError = true
            }
            loginModel.clearAlertResult()
        }
    }

    Column (modifier = Modifier
        .fillMaxSize()
        .verticalScroll(scrollState)
        .background(BackgroundColor)
        .padding(start = 40.dp, end = 40.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier
            .size(300.dp)
        ) {
            Image(
                painterResource(id = R.drawable.isr_logo),
                contentDescription = "Logo_ISR",
                modifier = Modifier.fillMaxSize()
            )
        }
        Text("Ingresa tu número de usuario", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(20.dp))
        OutlinedTextField(
            value = queryNumUser,
            onValueChange = { queryNumUser = it },
            label = { Text("Número de usuario", textAlign = TextAlign.Center) },
            shape = RoundedCornerShape(5.dp),
            modifier = Modifier
                .fillMaxWidth(),
            colors = TextFieldDefaults.colors(
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
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            ),
            singleLine = true
        )
        Spacer(Modifier.height(20.dp))
        Text(if (alertMessage == "Good" || alertMessage == "NoExistUser"){""} else {alertMessage}, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center, color = if (alertMessage =="Good"){SecondaryColor} else {ErrorColor})
        Spacer(Modifier.height(20.dp))
        OutlinedButton(
            onClick = {
                loginModel.existingUserValid(queryNumUser, navController)
            },
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = PrimaryColor,
                contentColor = TextColorWhite
            ),
            modifier = Modifier
                .height(50.dp)
                .width(230.dp),
        ){
            Text("Validar", style = MaterialTheme.typography.bodyLarge)
        }
        Spacer(Modifier.height(10.dp))
        Row (modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("¿Ya te has registrado?", style = MaterialTheme.typography.bodyLarge, color = PrimaryColor)
            TextButton(
                onClick = {navController.navigateTo(AppScreen.Login)},
                colors = ButtonDefaults.textButtonColors(
                    contentColor = PrimaryColor
                ),
            ) {
                Text("Ingresa aquí.", style = MaterialTheme.typography.bodyLarge)
            }
        }
        CustomAlertDialog(
            showDialog = showDialogError,
            onDismiss = { showDialogError = false; loginModel.clearAlertResult() },
            "El usuario no existe",
            ErrorColor,
            icon = Icons.Default.Warning
        )
    }
}

@Composable
fun Registro (navController: Navigation, loginModel: UserViewModel) {
    var queryName by remember { mutableStateOf("") }
    var queryMail by remember { mutableStateOf("") }
    var queryPassword by remember { mutableStateOf("") }
    var queryPasswordRepeat by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isPasswordVisibleRepeat by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    val alertMessage by loginModel.alertResult.collectAsState()

    LaunchedEffect(alertMessage) {
        if (alertMessage.isNotEmpty()) {
            when (alertMessage) {
                "Good" -> showDialog = true
            }
            loginModel.clearAlertResult()
        }
    }

    Column (modifier = Modifier
        .fillMaxSize()
        .verticalScroll(scrollState)
        .background(BackgroundColor)
        .padding(start = 40.dp, end = 40.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier
            .size(70.dp)
        ) {
            Icon(
                Icons.Default.Person,
                contentDescription = "Persona",
                modifier = Modifier.fillMaxSize(),
                tint = PrimaryColor
            )
        }
        Text("¡Regístrate!", style = MaterialTheme.typography.displaySmall)
        Spacer(Modifier.height(20.dp))
        OutlinedTextField(
            value = queryName,
            onValueChange = { queryName = it },
            label = { Text("Nombre completo", textAlign = TextAlign.Center) },
            // ... (resto del OutlinedTextField)
        )
        // ... (resto de los OutlinedTextFields)
        Spacer(Modifier.height(20.dp))
        Text(alertMessage, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center, color = ErrorColor)
        Spacer(Modifier.height(20.dp))
        OutlinedButton(
            onClick = {
                if (queryName.isBlank() || queryMail.isBlank() || queryPassword.isBlank() || queryPasswordRepeat.isBlank()) {
                    loginModel.setAlertResult("Llena todos los campos")
                } else {
                    loginModel.registerNewBasicUser(queryName, queryMail, queryPassword, queryPasswordRepeat, navController)
                }},
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = PrimaryColor,
                contentColor = TextColorWhite
            ),
            modifier = Modifier
                .height(50.dp)
                .width(230.dp),
        ){
            Text("Registrar", style = MaterialTheme.typography.bodyLarge)
        }
        CustomAlertDialog(
            showDialog = showDialog,
            onDismiss = { navController.navigateTo(AppScreen.Login) },
            "Te registraste exitosamente\nAhora inicia sesión",
            PrimaryColor,
            icon = Icons.Default.Check
        )
    }
}
