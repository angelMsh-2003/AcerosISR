package com.example.acerosisr.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.acerosisr.Data.ProjectsRepository
import com.example.acerosisr.Model.Project
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProjectsViewModel(
    private val repository: ProjectsRepository
) : ViewModel() {

    // Lista de proyectos (para ProjectsListScreen)
    private val _projects = MutableStateFlow<List<Project>>(emptyList())
    val projects: StateFlow<List<Project>> = _projects.asStateFlow()

    // Proyecto seleccionado (para ProjectDetailScreen, si lo haces)
    private val _selectedProject = MutableStateFlow<Project?>(null)
    val selectedProject: StateFlow<Project?> = _selectedProject.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // ====== GET: LISTA ======
    fun loadProjects() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            repository.getAllProjects()
                .onSuccess { list ->
                    _projects.value = list
                }
                .onFailure { e ->
                    _errorMessage.value = "Error al cargar proyectos: ${e.message}"
                }

            _isLoading.value = false
        }
    }

    // ====== GET: DETALLE ======
    fun loadProjectDetail(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            repository.getProjectById(id)
                .onSuccess { project ->
                    _selectedProject.value = project
                }
                .onFailure { e ->
                    _errorMessage.value = "Error al cargar proyecto: ${e.message}"
                    _selectedProject.value = null
                }

            _isLoading.value = false
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
    // ====== POST: CREAR PROYECTO ======
    /**
     * Esta firma está hecha para encajar con tu CreateProjectScreen:
     *
     * projectsViewModel.createProject(
     *    descripcion = descTrim,
     *    cliente = cliTrim,
     *    registradoPor = currentUserId,
     *    onSuccess = { ... },
     *    onError = { msg -> ... }
     * )
     */
    fun createProject(
        titulo: String,
        descripcion: String,
        cliente: String,
        registradoPor: Int?,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val nuevoProyecto = Project(
                id = null,                      // lo genera la BD
                titulo = titulo,
                descripcion = descripcion,
                cliente = cliente,
                estado = "En progreso",         // siempre así al crear
                fechaRegistro = "",             // lo pone el backend
                registradoPor = registradoPor   // empleado_id del usuario logueado (puede ser null)
            )

            repository.createProject(nuevoProyecto)
                .onSuccess {
                    // recargar lista para que aparezca el nuevo
                    loadProjects()
                    onSuccess()
                }
                .onFailure { e ->
                    val msg = "Error al crear proyecto: ${e.message}"
                    _errorMessage.value = msg
                    onError(msg)
                }

            _isLoading.value = false
        }
    }
    fun updateProject(
        id: Int,
        titulo: String,
        descripcion: String,
        cliente: String,
        estado: String,
        registradoPor: Int?,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val projectToUpdate = Project(
                id = id,
                titulo = titulo,
                descripcion = descripcion,
                cliente = cliente,
                estado = estado,
                fechaRegistro = selectedProject.value?.fechaRegistro ?: "",
                registradoPor = registradoPor
            )

            repository.updateProject(projectToUpdate)
                .onSuccess { updated ->
                    _selectedProject.value = updated
                    // Recargar lista para reflejar cambios
                    loadProjects()
                    onSuccess()
                }
                .onFailure { e ->
                    val msg = "Error al actualizar proyecto: ${e.message}"
                    _errorMessage.value = msg
                    onError(msg)
                }

            _isLoading.value = false
        }
    }

    fun deleteProject(
        id: Int,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            repository.deleteProject(id)
                .onSuccess {
                    // Sacamos el proyecto seleccionado y recargamos lista
                    _selectedProject.value = null
                    loadProjects()
                    onSuccess()
                }
                .onFailure { e ->
                    val msg = "Error al eliminar proyecto: ${e.message}"
                    _errorMessage.value = msg
                    onError(msg)
                }

            _isLoading.value = false
        }
    }
}

