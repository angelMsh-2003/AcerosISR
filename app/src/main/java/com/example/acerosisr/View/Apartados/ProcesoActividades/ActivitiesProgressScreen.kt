package com.example.acerosisr.View.Apartados.ProcesoActividades

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.acerosisr.Model.ActivityProgress
import com.example.acerosisr.Navigation.AppScreen
import com.example.acerosisr.Navigation.Navigation // Added import
import com.example.acerosisr.ViewModel.TareasViewModel
import com.example.acerosisr.ui.theme.AcerosISRTheme
import com.example.acerosisr.ui.theme.BackgroundColor
import com.example.acerosisr.ui.theme.BackgroundColorTwo
import com.example.acerosisr.ui.theme.PrimaryColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivitiesProgressScreen(navController: Navigation,tareasViewModel: TareasViewModel) { // Changed NavHostController to Navigation
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Progreso de Actividades") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) { // Used popBackStack from Navigation interface
                        Icon(Icons.Filled.ArrowBack, "Atrás")
                    }
                }
            )
        }
    ) { paddingValues ->
        val sampleActivities = listOf(
            ActivityProgress(1, "Diseño de Estructura", 0.75f, "Juan Pérez"),
            ActivityProgress(2, "Corte de Materiales", 0.5f, "María García"),
            ActivityProgress(3, "Ensamblaje Inicial", 0.2f, "Carlos López"),
            ActivityProgress(4, "Inspección de Soldadura", 0.0f, "Ana Torres")
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(BackgroundColorTwo)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(sampleActivities) { activity ->
                ActivityProgressItem(activity = activity)
            }
        }
    }
}

@Composable
fun ActivityProgressItem(activity: ActivityProgress) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = activity.name, style = MaterialTheme.typography.titleMedium)
            Text(text = "Asignado a: ${activity.assignedTo}", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = activity.progress,
                modifier = Modifier.fillMaxWidth(),
                color = PrimaryColor,
                trackColor = PrimaryColor.copy(alpha = 0.3f)
            )
            Text(text = "${(activity.progress * 100).toInt()}% Completado", style = MaterialTheme.typography.bodySmall)
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun ActivitiesProgressScreenPreview() {
//    AcerosISRTheme {
//        // Need a mock Navigation instance for preview
//        ActivitiesProgressScreen(object : Navigation { // Mock Navigation
//            override fun navigateTo(route: AppScreen.MaterialMovement) {
//                // Do nothing for preview
//            }
//            override fun popBackStack() {
//                // Do nothing for preview
//            }
//        })
//    }
//}
