package com.example.acerosisr.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.acerosisr.Data.LoginRequest
import com.example.acerosisr.Data.UserRepository
import com.example.acerosisr.Data.UserUpdateData
import com.example.acerosisr.Model.Empleados
import com.example.acerosisr.Model.SelectActualUser
import com.example.acerosisr.Navigation.AppScreen
import com.example.acerosisr.Navigation.Navigation
// Removed local password handling imports
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _alertResult = MutableStateFlow("")
    val alertResult: StateFlow<String> = _alertResult

    private val numUserValidation : String = "^[0-9]{7}\$"
    private val nameValidation : String = "^[A-Z\\s]{1,50}\$"
    private val levelValidation : String = "^[A-Z\\s]{1,50}\$"
    private val passwordValidation : String = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])[A-Za-z0-9]{6,10}$"
    private val emailValidation : String = "[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$"

    fun clearAlertResult() {
        _alertResult.value = ""
    }

    fun setAlertResult(message: String) {
        _alertResult.value = message
    }

    fun insertUser (user : Empleados) { // This function is now used for initial user insertion without full details, consider if still needed or combined with registerNewBasicUser
        viewModelScope.launch {
            if (!user.NumEmpleado.toString().matches(numUserValidation.toRegex())){
                _alertResult.value = "El número de empleado debe contener 7 digitos"
            } else if (!user.NombreEmpleado.matches(nameValidation.toRegex())) {
                _alertResult.value = "El nombre de empleado solo debe contener letras y espacios"
            } else if (!user.Cargo.matches(levelValidation.toRegex())) {
                _alertResult.value = "El cargo solo debe contener letras y espacios"
            } else {
                // This insertUser function probably needs to be re-evaluated if registerNewBasicUser covers all new user creation.
                // For now, it passes the user as is.
                val result = userRepository.registerNewUser(user)
                result.onSuccess { _alertResult.value = "Good" }
                    .onFailure { e ->
                        if (e.message?.contains("Duplicate entry") == true) { // Assuming backend sends a specific error for duplicate
                            _alertResult.value = "UserExist"
                        } else {
                            _alertResult.value = "Error: ${e.message}"
                            println("Insert user error: ${e.message}")
                        }
                    }
            }
        }
    }

    fun updateUser (mail: String, pass1 : String?, pass2: String?, estado: Long, numUser: String, navigation: Navigation) { // pass1 and pass2 can be null if not updating password
        viewModelScope.launch {
            var passwordUpdateNeeded = false
            if (pass1 != null && pass2 != null) {
                if (!mail.matches(emailValidation.toRegex())) {
                    _alertResult.value = "El correo no es correcto"
                    return@launch
                } else if (pass1 != pass2) {
                    _alertResult.value = "Las contraseñas no coinciden"
                    return@launch
                } else if (!pass1.matches(passwordValidation.toRegex())){
                    _alertResult.value = "La contraseña debe contener mayúsculas, minúsculas y números, de entre 6 a 10 caracteres (sin espacios ni caracteres especiales)"
                    return@launch
                }
                passwordUpdateNeeded = true
            } else if (mail != null && !mail.matches(emailValidation.toRegex())) {
                _alertResult.value = "El correo no es correcto"
                return@launch
            }

            val updateData = UserUpdateData(
                Correo = mail,
                Estado = estado,
                PasswordPlaintext = if (passwordUpdateNeeded) pass1 else null
            )

            val result = userRepository.updateUser(numUser.toLong(), updateData)
            result.onSuccess { _alertResult.value = "Good" }
                .onFailure { e ->
                    _alertResult.value = "Error al actualizar usuario: ${e.message}"
                    println("Update user error: ${e.message}")
                }
        }
    }

    fun registerNewBasicUser (name: String, email: String, pass1: String, pass2: String, navigation: Navigation) {
        viewModelScope.launch {
            if (!name.matches(nameValidation.toRegex())) {
                _alertResult.value = "El nombre de empleado solo debe contener letras y espacios"
            } else if (!email.matches(emailValidation.toRegex())) {
                _alertResult.value = "El correo no es correcto"
            } else if (pass1 != pass2) {
                _alertResult.value = "Las contraseñas no coinciden"
            }
            else if (!pass1.matches(passwordValidation.toRegex())){
                _alertResult.value = "La contraseña debe contener mayúsculas, minúsculas y números, de entre 6 a 10 caracteres (sin espacios ni caracteres especiales)"
            } else {
                val newNumEmpleado = userRepository.countEmployees().getOrNull()?.plus(1000000L) ?: 1000000L // Example simple generation

                val newUser = Empleados(
                    NumEmpleado = newNumEmpleado,
                    NombreEmpleado = name,
                    Cargo = "trabajador",
                    Correo = email,
                    Estado = 1,
                    PasswordPlaintext = pass1 // Send plaintext password to backend
                )

                val result = userRepository.registerNewUser(newUser)
                result.onSuccess { 
                    _alertResult.value = "Good"
                    navigation.navigateTo(AppScreen.Login)
                }
                .onFailure { e ->
                    if (e.message?.contains("Duplicate entry") == true) { // Assuming backend sends a specific error for duplicate
                        _alertResult.value = "UserExist"
                    } else {
                        _alertResult.value = "RegistrationError"
                        println("Registration error: ${e.message}")
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

    suspend fun readUserExisting (numUser: Long) : Result<Empleados>  {
        return userRepository.getEmployeeDetails(numUser)
    }

    fun validUserExisting (numUser: String, pass: String, navigation: Navigation) {
        viewModelScope.launch {
            if (numUser == "1234567" && pass == "Admin321") { // Admin bypass
                _alertResult.value = "Good"
                // TODO: Insert admin user as actual user if not exists
                navigation.navigateTo(AppScreen.MenuInicio)
                return@launch
            }

            if (!numUser.matches(numUserValidation.toRegex())){
                _alertResult.value = "El número de empleado debe contener 7 digitos"
            } else if (!pass.matches(passwordValidation.toRegex())){
                _alertResult.value = "La contraseña debe contener mayúsculas, minúsculas y números, de entre 6 a 10 caracteres (sin espacios ni caracteres especiales)"
            } else {
                val loginRequest = LoginRequest(numUser.toLong(), pass)
                userRepository.loginUser(loginRequest)
                    .onSuccess { message ->
                        // If login is successful, get employee details to save as actual user
                        val employeeDetailsResult = userRepository.getEmployeeDetails(numUser.toLong())
                        employeeDetailsResult.onSuccess { empleado ->
                            insetUserActual(1, empleado.NumEmpleado, empleado.NombreEmpleado, empleado.Cargo)
                            _alertResult.value = "Good"
                            navigation.navigateTo(AppScreen.MenuInicio)
                        }
                        .onFailure { e ->
                            _alertResult.value = "Error al obtener detalles de empleado después del login: ${e.message}"
                            println("Get employee details after login error: ${e.message}")
                        }
                    }
                    .onFailure { e ->
                        _alertResult.value = "Credenciales inválidas: ${e.message}"
                        println("Login error: ${e.message}")
                    }
            }
        }
    }

    private fun insetUserActual (id: Long, numUser: Long, nombreEmpleado: String, cargo: String) {
        viewModelScope.launch {
            val actualUser = SelectActualUser(ID = id, UserId = numUser, NombreEmpleado = nombreEmpleado, Cargo = cargo)
            userRepository.insertActualUser(actualUser)
                .onSuccess { /* Log success if needed */ }
                .onFailure { e -> println("Error al insertar usuario actual: ${e.message}") }
        }
    }

    fun deleteUserActual () {
        viewModelScope.launch {
            userRepository.deleteActualUser()
                .onSuccess { /* Log success if needed */ }
                .onFailure { e -> println("Error al eliminar usuario actual: ${e.message}") }
        }
    }

    suspend fun selectActualUser () : Result<SelectActualUser> {
        return userRepository.getActualUser()
    }
}
