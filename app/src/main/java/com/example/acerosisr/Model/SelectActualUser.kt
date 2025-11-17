package com.example.acerosisr.Model

data class SelectActualUser(
    val ID: Long = 0L, // Assuming this is an internal ID, not the employee number
    val UserId: Long = 0L,
    val NombreEmpleado: String = "",
    val Cargo: String = ""
)
