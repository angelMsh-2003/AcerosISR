package com.example.acerosisr.Funtion

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.acerosisr.ui.theme.AcerosISRTheme
import com.example.acerosisr.ui.theme.PrimaryColor
import com.example.acerosisr.ui.theme.SecondaryColor
import com.example.acerosisr.ui.theme.ErrorColor
import com.example.acerosisr.ui.theme.TextColorWhite
import com.example.acerosisr.ui.theme.TextColorBlack
import androidx.compose.foundation.layout.fillMaxWidth // Added import

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorsSegmentedButton(options: List<String>, selectedIndex: Int, onSegmentSelected: (Int) -> Unit) {
    MultiChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        options.forEachIndexed { index, label ->
            val isSelected = index == selectedIndex
            val segmentColor = when (label) {
                "Pendiente" -> ErrorColor // Changed from Theme.errorColor
                "En proceso" -> PrimaryColor // Changed from Theme.primaryColor
                "Completada" -> SecondaryColor // Changed from Theme.secondaryColor
                else -> Color.Gray
            }
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                // Removed onClick and selected as they are redundant/deprecated for this use case in M3 SegmentedButton
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = segmentColor,
                    activeContentColor = TextColorWhite, // Changed from Theme.textColorWhite
                    inactiveContainerColor = Color.LightGray,
                    inactiveContentColor = TextColorBlack // Changed from Theme.textColorBlack
                ),
                checked = isSelected,
                onCheckedChange = { onSegmentSelected(index) } // This handles the click and selection state
            ) {
                Text(label)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ColorsSegmentedButtonPreview() {
    AcerosISRTheme {
        var selected by remember { mutableStateOf(0) }
        ColorsSegmentedButton(options = listOf("Pendiente", "En proceso", "Completada"), selectedIndex = selected) { newIndex ->
            selected = newIndex
        }
    }
}
