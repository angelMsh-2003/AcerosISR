package com.example.acerosisr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.acerosisr.Data.ApiService
import com.example.acerosisr.Data.TareasRepositoryImpl
import com.example.acerosisr.Data.UserRepositoryImpl
import com.example.acerosisr.Data.MaterialsRepositoryImpl
import com.example.acerosisr.Data.ProjectsRepositoryImpl
import com.example.acerosisr.Navigation.AppNavHost
import com.example.acerosisr.ViewModel.AppViewModelFactory
import com.example.acerosisr.ViewModel.MaterialsViewModel
import com.example.acerosisr.ViewModel.ProjectsViewModel
import com.example.acerosisr.ViewModel.TareasViewModel
import com.example.acerosisr.ViewModel.UserViewModel
import com.example.acerosisr.other.AppCloser // Import AppCloser
import com.example.acerosisr.ui.theme.AcerosISRTheme

class MainActivity : ComponentActivity(), AppCloser { // Implement the interface

    // Use "mock-api-url" for development without a real backend
    // Once you have your Cloud Run service, replace it with your actual URL
    private val BASE_URL = "https://backend-flask-1052567957618.us-central1.run.app"
//    private val BASE_URL = "https://backend-flask-1052567957618.us-central1.run.app/"

    private val apiService by lazy { ApiService(BASE_URL) }
    private val userRepository by lazy { UserRepositoryImpl(apiService) }
    private val tareasRepository by lazy { TareasRepositoryImpl(apiService) }
    private val materialsRepository by lazy { MaterialsRepositoryImpl(apiService) }
    private val projectsRepository by lazy { ProjectsRepositoryImpl(apiService) }
    private val appViewModelFactory by lazy { AppViewModelFactory(userRepository, tareasRepository, materialsRepository, projectsRepository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AcerosISRTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val userViewModel: UserViewModel = viewModel(factory = appViewModelFactory)
                    val tareasViewModel: TareasViewModel = viewModel(factory = appViewModelFactory)
                    val materialsViewModel : MaterialsViewModel = viewModel(factory = appViewModelFactory)
                    val projectsViewModel : ProjectsViewModel = viewModel(factory = appViewModelFactory)

                    AppNavHost(
                        navController = navController,
                        userViewModel = userViewModel,
                        tareasViewModel = tareasViewModel,
                        materialsViewModel= materialsViewModel,
                        projectsViewModel = projectsViewModel,
                        appCloser = this// Pass the implementation of AppCloser,
                    )
                }
            }
        }
    }

    override fun closeApp() { // Implement the closeApp method
        finish()
    }
}
