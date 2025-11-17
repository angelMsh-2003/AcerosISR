package com.example.acerosisr.Funtion

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.acerosisr.ui.theme.AcerosISRTheme
import com.example.acerosisr.ui.theme.PrimaryColor // Import for theme colors
import com.example.acerosisr.ui.theme.TextColorWhite // Import for theme colors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(showDialog: Boolean, onDismiss: () -> Unit, onDateSelected: (Long?) -> Unit) {
    if (showDialog) {
        val datePickerState = rememberDatePickerState()

        DatePickerDialog(
            onDismissRequest = { onDismiss() },
            confirmButton = {
                TextButton(onClick = { 
                    onDateSelected(datePickerState.selectedDateMillis)
                    onDismiss()
                }) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { onDismiss() }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

// Helper function to format millis to string
fun convertMillisToDate(millis: Long?): String {
    return millis?.let {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        formatter.format(Date(it))
    } ?: "Fecha no seleccionada"
}

@Preview(showBackground = true)
@Composable
fun DatePickerModalPreview() {
    AcerosISRTheme {
        DatePickerModal(showDialog = true, onDismiss = {}, onDateSelected = {}) 
    }
}
