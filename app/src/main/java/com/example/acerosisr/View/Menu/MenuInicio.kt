package com.example.acerosisr.View.Menu

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.acerosisr.Funtion.Encabezado
import com.example.acerosisr.Navigation.AppScreen
import com.example.acerosisr.Navigation.Navigation
import com.example.acerosisr.ViewModel.UserViewModel
import com.example.acerosisr.other.AppCloser
import com.example.acerosisr.ui.theme.PrimaryColor
import com.example.acerosisr.ui.theme.TextColorWhite

@Composable
fun MenuInicio(
    navController: Navigation,
    appCloser: AppCloser,
    userViewModel: UserViewModel
) {
    val user by userViewModel.actualUser.collectAsState()

    LaunchedEffect(Unit) { userViewModel.loadActualUser() }

    val cargo = user?.Cargo?.lowercase()?.trim() ?: ""

    val isAdmin = cargo == "admin"
    val isDueno = cargo == "dueño" || cargo == "dueno"
    val isTrabajador = cargo == "trabajador"

    val showMisTareas = true
    val showGestionMateriales = isAdmin || isDueno
    val showVerMateriales = isTrabajador

    // 4. Resto de permisos
    val showAsignarTareas = isAdmin || isDueno
    val showMisProyectos = isAdmin || isDueno
    val showGestionEmpleados = isAdmin
    val showProgresoActividades = isAdmin

    MenuDrawer(userViewModel, appCloser, navController) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(PrimaryColor)
        ) {
            Encabezado()
            Spacer(modifier = Modifier.height(10.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 32.dp, top = 10.dp)
            ) {

                // --- MIS TAREAS ---
                if (showMisTareas) {
                    item {
                        DashboardItem(
                            title = "Mis\nTareas",
                            icon = Icons.Filled.AccountTree,
                            onClick = { navController.navigateTo(AppScreen.MyTasksScreen) }
                        )
                    }
                }

                // --- GESTIÓN MATERIALES (CRUD - Admin/Dueño) ---
                if (showGestionMateriales) {
                    item {
                        DashboardItem(
                            title = "Gestión de\nMateriales",
                            icon = Icons.Filled.Category,
                            onClick = { navController.navigateTo(AppScreen.MaterialsList) }
                        )
                    }
                }

                // --- VER MATERIALES (READ ONLY - Trabajador) ---
                if (showVerMateriales) {
                    item {
                        DashboardItem(
                            title = "Ver\nMateriales",
                            icon = Icons.Filled.Inventory, // Icono diferente para distinguir visualmente
                            onClick = { navController.navigateTo(AppScreen.WorkerMaterialsList) }
                        )
                    }
                }

                // --- ASIGNAR TAREAS ---
                if (showAsignarTareas) {
                    item {
                        DashboardItem(
                            title = "Asignar\nTareas",
                            icon = Icons.Filled.Assignment,
                            onClick = { navController.navigateTo(AppScreen.ProjectTasksList) }
                        )
                    }
                }

                // --- MIS PROYECTOS ---
                if (showMisProyectos) {
                    item {
                        DashboardItem(
                            title = "Mis\nProyectos",
                            icon = Icons.Filled.Apartment,
                            onClick = { navController.navigateTo(AppScreen.ProjectsList) }
                        )
                    }
                }

                // --- GESTIONAR EMPLEADOS ---
                if (showGestionEmpleados) {
                    item {
                        DashboardItem(
                            title = "Gestionar\nEmpleados",
                            icon = Icons.Filled.Group,
                            onClick = { navController.navigateTo(AppScreen.EmployeeList) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.15f)
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TextColorWhite,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = TextColorWhite,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
        }
    }
}