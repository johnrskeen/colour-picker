package com.skeensystems.colorpicker.ui.picker.finedetails

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.skeensystems.colorpicker.ui.picker.PickerViewModel
import com.skeensystems.colorpicker.ui.picker.adjust
import com.skeensystems.colorpicker.ui.picker.format

@Composable
fun FineDetailsEntry(
    modifier: Modifier = Modifier,
    viewModel: PickerViewModel = viewModel(LocalActivity.current as ComponentActivity),
    details: FineDetailsEntrySpec,
    colourSystem: ColourSystem,
) {
    val name = details.componentType.toString()
    val id = colourSystem.toString() + name
    OutlinedTextField(
        modifier =
            modifier.onFocusChanged { focusState ->
                if (!focusState.isFocused) {
                    if (details.value == 0.000001f) {
                        viewModel.updateValue(details.componentType, 0f)
                    }
                }
            },
        value =
            details.value.format(details.componentType, id, viewModel.lastUpdateId.value).let {
                if (it == -1) {
                    ""
                } else if (colourSystem ==
                    ColourSystem.HEX
                ) {
                    String.format("%X", it)
                } else {
                    it.toString()
                }
            },
        label = { Text(name) },
        textStyle = TextStyle(textAlign = TextAlign.Center),
        onValueChange = { input ->
            viewModel.setLastUpdateId(id)
            val newValue =
                input.uppercase().applyFilter(colourSystem).let {
                    if (it == "") {
                        0.000001f
                    } else {
                        (
                            if (colourSystem == ColourSystem.HEX) {
                                it.toInt(radix = 16).toFloat()
                            } else {
                                it.toFloat()
                            }
                        ).adjust(details.componentType)
                    }
                }
            viewModel.updateValue(details.componentType, newValue)
        },
        keyboardOptions =
            if (colourSystem == ColourSystem.HEX) {
                KeyboardOptions(keyboardType = KeyboardType.Ascii)
            } else {
                KeyboardOptions(keyboardType = KeyboardType.Number)
            },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
    )
}

fun String.applyFilter(colourSystem: ColourSystem): String =
    when (colourSystem) {
        ColourSystem.HEX -> {
            val hexCharacters = listOf('A', 'B', 'C', 'D', 'E', 'F')
            this.filter { it.isDigit() || it in hexCharacters }
        }
        else -> this.filter { it.isDigit() }
    }
