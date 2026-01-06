package com.example.acerosisr.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.acerosisr.Data.MaterialsRepository
import com.example.acerosisr.Data.ProjectsRepository
import com.example.acerosisr.Data.TareasRepository
import com.example.acerosisr.Data.UserRepository

class AppViewModelFactory(
    private val userRepository: UserRepository,
    private val tareasRepository: TareasRepository,
    private  val materialsRepository: MaterialsRepository,
    private  val projectsRepository: ProjectsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(userRepository) as T
        } else if (modelClass.isAssignableFrom(TareasViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TareasViewModel(tareasRepository, materialsRepository) as T
        } else if (modelClass.isAssignableFrom(MaterialsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MaterialsViewModel(materialsRepository) as T
        }  else  if (modelClass.isAssignableFrom(ProjectsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProjectsViewModel(projectsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
