package com.example.acerosisr.View.Menu

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.acerosisr.Navigation.AppScreen
import com.example.acerosisr.Navigation.Navigation
import com.example.acerosisr.ViewModel.UserViewModel
import com.example.acerosisr.other.AppCloser
import com.example.acerosisr.ui.theme.BackgroundColorTwo
import com.example.acerosisr.ui.theme.PrimaryColor
import com.example.acerosisr.ui.theme.SecondaryColor
import com.example.acerosisr.ui.theme.TextColorWhite
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun MenuDrawer(
    userViewModel: UserViewModel,
    appCloser: AppCloser,
    navController: Navigation? = null,
    content: @Composable (PaddingValues) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val actualUser by userViewModel.actualUser.collectAsState()

    LaunchedEffect(Unit) { userViewModel.loadActualUser() }

    val nameUser = actualUser?.NombreEmpleado ?: "Cargando..."
    val rawCargo = actualUser?.Cargo ?: ""
    val displayCargo = when (rawCargo.lowercase().trim()) {
        "dueño", "dueno" -> "Administrativo"
        "admin" -> "Administrador"
        else -> rawCargo.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = PrimaryColor,
                modifier = Modifier.fillMaxWidth(0.85f),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.height(40.dp))

                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .background(Color.White.copy(alpha = 0.1f), shape = CircleShape)
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, null, tint = TextColorWhite, modifier = Modifier.fillMaxSize())
                    }

                    Spacer(Modifier.height(24.dp))
                    Text(nameUser, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = TextColorWhite, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(12.dp))
                    Surface(color = TextColorWhite.copy(alpha = 0.2f), shape = RoundedCornerShape(50)) {
                        Text(displayCargo.uppercase(), style = MaterialTheme.typography.labelLarge, color = TextColorWhite, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(Modifier.height(32.dp))
                    HorizontalDivider(thickness = 1.dp, color = TextColorWhite.copy(alpha = 0.2f))
                    Spacer(Modifier.height(24.dp))

                    if (navController != null) {
                        OutlinedButton(
                            onClick = {
                                scope.launch { drawerState.close() }
                                navController.navigateTo(AppScreen.EditProfile)
                            },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, TextColorWhite),
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            colors = ButtonColors(Color.Transparent, TextColorWhite, Color.Transparent, TextColorWhite)
                        ) {
                            Icon(Icons.Default.Edit, null)
                            Spacer(Modifier.width(12.dp))
                            Text("Editar Información")
                        }
                        Spacer(Modifier.height(16.dp))
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    OutlinedButton(
                        onClick = {
                            userViewModel.deleteUserActual()
                            appCloser.closeApp()
                        },
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.5.dp, TextColorWhite),
                        modifier = Modifier.fillMaxWidth().height(60.dp),
                        colors = ButtonColors(Color.Transparent, TextColorWhite, Color.Transparent, TextColorWhite.copy(alpha = 0.5f)),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ExitToApp, null, modifier = Modifier.size(28.dp))
                            Spacer(Modifier.width(12.dp))
                            Text("Cerrar sesión", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                }
            }
        },
    ) {
        Scaffold(
            containerColor = BackgroundColorTwo,
            floatingActionButton = {
                IconButton(
                    onClick = { scope.launch { if (drawerState.isClosed) drawerState.open() else drawerState.close() } },
                    modifier = Modifier.size(56.dp)
                ) {
                    Surface(shape = CircleShape, color = SecondaryColor, shadowElevation = 6.dp, modifier = Modifier.fillMaxSize()) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Rounded.Person, "Menu", tint = Color.White, modifier = Modifier.size(32.dp))
                        }
                    }
                }
            },
            floatingActionButtonPosition = FabPosition.Start,
            content = content
        )
    }
}