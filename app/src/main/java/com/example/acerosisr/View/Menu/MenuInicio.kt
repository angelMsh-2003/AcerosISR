package com.example.acerosisr.View.Menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource // Changed import
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.acerosisr.Navigation.AppScreen
import com.example.acerosisr.Navigation.Navigation
import com.example.acerosisr.Funtion.Encabezado
import com.example.acerosisr.Funtion.MenuDrawer
import com.example.acerosisr.ViewModel.UserViewModel
import com.example.acerosisr.other.AppCloser
import com.example.acerosisr.ui.theme.*
import com.example.acerosisr.R // Added import for Android resources
@Composable
fun MenuInicio (navController: Navigation, appCloser: AppCloser, userViewModel: UserViewModel) {
    val scrollState = rememberScrollState()

    MenuDrawer (userViewModel, appCloser) {
        Column (modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(BackgroundColorTwo),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // HEADER
            Encabezado()
            // CONTENT
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp, start = 10.dp, end = 10.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(
                    modifier = Modifier
                        .width(170.dp)
                        .height(220.dp)
                        .padding(bottom = 10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(Color.White),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier
                            .padding(10.dp)
                            .width(150.dp)
                            .height(30.dp)
                            .background(PrimaryColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Asigar tareas",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            color = TextColorWhite
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            navController.navigateTo(AppScreen.TasksList)
                        },
                        modifier = Modifier
                            .size(150.dp)
                            .border(2.dp, SecondaryColor)
                    ) {
                        Image(
                            painterResource(id = R.drawable.note_menu), // Changed call
                            null
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .width(170.dp)
                        .height(220.dp)
                        .padding(bottom = 10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(Color.White),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .padding(10.dp)
                            .width(150.dp)
                            .height(30.dp)
                            .background(PrimaryColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Progreso de Actividades",
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            color = TextColorWhite
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            navController.navigateTo(AppScreen.ActivitiesProgress) // Changed to ActivitiesProgress
                        },
                        modifier = Modifier
                            .size(150.dp)
                            .border(2.dp, SecondaryColor)
                    ) {
                        Image(
                            painterResource(id = R.drawable.progreso_actividades), // Changed call
                            null
                        )
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 20.dp, start = 10.dp, end = 10.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(
                    modifier = Modifier
                        .width(170.dp)
                        .height(220.dp)
                        .padding(bottom = 10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(Color.White),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .padding(10.dp)
                            .width(150.dp)
                            .height(30.dp)
                            .background(PrimaryColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Materiales",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            color = TextColorWhite
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            navController.navigateTo(AppScreen.MaterialsList)
                        },
                        modifier = Modifier
                            .size(150.dp)
                            .border(2.dp, SecondaryColor)
                    ) {
                        Image(
                            painterResource(id = R.drawable.materiales), // Changed call
                            null
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .width(170.dp)
                        .height(220.dp)
                        .padding(bottom = 10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(Color.White),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .padding(10.dp)
                            .width(150.dp)
                            .height(30.dp)
                            .background(PrimaryColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Proyectos",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            color = TextColorWhite
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            navController.navigateTo(AppScreen.ProjectsList)
                        },
                        modifier = Modifier
                            .size(150.dp)
                            .border(2.dp, SecondaryColor)
                    ) {
                        Image(
                            painterResource(id = R.drawable.note_menu), // Changed call
                            null
                        )
                    }
                }
            }
            Row (
                modifier = Modifier.fillMaxWidth().padding(top = 20.dp, start = 10.dp, end = 10.dp),
                horizontalArrangement = Arrangement.Center
            ){
                Column (modifier = Modifier
                    .width(170.dp)
                    .height(220.dp)
                    .padding(bottom = 10.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color.White),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .padding(10.dp)
                            .width(150.dp)
                            .height(30.dp)
                            .background(PrimaryColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Gestionar Empleados",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            color = TextColorWhite
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            navController.navigateTo(AppScreen.EmployeeList)
                        },
                        modifier = Modifier
                            .size(150.dp)
                            .border(2.dp, SecondaryColor)
                    ) {
                        Icon(
                            Icons.Rounded.Person,
                            contentDescription = "Localized description",

                            tint = SecondaryColor,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}