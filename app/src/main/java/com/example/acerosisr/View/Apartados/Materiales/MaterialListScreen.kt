package com.example.acerosisr.View.Apartados.Materiales

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.acerosisr.Model.Material // Added import
import com.example.acerosisr.Navigation.AppScreen
import com.example.acerosisr.Navigation.Navigation
import com.example.acerosisr.ui.theme.AcerosISRTheme

// Removed the local data class Material definition

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaterialListScreen(navController: Navigation) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Materiales") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigateTo(AppScreen.MaterialMovement)
            }) {
                Icon(Icons.Filled.Add, "Agregar Material o Movimiento")
            }
        }
    ) { paddingValues ->
        val sampleMaterials = listOf(
            Material(1, "Tubo Cuadrado 2x2", "tubo", 150.0),
            Material(2, "Soldadura 7018", "soldadura", 50.0),
            Material(3, "Lámina Lisa Cal. 16", "lamina", 25.0),
            Material(4, "Electrodo E6010", "soldadura", 75.0),
            Material(5, "Escuadra Magnética", "herramienta", 10.0)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(sampleMaterials) { material ->
                MaterialListItem(material = material) {
                    navController.navigateTo(AppScreen.MaterialDetail.createRoute(material.id))
                }
            }
        }
    }
}

@Composable
fun MaterialListItem(material: Material, onClick: (Material) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick(material) }
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(text = material.nombre, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Tipo: ${material.tipo}", style = MaterialTheme.typography.bodySmall)
            Text(text = "Stock: ${material.stock}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun MaterialListScreenPreview() {
//    AcerosISRTheme {
//        MaterialListScreen(object : Navigation {
//            override fun navigateTo(route: AppScreen.MaterialMovement) {}
//            override fun popBackStack() {}
//        })
//    }
//}
