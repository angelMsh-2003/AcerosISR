package com.example.acerosisr.View.Apartados.Materiales

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
fun MaterialDetailScreen(navController: Navigation, materialId: Int?) {
    // In a real application, you would fetch material details using materialId
    val material = materialId?.let {
        // Mock data for demonstration
        when (it) {
            1 -> Material(1, "Tubo Cuadrado 2x2", "tubo", 150.0)
            2 -> Material(2, "Soldadura 7018", "soldadura", 50.0)
            3 -> Material(3, "Lámina Lisa Cal. 16", "lamina", 25.0)
            4 -> Material(4, "Electrodo E6010", "soldadura", 75.0)
            5 -> Material(5, "Escuadra Magnética", "herramienta", 10.0)
            else -> null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(material?.nombre ?: "Detalle del Material") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, "Atrás")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (material != null) {
                Text(text = "Nombre: ${material.nombre}", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "ID: ${material.id}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Tipo: ${material.tipo}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Stock Actual: ${material.stock}", style = MaterialTheme.typography.bodyMedium)
            } else {
                Text("Material no encontrado.", style = MaterialTheme.typography.titleLarge)
            }
        }
    }
}
//
//@Preview(showBackground = true)
//@Composable
//fun MaterialDetailScreenPreview() {
//    AcerosISRTheme {
//        MaterialDetailScreen(object : Navigation {
//            override fun navigateTo(route: AppScreen.MaterialMovement) {}
//            override fun popBackStack() {}
//        }, materialId = 1)
//    }
//}
