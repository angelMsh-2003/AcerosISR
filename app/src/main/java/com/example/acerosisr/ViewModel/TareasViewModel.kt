package com.example.acerosisr.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.acerosisr.Data.MaterialsRepository
import com.example.acerosisr.Data.TareasRepository
import com.example.acerosisr.Model.ProjectTasksSummary
import com.example.acerosisr.Model.Tareas
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.acerosisr.Model.Empleados
import com.example.acerosisr.Model.Material
import com.example.acerosisr.Model.UsuarioTareaEspecifica
import kotlinx.coroutines.flow.asStateFlow
data class MaterialUsageItem(
    val material: Material,
    val cantidadUsada: Double
)
class TareasViewModel(
    private val tareasRepository: TareasRepository,
    private val materialsRepository: MaterialsRepository // ✅ INYECCIÓN NUEVA

) : ViewModel() {
    private val _employees = MutableStateFlow<List<Empleados>>(emptyList())
    val employees: StateFlow<List<Empleados>> = _employees.asStateFlow()


    // ===== LISTA GENERAL DE TAREAS (para ProjectTasksScreen, etc.) =====
    private val _taskList = MutableStateFlow<List<Tareas>>(emptyList())
    val taskList: StateFlow<List<Tareas>> get() = _taskList

    private val _selectedTask = MutableStateFlow<Tareas?>(null)
    val selectedTask: StateFlow<Tareas?> get() = _selectedTask

    // Mensajes tipo "toast" / alerta simple
    private val _alertMessage = MutableStateFlow("")
    val alertMessage: StateFlow<String> get() = _alertMessage

    // ===== RESUMEN POR PROYECTO (para pantalla tipo dashboard) =====
    private val _projectsTasksSummary = MutableStateFlow<List<ProjectTasksSummary>>(emptyList())
    val projectsTasksSummary: StateFlow<List<ProjectTasksSummary>> get() = _projectsTasksSummary

    // Loading + error genérico (reusado por las vistas)
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage
    private val _myTasks = MutableStateFlow<List<UsuarioTareaEspecifica>>(emptyList())
    val myTasks: StateFlow<List<UsuarioTareaEspecifica>> = _myTasks.asStateFlow()
    // === NUEVO: LISTA DE MATERIALES DISPONIBLES (Para el dropdown) ===
    private val _availableMaterials = MutableStateFlow<List<Material>>(emptyList())
    val availableMaterials: StateFlow<List<Material>> = _availableMaterials.asStateFlow()

    init {
        // Cargamos todas las tareas al iniciar (puedes quitarlo si quieres controlarlo desde la UI)
        loadAllTasks()
    }

    // ==================== TAREAS: CRUD ====================

    fun loadAllTasks() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            tareasRepository.getAllTasks()
                .onSuccess { tasks ->
                    _taskList.value = tasks
                }
                .onFailure { e ->
                    val msg = "Error al cargar tareas: ${e.message}"
                    _alertMessage.value = msg
                    _errorMessage.value = msg
                    println(msg)
                }

            _isLoading.value = false
        }
    }
    fun loadMaterialsForReport() {
        viewModelScope.launch {
            materialsRepository.getAllMaterials()
                .onSuccess { _availableMaterials.value = it }
                .onFailure { println("Error cargando materiales: ${it.message}") }
        }
    }

    // === NUEVO: COMPLETAR TAREA CON CONSUMO DE MATERIALES ===
    fun completeTaskWithMaterials(
        tareaId: Long,
        usedMaterials: List<MaterialUsageItem>,
        currentEmployeeId: Long
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            var errorOccurred = false

            // 1. Registrar salidas de cada material seleccionado
            // Lo hacemos secuencial para asegurar que todos se registren
            for (item in usedMaterials) {
                val result = materialsRepository.registerMaterialExitForTask(
                    materialId = item.material.id ?: 0,
                    cantidad = item.cantidadUsada,
                    tareaId = tareaId,
                    observaciones = "Uso en tarea $tareaId" // Observación automática
                )

                if (result.isFailure) {
                    _errorMessage.value = "Error registrando material ${item.material.nombre}: ${result.exceptionOrNull()?.message}"
                    errorOccurred = true
                    break // Detenemos si falla uno (o podrías continuar y reportar errores parciales)
                }
            }

            // 2. Si todo salió bien (o no hubo materiales), actualizamos el estado a "Completada"
            if (!errorOccurred) {
                tareasRepository.updateTaskStatus(tareaId, "Completada")
                    .onSuccess {
                        _alertMessage.value = "Tarea completada y reporte guardado"
                        // Recargamos la lista
                        loadMyTasks(currentEmployeeId)
                    }
                    .onFailure { e ->
                        _errorMessage.value = "Error al finalizar tarea: ${e.message}"
                    }
            }

            _isLoading.value = false
        }
    }

    fun createTask(tarea: Tareas) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            tareasRepository.createTask(tarea)
                .onSuccess { message ->
                    _alertMessage.value = message
                    // recargar lista general para que la nueva tarea salga
                    loadAllTasks()
                }
                .onFailure { e ->
                    val msg = "Error al crear tarea: ${e.message}"
                    _alertMessage.value = msg
                    _errorMessage.value = msg
                    println(msg)
                }

            _isLoading.value = false
        }
    }

    fun updateTask(tarea: Tareas) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            tareasRepository.updateTask(tarea.tareaId, tarea)
                .onSuccess { message ->
                    _alertMessage.value = message
                    loadAllTasks()
                }
                .onFailure { e ->
                    val msg = "Error al actualizar tarea: ${e.message}"
                    _alertMessage.value = msg
                    _errorMessage.value = msg
                    println(msg)
                }

            _isLoading.value = false
        }
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            tareasRepository.deleteTask(taskId)
                .onSuccess { message ->
                    _alertMessage.value = message
                    loadAllTasks()
                }
                .onFailure { e ->
                    val msg = "Error al eliminar tarea: ${e.message}"
                    _alertMessage.value = msg
                    _errorMessage.value = msg
                    println(msg)
                }

            _isLoading.value = false
        }
    }

    fun loadMyTasks(empleadoId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            tareasRepository.getTasksByEmployee(empleadoId)
                .onSuccess { list ->
                    _myTasks.value = list
                }
                .onFailure { e ->
                    _errorMessage.value = "Error al cargar mis tareas: ${e.message}"
                }
            _isLoading.value = false
        }
    }
    fun updateMyTaskStatus(taskId: Long, newStatus: String, currentEmployeeId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            tareasRepository.updateTaskStatus(taskId, newStatus)
                .onSuccess {
                    _alertMessage.value = "Tarea actualizada a: $newStatus"
                    // Recargamos inmediatamente la lista de este empleado
                    loadMyTasks(currentEmployeeId)
                }
                .onFailure { e ->
                    _errorMessage.value = "No se pudo actualizar: ${e.message}"
                }
            _isLoading.value = false
        }
    }

    fun updateTaskStatus(taskId: Long, newStatus: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            tareasRepository.updateTaskStatus(taskId, newStatus)
                .onSuccess { message ->
                    _alertMessage.value = message
                    loadAllTasks()
                }
                .onFailure { e ->
                    val msg = "Error al actualizar estado de tarea: ${e.message}"
                    _alertMessage.value = msg
                    _errorMessage.value = msg
                    println(msg)
                }

            _isLoading.value = false
        }
    }

    fun clearAlertMessage() {
        _alertMessage.value = ""
    }

    // ==================== RESUMEN POR PROYECTO ====================

    fun loadProjectsTasksSummary() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            tareasRepository.getProjectsTasksSummary()
                .onSuccess { list ->
                    _projectsTasksSummary.value = list
                }
                .onFailure { e ->
                    val msg = "Error al cargar proyectos con tareas: ${e.message}"
                    _errorMessage.value = msg
                    println(msg)
                }

            _isLoading.value = false
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
    fun loadEmployees() {
        viewModelScope.launch {
            // si no quieres que esto pise otros loaders, puedes quitar estas dos líneas:
            _isLoading.value = true
            _errorMessage.value = null

            tareasRepository.getAllEmployees()
                .onSuccess { list ->
                    _employees.value = list
                }
                .onFailure { e ->
                    val msg = "Error al cargar empleados: ${e.message}"
                    _errorMessage.value = msg
                    println(msg)
                }

            _isLoading.value = false
        }
    }
    fun clearSelectedTask() {
        _selectedTask.value = null
    }


}
