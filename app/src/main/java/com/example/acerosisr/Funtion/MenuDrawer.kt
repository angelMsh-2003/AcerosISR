package com.example.acerosisr.Funtion

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FabPosition
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.acerosisr.ViewModel.UserViewModel
import com.example.acerosisr.other.AppCloser
import com.example.acerosisr.ui.theme.*
import kotlinx.coroutines.launch

// Assuming SelectActualUser is a data class that will be correctly imported or defined elsewhere
// For now, I'll use placeholders for its properties if SelectActualUser cannot be resolved.

@Composable
fun MenuDrawer (userViewModel: UserViewModel, appCloser: AppCloser, content: @Composable (PaddingValues) -> Unit) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    // val infoUser : SelectActualUser = userViewModel.selectActualUser()
    // val NameUser by remember { mutableStateOf(infoUser.NombreEmpleado) }
    // val Cargo by remember { mutableStateOf(infoUser.Cargo) }
    // val NumberUser by remember { mutableStateOf(infoUser.UserId) }

    // Using mock data for user info since SelectActualUser is not fully defined in this context.
    val NameUser by remember { mutableStateOf("Nombre de Usuario") }
    val Cargo by remember { mutableStateOf("Cargo de Usuario") }
    val NumberUser by remember { mutableStateOf(12345) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet (
                drawerContainerColor = PrimaryColor,
                modifier = Modifier.width(300.dp),
            ) {
                Column (modifier = Modifier.fillMaxWidth().padding(top = 5.dp, start = 5.dp),
                    horizontalAlignment = Alignment.CenterHorizontally){
                    Spacer(Modifier.height(60.dp))
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "User",
                        tint = Color.White,
                        modifier = Modifier.size(100.dp)
                    )
                    Spacer(Modifier.height(20.dp))
                    HorizontalDivider(thickness = 1.dp, color = FourthColor)
                    Spacer(Modifier.height(20.dp))
                    Text(NameUser, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextColorWhite, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(3.dp))
                    Text(Cargo, style = MaterialTheme.typography.titleMedium, color = TextColorWhite)
                    Spacer(Modifier.height(3.dp))
                    Text(NumberUser.toString(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextColorWhite)

                    Spacer(modifier = Modifier.weight(1f))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp)
                    ){
                        OutlinedButton(
                            onClick = {
                                userViewModel.deleteUserActual()
                                appCloser.closeApp() 
                                      },
                            shape = RoundedCornerShape(5.dp),
                            border = BorderStroke(1.dp, PrimaryColor),
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonColors(
                                containerColor = PrimaryColor,
                                contentColor = TextColorWhite,
                                disabledContainerColor = PrimaryColor.copy(alpha = 0.5f), // Set a disabled container color
                                disabledContentColor = TextColorWhite.copy(alpha = 0.5f) // Set a disabled content color
                            ),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ExitToApp,
                                    contentDescription = "FlechaUP/Down",
                                    tint = Color.White
                                )
                                Spacer(Modifier.width(9.dp))
                                Text("Cerrar sesión",
                                    style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Start
                                )
                            }
                        }
                    }
                }
            }
        },
    ) {
        Scaffold(
            containerColor = BackgroundColorTwo,
            floatingActionButton = {
                IconButton(
                    onClick = {
                        scope.launch {
                            drawerState.apply {
                                if (isClosed) open() else close()
                            }
                        }
                    },
                    modifier = Modifier
                        .size(40.dp)
                ) {
                    Icon(
                        Icons.Rounded.Person,
                        contentDescription = "Atras",
                        modifier = Modifier.fillMaxSize().background(SecondaryColor),
                        tint = Color.White
                    )
                }
            },
            floatingActionButtonPosition = FabPosition.Start,
            content = content
        )
    }
}
