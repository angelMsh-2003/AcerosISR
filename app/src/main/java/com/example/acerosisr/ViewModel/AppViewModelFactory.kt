package com.example.acerosisr.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.acerosisr.Data.TareasRepository
import com.example.acerosisr.Data.UserRepository

class AppViewModelFactory(private val userRepository: UserRepository, private val tareasRepository: TareasRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(userRepository) as T
        } else if (modelClass.isAssignableFrom(TareasViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TareasViewModel(tareasRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
