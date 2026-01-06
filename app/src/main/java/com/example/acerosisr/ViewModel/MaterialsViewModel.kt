package com.example.acerosisr.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.acerosisr.Data.MaterialsRepository
import com.example.acerosisr.Model.Material
import com.example.acerosisr.Model.MaterialMovementReport
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject


class MaterialsViewModel(
    private val repository: MaterialsRepository
) : ViewModel() {

    // LISTA DE MATERIALES (para MaterialListScreen)
    private val _materials = MutableStateFlow<List<Material>>(emptyList())
    val materials: StateFlow<List<Material>> = _materials.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // MATERIAL SELECCIONADO (para MaterialDetailScreen)
    private val _selectedMaterial = MutableStateFlow<Material?>(null)
    val selectedMaterial: StateFlow<Material?> = _selectedMaterial.asStateFlow()
    private val _movementsReport = MutableStateFlow<List<MaterialMovementReport>>(emptyList())
    val movementsReport: StateFlow<List<MaterialMovementReport>> = _movementsReport.asStateFlow()



    // ====== GET: LISTA DEL STOCK C0 ======
    fun loadMaterials() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            repository.getAllMaterials()
                .onSuccess { list ->
                    _materials.value = list
                }
                .onFailure { e ->
                    _errorMessage.value = "Error al cargar materiales: ${e.message}"
                }
            _isLoading.value = false
        }
    }

    // ====== GET: DETALLE ======
    fun loadMaterialDetail(id: Int?) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            repository.getMaterialById(id)
                .onSuccess { material ->
                    _selectedMaterial.value = material
                }
                .onFailure { e ->
                    _errorMessage.value = "Error al cargar material: ${e.message}"
                    _selectedMaterial.value = null
                }
            _isLoading.value = false
        }
    }

    // ====== POST: CREAR MATERIAL ======
    fun createMaterial(
        material: Material,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.createMaterial(material)
                .onSuccess { created ->
                    // Recargar lista
                    loadMaterials()
                    onSuccess()
                }
                .onFailure { e ->
                    val msg = "Error al crear material: ${e.message}"
                    _errorMessage.value = msg
                    onError(msg)
                }
            _isLoading.value = false
        }
    }

    fun createMaterialFromForm(
        tipo: String,
        nombre: String,
        unidadMedida: String,
        stock: String,
        descripcion: String,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        // Validaciones básicas según tu tabla Materiales
        val allowedTipos = setOf("tubo", "soldadura", "lamina", "herramienta", "otro")
        val allowedUM = setOf("pieza", "kg", "ton", "m", "lt")

        if (tipo !in allowedTipos) {
            onError("Selecciona un tipo de material válido")
            return
        }
        if (nombre.isBlank()) {
            onError("El nombre del material no puede estar vacío")
            return
        }
        if (unidadMedida !in allowedUM) {
            onError("Selecciona una unidad de medida válida")
            return
        }
        val stockValue = stock.toDoubleOrNull()
        if (stockValue == null || stockValue < 0) {
            onError("El stock debe ser un número mayor o igual a 0")
            return
        }
        // Construimos el modelo de dominio (ajusta los nombres a tu data class Material)
        val nuevoMaterial = Material(
            id = null,
            tipo = tipo,
            nombre = nombre,
            unidadMedida = unidadMedida,
            descripcion = descripcion,
            stockActual = stockValue
        )
        createMaterial(
            material = nuevoMaterial,
            onSuccess = onSuccess,
            onError = onError
        )
    }

    // ====== PUT: ACTUALIZAR MATERIAL ======
    fun updateMaterial(
        material: Material,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.updateMaterial(material)
                .onSuccess {
                    loadMaterials()
                    onSuccess()
                }
                .onFailure { e ->
                    val msg = "Error al actualizar material: ${e.message}"
                    _errorMessage.value = msg
                    onError(msg)
                }
            _isLoading.value = false
        }
    }

    // ====== DELETE: ELIMINAR MATERIAL ======
    fun deleteMaterial(
        id: Int?,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            repository.deleteMaterial(id)
                .onSuccess {
                    loadMaterials()
                    onSuccess()
                }
                .onFailure { e ->
                    val backendMsg = extractBackendError(e.message)
                    val msg = backendMsg ?: "Error al eliminar material: ${e.message}"
                    _errorMessage.value = msg
                    onError(msg)
                }

            _isLoading.value = false
        }
    }

    // ====== MOVIMIENTOS: ENTRADAS / SALIDAS ======
    fun registerEntry(
        materialId: Int?,
        cantidadStr: String,
        costoStr: String,
        observaciones: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val cantidad = cantidadStr.toDoubleOrNull()
        val costo = costoStr.toDoubleOrNull()

        if (cantidad == null || cantidad <= 0) {
            onError("La cantidad debe ser un número mayor a 0")
            return
        }
        if (costo == null || costo < 0) {
            onError("El costo unitario debe ser un número válido (>= 0)")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            repository.registerMaterialEntry(
                materialId = materialId,
                cantidad = cantidad,
                costoUnitario = costo,
                observaciones = observaciones
            ).onSuccess {
                loadMaterials() // Actualizar lista global
                onSuccess()
            }.onFailure { e ->
                val msg = extractBackendError(e.message) ?: "Error al registrar entrada: ${e.message}"
                _errorMessage.value = msg
                onError(msg)
            }
            _isLoading.value = false
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    private fun extractBackendError(raw: String?): String? {
        if (raw.isNullOrBlank()) return null
        return try {
            // A veces el mensaje viene con más texto + JSON al final
            val jsonStart = raw.indexOf('{')
            val jsonStr = if (jsonStart >= 0) raw.substring(jsonStart) else raw
            val obj = JSONObject(jsonStr)
            obj.optString("error", null).takeIf { it.isNotBlank() }
        } catch (_: Exception) {
            null
        }
    }
    fun loadMovementsReport() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null // Limpiamos errores previos

            repository.getMovementsReport()
                .onSuccess { list ->
                    // Opcional: Ordenar por fecha descendente si el backend no lo hace
                    _movementsReport.value = list.sortedByDescending { it.fecha }
                }
                .onFailure { e ->
                    _errorMessage.value = "Error al cargar reporte: ${e.message}"
                }

            _isLoading.value = false
        }
    }
}
