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
import com.example.acerosisr.Navigation.AppNavHost
import com.example.acerosisr.ViewModel.AppViewModelFactory
import com.example.acerosisr.ViewModel.TareasViewModel
import com.example.acerosisr.ViewModel.UserViewModel
import com.example.acerosisr.other.AppCloser // Import AppCloser
import com.example.acerosisr.ui.theme.AcerosISRTheme

class MainActivity : ComponentActivity(), AppCloser { // Implement the interface

    // Use "mock-api-url" for development without a real backend
    // Once you have your Cloud Run service, replace it with your actual URL
    private val BASE_URL = "mock-api-url" // Changed to mock URL

    private val apiService by lazy { ApiService(BASE_URL) }
    private val userRepository by lazy { UserRepositoryImpl(apiService) }
    private val tareasRepository by lazy { TareasRepositoryImpl(apiService) }
    private val appViewModelFactory by lazy { AppViewModelFactory(userRepository, tareasRepository) }

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

                    AppNavHost(
                        navController = navController,
                        userViewModel = userViewModel,
                        tareasViewModel = tareasViewModel,
                        appCloser = this // Pass the implementation of AppCloser
                    )
                }
            }
        }
    }

    override fun closeApp() { // Implement the closeApp method
        finish()
    }
}
