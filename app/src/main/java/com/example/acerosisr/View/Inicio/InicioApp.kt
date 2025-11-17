package com.example.acerosisr.View.Inicio

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource // Changed import
import androidx.compose.ui.unit.dp
import com.example.acerosisr.Navigation.AppScreen
import com.example.acerosisr.Navigation.Navigation
import com.example.acerosisr.ViewModel.UserViewModel
import com.example.acerosisr.ui.theme.PrimaryColor
import com.example.acerosisr.ui.theme.TextColorWhite
import com.example.acerosisr.R // Added import for Android resources
import androidx.compose.runtime.rememberCoroutineScope // Added import
import kotlinx.coroutines.launch // Added import

@Composable
fun InicioApp (userViewModel: UserViewModel, navController : Navigation){
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope() // Obtain CoroutineScope

    Column (modifier = Modifier
        .fillMaxSize()
        .verticalScroll(scrollState)
        .background(Color.White) // Keeping Color.White as it was a direct Color import, not from Theme.backgroundColor
        .padding(start = 20.dp, end = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center

    ) {
        Box(modifier = Modifier
            .size(300.dp)
        ) {
            Image(
                painterResource(id = R.drawable.isr_logo), // Changed call
                contentDescription = "Logo_ISR",
                modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(Modifier.height(200.dp))
        OutlinedButton(
            onClick = {
                scope.launch { // Launch a coroutine
                    val existingUser = userViewModel.existingValidUser() // Now callable
                    if (existingUser) {
                        navController.navigateTo(AppScreen.MenuInicio)
                    } else {
                        navController.navigateTo(AppScreen.Login)
                    }
                }
                      },
            colors = ButtonColors(
                containerColor = PrimaryColor,
                contentColor = TextColorWhite,
                disabledContainerColor = TextColorWhite,
                disabledContentColor = PrimaryColor,
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .height(50.dp)
                .width(250.dp)
        ){
            Text("Iniciar Sesión", style = MaterialTheme.typography.bodyLarge)
        }
        Spacer(Modifier.height(20.dp))
        OutlinedButton(
            onClick = {navController.navigateTo(AppScreen.Register)},
            colors = ButtonColors(
                containerColor = PrimaryColor,
                contentColor = TextColorWhite,
                disabledContainerColor = TextColorWhite,
                disabledContentColor = PrimaryColor,
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .height(50.dp)
                .width(250.dp)
        ){
            Text("Registrarse", style = MaterialTheme.typography.bodyLarge)
        }
    }
}
