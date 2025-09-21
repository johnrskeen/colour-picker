package com.skeensystems.colorpicker.ui.saved.sortandfilter

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.skeensystems.colorpicker.R
import com.skeensystems.colorpicker.themeColour
import kotlin.enums.EnumEntries

// TODO the theming done here might be extractable to a main theme
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> DropDownMenu(
    modifier: Modifier = Modifier,
    title: String,
    options: EnumEntries<T>,
    onOptionSelected: (T) -> Unit,
    selectedOption: T,
) where T : Enum<T>, T : SortAndFilterOptions {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        OutlinedTextField(
            value = selectedOption.label,
            onValueChange = {},
            readOnly = true,
            label = { Text(title) },
            textStyle =
                TextStyle(
                    fontSize = 16.sp,
                ),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors =
                OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = themeColour(R.attr.defaultTextColour),
                    focusedLabelColor = themeColour(R.attr.defaultTextColour),
                ),
            modifier =
                Modifier
                    .menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true)
                    .fillMaxWidth(),
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(text = option.label, fontSize = 16.sp, fontWeight = FontWeight.Normal) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    },
                )
            }
        }
    }
}
