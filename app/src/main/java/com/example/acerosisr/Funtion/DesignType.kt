package com.example.acerosisr.Funtion

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.acerosisr.ui.theme.PrimaryColor
import com.example.acerosisr.ui.theme.SecondaryColor
import com.example.acerosisr.ui.theme.TextColorBlack

// Ejemplo de un archivo DesignType.kt
object AppDesign {
    val CardElevation = 4.dp
    val DefaultPadding = 16.dp
    val CornerRadius = 8.dp
    
    // Si hubiera referencias a colores antiguos
    val MyCustomBorderColor: Color = PrimaryColor // Changed from Theme.primaryColor
    val MyCustomTextColor: Color = TextColorBlack // Changed from Theme.textColorBlack
    val MySecondaryColor: Color = SecondaryColor // Changed from Theme.secondaryColor

    // Podría contener estilos de texto personalizados que usen tipografía
    // val CustomTitleStyle = MaterialTheme.typography.titleLarge.copy(color = MyCustomTextColor)
    // Para esto, necesitarías `MaterialTheme` en un @Composable
}
