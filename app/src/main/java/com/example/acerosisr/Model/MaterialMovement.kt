package com.example.acerosisr.Model

data class MaterialMovement(
    val materialId: Int?,
    val tipoMovimiento: String,   // "entrada"
    val cantidad: Double,
    val costoUnitario: Double? = null,
    val observaciones: String? = null
)


data class MaterialMovementReport(
    val movimientoId: Int,
    val materialId: Int?, // Puede venir nulo según tu definición anterior
    val materialName: String,
    val tipoMovimiento: String, // "entrada" o "salida"
    val cantidad: Double,
    val fecha: String, // Se recibe como ISO string
    val observaciones: String?
)