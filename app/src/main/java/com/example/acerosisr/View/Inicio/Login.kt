package com.example.acerosisr.View.Inicio

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
    var showDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    val alertMessage by loginModel.alertResult.collectAsState()

    LaunchedEffect(alertMessage) {
        if (alertMessage.isNotEmpty()) {
            when (alertMessage) {
                "userNoExists" -> showDialog = true
                else -> { /* Other messages */ }
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
            .size(210.dp)
        ) {
            Image(
                painterResource(id = R.drawable.isr_logo),
                contentDescription = "Imagen_Logo",
                modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(Modifier.height(10.dp))
        Text("¡Bienvenido!", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(40.dp))
        OutlinedTextField(
            value = queryNumUser,
            onValueChange = { queryNumUser = it },
            label = {Text("Número de empleado", textAlign = TextAlign.Center)},
            shape = RoundedCornerShape(5.dp),
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Usuario", tint = PrimaryColor) },
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
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            value = queryPassword,
            onValueChange = { queryPassword = it },
            label = {Text("Contraseña", textAlign = TextAlign.Center)},
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Image(
                        painterResource(id = if (isPasswordVisible) R.drawable.visibility else R.drawable.visibility_off),
                        contentDescription = if (isPasswordVisible) "Hide password" else "Show password"
                    )
                }
            },
            shape = RoundedCornerShape(5.dp),
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Buscar", tint = PrimaryColor) },
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
            singleLine = true
        )
        Spacer(Modifier.height(20.dp))
        Text(if (alertMessage=="userNoExists"){""}else{alertMessage}, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center, color = ErrorColor)
        Spacer(Modifier.height(20.dp))
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
                contentColor = TextColorWhite
            ),
            modifier = Modifier
                .height(50.dp)
                .width(230.dp),
        ) {
            Text("Iniciar Sesión", style = MaterialTheme.typography.bodyLarge)
        }
        Spacer(Modifier.height(20.dp))
        Text("¿No tienes cuenta?", style = MaterialTheme.typography.labelLarge, color = PrimaryColor)
        Spacer(Modifier.height(3.dp))
        OutlinedButton(
            onClick = {navController.navigateTo(AppScreen.Register)},
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = BackgroundColor,
                contentColor = TextColorBlue
            ),
            modifier = Modifier
                .height(50.dp),
        ){
            Text("Crear cuenta", fontSize = 15.sp)
        }

        CustomAlertDialog(
            showDialog = showDialog,
            onDismiss = { showDialog = false; loginModel.clearAlertResult() },
            "Usuario y/o contraseña\nincorrectos",
            ErrorColor,
            icon = Icons.Default.Warning
        )
    }
}