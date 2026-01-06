package com.example.acerosisr.Model
data class Project(
    val id: Int?,           // proyecto_id
    val titulo: String,
    val descripcion: String,
    val cliente: String,
    val estado: String,     // "En progreso", "Finalizado", "Cancelado"
    val fechaRegistro: String, // String ISO que viene del backend
    val registradoPor: Int? // empleado_id o null
)
