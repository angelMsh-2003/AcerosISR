package com.example.acerosisr.Model

data class Material(
    val id: Int? = null,
    val tipo: String,
    val nombre: String,
    val unidadMedida: String,
    val stockActual: Double,
    val descripcion: String? = null
)
