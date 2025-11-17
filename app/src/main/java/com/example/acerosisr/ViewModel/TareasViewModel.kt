package com.example.acerosisr.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.acerosisr.Data.TareasRepository
import com.example.acerosisr.Model.Tareas // Assuming this is your actual Tareas data class
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date

// Assuming a simplified Tarea data class if Model.Tareas is not fully defined yet.
// In a real app, this should align with your actual Model.Tareas.
// Moved the Tareas interface to Data.TareasRepository.kt
// data class Tarea(
//    val tareaId: Long,
//    val proyectoId: Long,
//    val empleadoId: Long?,
//    val estado: String,
//    val fechaInicio: Date?,
//    val fechaFin: Date?,
//    val comentarios: String?
// )

class TareasViewModel(private val tareasRepository: TareasRepository) : ViewModel() { // Changed constructor

    private val _taskList = MutableStateFlow<List<Tareas>>(emptyList())
    val taskList: StateFlow<List<Tareas>> = _taskList

    private val _selectedTask = MutableStateFlow<Tareas?>(null)
    val selectedTask: StateFlow<Tareas?> = _selectedTask

    private val _alertMessage = MutableStateFlow("")
    val alertMessage: StateFlow<String> = _alertMessage

    init {
        loadAllTasks()
    }

    fun loadAllTasks() {
        viewModelScope.launch {
            tareasRepository.getAllTasks()
                .onSuccess { tasks ->
                    _taskList.value = tasks
                }
                .onFailure { e ->
                    _alertMessage.value = "Error al cargar tareas: ${e.message}"
                    println("Error al cargar tareas: ${e.message}")
                }
        }
    }

    fun getTaskById(taskId: Long) {
        viewModelScope.launch {
            tareasRepository.getTaskById(taskId)
                .onSuccess { task ->
                    _selectedTask.value = task
                }
                .onFailure { e ->
                    _alertMessage.value = "Error al obtener tarea: ${e.message}"
                    println("Error al obtener tarea: ${e.message}")
                }
        }
    }

    fun createTask(tarea: Tareas) {
        viewModelScope.launch {
            tareasRepository.createTask(tarea)
                .onSuccess { message ->
                    _alertMessage.value = message
                    loadAllTasks() // Refresh list
                }
                .onFailure { e ->
                    _alertMessage.value = "Error al crear tarea: ${e.message}"
                    println("Error al crear tarea: ${e.message}")
                }
        }
    }

    fun updateTask(tarea: Tareas) {
        viewModelScope.launch {
            tareasRepository.updateTask(tarea.tareaId, tarea) // Assuming tareaId for update
                .onSuccess { message ->
                    _alertMessage.value = message
                    loadAllTasks() // Refresh list
                }
                .onFailure { e ->
                    _alertMessage.value = "Error al actualizar tarea: ${e.message}"
                    println("Error al actualizar tarea: ${e.message}")
                }
        }
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            tareasRepository.deleteTask(taskId)
                .onSuccess { message ->
                    _alertMessage.value = message
                    loadAllTasks() // Refresh list
                }
                .onFailure { e ->
                    _alertMessage.value = "Error al eliminar tarea: ${e.message}"
                    println("Error al eliminar tarea: ${e.message}")
                }
        }
    }

    fun assignTaskToEmployee(taskId: Long, empleadoId: Long) {
        viewModelScope.launch {
            tareasRepository.assignTaskToEmployee(taskId, empleadoId)
                .onSuccess { message ->
                    _alertMessage.value = message
                    loadAllTasks() // Refresh list
                }
                .onFailure { e ->
                    _alertMessage.value = "Error al asignar tarea: ${e.message}"
                    println("Error al asignar tarea: ${e.message}")
                }
        }
    }

    fun updateTaskStatus(taskId: Long, newStatus: String) {
        viewModelScope.launch {
            tareasRepository.updateTaskStatus(taskId, newStatus)
                .onSuccess { message ->
                    _alertMessage.value = message
                    loadAllTasks() // Refresh list
                }
                .onFailure { e ->
                    _alertMessage.value = "Error al actualizar estado de tarea: ${e.message}"
                    println("Error al actualizar estado de tarea: ${e.message}")
                }
        }
    }

    fun clearAlertMessage() {
        _alertMessage.value = ""
    }
}
