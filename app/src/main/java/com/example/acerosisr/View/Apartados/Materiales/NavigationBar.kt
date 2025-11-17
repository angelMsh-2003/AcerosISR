package com.example.acerosisr.View.Apartados.Materiales

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.acerosisr.Navigation.AppScreen
import com.example.acerosisr.Navigation.Navigation
import com.example.acerosisr.ui.theme.AcerosISRTheme

@Composable
fun AppNavigationBar(navController: Navigation) { // Changed NavController to Navigation
    var selectedItem by remember { mutableStateOf(0) }
    val items = listOf("Inicio", "Materiales", "Proyectos", "Empleados") // Example items

    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { 
                    Icon(when(item) {
                        "Inicio" -> Icons.Filled.Home
                        "Materiales" -> Icons.Filled.Info // Placeholder, use a relevant icon
                        "Proyectos" -> Icons.Filled.Settings // Placeholder, use a relevant icon
                        "Empleados" -> Icons.Filled.Info // Placeholder, use a relevant icon
                        else -> Icons.Filled.Home
                    }, contentDescription = item) 
                },
                label = { Text(item) },
                selected = selectedItem == index,
                onClick = { 
                    selectedItem = index 
                    when(item) {
                        "Inicio" -> navController.navigateTo(AppScreen.Home)
                        "Materiales" -> navController.navigateTo(AppScreen.MaterialsList)
                        "Proyectos" -> navController.navigateTo(AppScreen.ProjectsList)
                        "Empleados" -> navController.navigateTo(AppScreen.EmployeeList)
                    }
                }
            )
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun AppNavigationBarPreview() {
//    AcerosISRTheme {
//        // Need a mock Navigation instance for preview
//        AppNavigationBar(object : Navigation { // Mock Navigation
//            override fun navigateTo(route: AppScreen.MaterialMovement) {
//                // Do nothing for preview
//            }
//            override fun popBackStack() {
//                // Do nothing for preview
//            }
//        })
//    }
//}
