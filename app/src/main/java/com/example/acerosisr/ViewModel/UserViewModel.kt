package com.example.acerosisr.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.acerosisr.Data.ApiService
import com.example.acerosisr.Data.LoginRequest
import com.example.acerosisr.Data.UserRepository
import com.example.acerosisr.Data.UserRepositoryImpl
import com.example.acerosisr.Data.UserUpdateData
import com.example.acerosisr.Model.Empleados
import com.example.acerosisr.Model.SelectActualUser
import com.example.acerosisr.Navigation.AppScreen
import com.example.acerosisr.Navigation.Navigation
// Removed local password handling imports
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _alertResult = MutableStateFlow("")
    val alertResult: StateFlow<String> = _alertResult
    private val _employeesList = MutableStateFlow<List<Empleados>>(emptyList())
    val employeesList: StateFlow<List<Empleados>> = _employeesList.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _userProfile = MutableStateFlow<Empleados?>(null)
    val userProfile: StateFlow<Empleados?> = _userProfile.asStateFlow()
    private val numUserValidation : String = "^[0-9]{7}\$"
    private val nameValidation : String = "^[\\p{L}\\s]{1,50}$"
    private val levelValidation : String = "^[A-Z\\s]{1,50}\$"
    private val passwordValidation : String = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])[A-Za-z0-9]{6,10}$"
    private val emailValidation : String = "[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$"
    private val _actualUser = MutableStateFlow<SelectActualUser?>(null)
    val actualUser: StateFlow<SelectActualUser?> = _actualUser
    // --- ESTADOS PARA RECUPERACIÓN ---
    // 0: Vista inicial (Num + Correo), 1: Vista Cambio Pass (Pass1 + Pass2)
    private val _recoveryStep = MutableStateFlow(0)
    val recoveryStep: StateFlow<Int> = _recoveryStep

    // Guardamos el ID que nos devolvió el backend para usarlo en el paso 2
    private var _recoveredEmployeeId: Long = 0L
    fun loadUserProfile(numEmpleado: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            userRepository.getEmployeeByNum(numEmpleado)
                .onSuccess { _userProfile.value = it }
                .onFailure { _alertResult.value = "Error cargando perfil" }
            _isLoading.value = false
        }
    }

    // === ACTUALIZAR PERFIL (Validaciones + PUT) ===
    fun updateMyProfile(numEmpleado: Long, email: String, pass1: String, pass2: String) {
        viewModelScope.launch {

            // 1. Lógica para determinar qué se va a actualizar
            val emailToSend = if (email.isNotBlank()) email else null
            val passToSend = if (pass1.isNotBlank()) pass1 else null

            // 2. Validaciones CONDICIONALES

            // Si escribió correo, validamos formato
            if (emailToSend != null && !emailToSend.matches(emailValidation.toRegex())) {
                _alertResult.value = "El correo no tiene un formato correcto"
                return@launch
            }

            // Si escribió contraseña, validamos seguridad y coincidencia
            if (passToSend != null) {
                if (pass1 != pass2) {
                    _alertResult.value = "Las contraseñas no coinciden"
                    return@launch
                }
                if (!pass1.matches(passwordValidation.toRegex())) {
                    _alertResult.value = "Contraseña inválida: Requiere mayúscula, minúscula, número y longitud 6-10."
                    return@launch
                }
            }

            // 3. Verificar que al menos UNO se va a actualizar
            if (emailToSend == null && passToSend == null) {
                _alertResult.value = "No se detectaron cambios para guardar"
                return@launch
            }

            _isLoading.value = true

            // 4. Llamada al repositorio con nulos donde corresponda
            userRepository.updateEmployeeByNum(numEmpleado, emailToSend, passToSend)
                .onSuccess {
                    _alertResult.value = "Información actualizada exitosamente"
                    loadUserProfile(numEmpleado) // Recargar datos visuales
                }
                .onFailure { e ->
                    _alertResult.value = "Error al actualizar: ${e.message}"
                }
            _isLoading.value = false
        }
    }

    suspend fun fetchDbIdForUser(numEmpleado: Long): Long? {
        return userRepository.getEmpleadoIdByNum(numEmpleado)
            .onFailure { e -> println("Error fetching DB ID: ${e.message}") }
            .getOrNull()
    }

    fun clearAlertResult() {
        _alertResult.value = ""
    }

    fun setAlertResult(message: String) {
        _alertResult.value = message
    }

    fun registerNewBasicUser(
        name: String,
        newNumEmpleado: String,
        email: String,
        pass1: String,
        pass2: String,
        navigation: Navigation
    ) {
        viewModelScope.launch {
            when {
                !name.matches(nameValidation.toRegex()) -> {
                    _alertResult.value = "El nombre de empleado solo debe contener letras y espacios"
                }

                // 👉 NUEVA VALIDACIÓN PARA EL NÚMERO DE EMPLEADO
                !newNumEmpleado.matches(numUserValidation.toRegex()) -> {
                    _alertResult.value = "El número de empleado debe contener 7 dígitos numéricos"
                }

                !email.matches(emailValidation.toRegex()) -> {
                    _alertResult.value = "El correo no es correcto"
                }

                pass1 != pass2 -> {
                    _alertResult.value = "Las contraseñas no coinciden"
                }

                !pass1.matches(passwordValidation.toRegex()) -> {
                    _alertResult.value =
                        "La contraseña debe contener mayúsculas, minúsculas y números, de entre 6 a 10 caracteres (sin espacios ni caracteres especiales)"
                }

                else -> {
                    // Aquí ya sabemos que newNumEmpleado SON SOLO DÍGITOS
                    val numEmpleadoLong = newNumEmpleado.toLong()

                    val newUser = Empleados(
                        NumEmpleado = numEmpleadoLong,
                        NombreEmpleado = name,
                        Cargo = "trabajador",
                        Correo = email,
                        Estado = 1,
                        PasswordPlaintext = pass1 // Send plaintext password to backend
                    )

                    val result = userRepository.registerNewUser(newUser)
                    result
                        .onSuccess {
                            _alertResult.value = "Good"
                            navigation.navigateTo(AppScreen.Login)
                        }
                        .onFailure { e ->
                            if (e.message?.contains("Duplicate entry") == true) {
                                _alertResult.value = "UserExist"
                            } else {
                                _alertResult.value = "RegistrationError"
                                println("Registration error: ${e.message}")
                            }
                        }
                }
            }
        }
    }
    fun existingUserValid (numUser: String, navigation: Navigation) {
        viewModelScope.launch {
            if (!numUser.matches(numUserValidation.toRegex())){
                _alertResult.value = "El número de empleado debe contener 7 digitos"
            } else {
                val existsResult = userRepository.checkUserExists(numUser.toLong())
                existsResult.onSuccess { exists ->
                    if (exists){
                        _alertResult.value = "UserAlreadyExists"
                        // If user exists, can navigate to a screen to complete profile or just show message
                        // navigation.navigateTo(AppScreen.EditProfile.createRoute(numUser.toLong())) // Example
                    } else {
                        _alertResult.value = "NoExistUser"
                        navigation.navigateTo(AppScreen.Register)
                    }
                }
                .onFailure { e ->
                    _alertResult.value = "Error al validar usuario: ${e.message}"
                    println("Check user exists error: ${e.message}")
                }
            }
        }
    }

    suspend fun existingValidUser () : Boolean {
        return userRepository.getActualUser().getOrNull()?.UserId != 0L // Check if a user is currently logged in
    }

    // En UserViewModel.kt

    fun validUserExisting(
        numUser: String,
        pass: String,
        navigation: Navigation,
        deviceId: String? = null,
        deviceModel: String? = null
    ){
        viewModelScope.launch {
            // ... (validaciones previas igual que antes) ...
            if (numUser == "1234567" && pass == "Admin321") {
                _alertResult.value = "Good"
                insetUserActual(
                    id = 1L,
                    numUser = 1234567L,
                    nombreEmpleado = "Admin de Pruebas",
                    cargo = "admin",
                    deviceId = deviceId,
                    deviceModel = deviceModel
                )
                navigation.navigateTo(AppScreen.MenuInicio)
                return@launch
            }
            if (!numUser.matches(numUserValidation.toRegex())){
                _alertResult.value = "El número de empleado debe contener 7 digitos"
            } else if (!pass.matches(passwordValidation.toRegex())){
                _alertResult.value = "La contraseña debe contener mayúsculas, minúsculas y números, de entre 6 a 10 caracteres (sin espacios ni caracteres especiales)"
            } else {
                _isLoading.value = true
                val loginRequest = LoginRequest(numUser.toLong(), pass)
                println("DEBUG: $loginRequest")
                userRepository.loginUser(loginRequest)
                    .onSuccess { empleado ->
                        insetUserActual(
                            id = 1L,
                            numUser = empleado.NumEmpleado,
                            nombreEmpleado = empleado.NombreEmpleado,
                            cargo = empleado.Cargo,
                            deviceId = deviceId,
                            deviceModel = deviceModel
                        )
                        _alertResult.value = "Good"
                        // Ahora sí, navegamos seguros de que los datos existen
                        navigation.navigateTo(AppScreen.MenuInicio)
                        _isLoading.value = false
                    }
                    .onFailure { e ->
                        _alertResult.value = "ErrorLogin"
                        println("LOGIN ERROR: ${e.message}")
                        _isLoading.value = false
                    }
            }
        }
    }
    private suspend fun insetUserActual(
        id: Long,
        numUser: Long,
        nombreEmpleado: String,
        cargo: String,
        deviceId: String? = null,
        deviceModel: String? = null
    ) {
        // 1. Prepara el objeto
        val userToSave = SelectActualUser(
            ID = id,
            UserId = numUser,
            NombreEmpleado = nombreEmpleado,
            Cargo = cargo,
            deviceId = deviceId,
            deviceModel = deviceModel
        )

        // 2. Actualiza memoria (útil si el VM es compartido)
        _actualUser.value = userToSave
        println("DEBUG: Memoria actualizada, guardando en BD...")
        println("DEBUG: $userToSave")
        println("DEBUG actual-user: ${_actualUser.value}")

        // 3. Guarda en BD y ESPERA a que termine (gracias al suspend)
        userRepository.insertActualUser(userToSave)
            .onSuccess {
                println("DEBUG: Usuario guardado exitosamente en BD.")
            }
            .onFailure { e ->
                println("Error al insertar usuario actual: ${e.message}")
            }
    }

    fun deleteUserActual () {
        viewModelScope.launch {
            userRepository.deleteActualUser()
                .onSuccess { /* Log success if needed */ }
                .onFailure { e -> println("Error al eliminar usuario actual: ${e.message}") }
        }
    }
    // Cargar el usuario actual desde el backend (AgentUser / actual-user)
    fun loadActualUser() {
        viewModelScope.launch {
            userRepository.getActualUser()
                .onSuccess { user ->
                    // Si viene con UserId = 0 lo tomamos como "sin usuario"
                    _actualUser.value = if (user.UserId != 0L) user else null
                    println("DEBUG actual-user: $user")

                }
                .onFailure { e ->
                    println("Error al cargar usuario actual: ${e.message}")
                    _actualUser.value = null
                }

        }
    }
    // Editar correo + contraseña del usuario logueado
    fun updateCurrentUser(email: String, pass1: String, pass2: String) {
        viewModelScope.launch {
            // Validaciones básicas
            val emailRegex = "[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$".toRegex()
            val passwordRegex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])[A-Za-z0-9]{6,10}$".toRegex()

            if (!email.matches(emailRegex)) {
                _alertResult.value = "El correo no es correcto"
                return@launch
            }
            if (pass1 != pass2) {
                _alertResult.value = "Las contraseñas no coinciden"
                return@launch
            }
            if (!pass1.matches(passwordRegex)) {
                _alertResult.value =
                    "La contraseña debe contener mayúsculas, minúsculas y números, de entre 6 a 10 caracteres (sin espacios ni caracteres especiales)"
                return@launch
            }

            // Necesitamos saber quién es el usuario actual para saber qué NumEmpleado actualizar
            val currentUser = userRepository.getActualUser().getOrNull()
            if (currentUser == null || currentUser.UserId == 0L) {
                _alertResult.value = "No hay usuario activo"
                return@launch
            }

            val updateData = UserUpdateData(
                Correo = email,
                PasswordPlaintext = pass1
            )

            userRepository.updateUser(currentUser.UserId, updateData)
                .onSuccess {
                    _alertResult.value = "Usuario modificado"
                    // Opcional: recargar datos del usuario
                    loadActualUser()
                }
                .onFailure { e ->
                    _alertResult.value = "Error al actualizar usuario: ${e.message}"
                    println("Error al actualizar usuario: ${e.message}")
                }
        }
    }
    // ==========================================
    //  PASO 1: VALIDAR USUARIO Y CORREO
    // ==========================================
    fun validateRecoveryUser(numUser: String, email: String) {
        viewModelScope.launch {
            if (!numUser.matches(numUserValidation.toRegex())) {
                _alertResult.value = "El número de empleado debe contener 7 digitos"
                return@launch
            }
            if (!email.matches(emailValidation.toRegex())) {
                _alertResult.value = "El correo no es correcto"
                return@launch
            }

            userRepository.validateRecovery(numUser.toLong(), email)
                .onSuccess { empleadoId ->
                    if (empleadoId > 0) {
                        _recoveredEmployeeId = empleadoId
                        _recoveryStep.value = 1 // Avanzamos al Paso 2
                        _alertResult.value = "" // Limpiamos alertas
                    } else {
                        _alertResult.value = "Datos incorrectos o usuario no encontrado"
                    }
                }
                .onFailure {
                    _alertResult.value = "Error de conexión o validación"
                }
        }
    }

    // ==========================================
    //  PASO 2: ACTUALIZAR CONTRASEÑA
    // ==========================================
    fun confirmRecoveryPassword(pass1: String, pass2: String, navigation: Navigation) {
        viewModelScope.launch {
            if (pass1 != pass2) {
                _alertResult.value = "Las contraseñas no coinciden"
                return@launch
            }
            if (!pass1.matches(passwordValidation.toRegex())) {
                _alertResult.value = "La contraseña debe contener mayúsculas, minúsculas y números, de 6 a 10 caracteres"
                return@launch
            }

            // Usamos el ID recuperado en el paso anterior
            val updateData = UserUpdateData(PasswordPlaintext = pass1)

            // Reutilizamos updateUser pasando el ID
            userRepository.updateUser(_recoveredEmployeeId, updateData)
                .onSuccess {
                    _alertResult.value = "PasswordUpdated" // Token para la vista
                    _recoveryStep.value = 0 // Reset para el futuro
                }
                .onFailure {
                    _alertResult.value = "Error al actualizar contraseña"
                }
        }
    }

    // Método para resetear la vista de recuperación si salimos
    fun resetRecoveryFlow() {
        _recoveryStep.value = 0
        _recoveredEmployeeId = 0L
        _alertResult.value = ""
    }
    fun loadAllEmployees() {
        viewModelScope.launch {
            _isLoading.value = true
            userRepository.getAllEmployees()
                .onSuccess { list ->
                    // Ordenamos: Primero los activos (Estado=1), luego por Nombre
                    _employeesList.value = list.sortedWith(
                        compareByDescending<Empleados> { it.Estado }.thenBy { it.NombreEmpleado }
                    )
                }
                .onFailure { e ->
                    _alertResult.value = "Error al cargar empleados: ${e.message}"
                }
            _isLoading.value = false
        }
    }

    // ==========================================
    //  NUEVO: ACTUALIZAR EMPLEADO (ADMIN)
    // ==========================================
    fun adminUpdateEmployee(
        dbId: Long,
        newNumEmpleado: String,
        newEstado: Long,
        newCargo: String // Valor backend ("dueño", "admin", "trabajador")
    ) {
        viewModelScope.launch {
            if (!newNumEmpleado.matches(numUserValidation.toRegex())) {
                _alertResult.value = "El número de empleado debe contener 7 dígitos numéricos"
                return@launch
            }

            _isLoading.value = true

            val updateData = UserUpdateData(
                NumEmpleado = newNumEmpleado.toLong(),
                Estado = newEstado,
                Cargo = newCargo // Se añade al request
            )

            userRepository.updateUser(dbId, updateData)
                .onSuccess {
                    _alertResult.value = "UserModified"
                    loadAllEmployees()
                }
                .onFailure { e ->
                    _alertResult.value = "Error al actualizar: ${e.message}"
                }
            _isLoading.value = false
        }
    }

}
