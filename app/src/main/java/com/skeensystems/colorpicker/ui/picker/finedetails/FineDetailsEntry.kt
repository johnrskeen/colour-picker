package com.skeensystems.colorpicker.ui.picker.finedetails

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.skeensystems.colorpicker.R
import com.skeensystems.colorpicker.themeColour
import com.skeensystems.colorpicker.ui.picker.PickerViewModel
import com.skeensystems.colorpicker.ui.picker.format

@Composable
fun FineDetailsEntry(
    modifier: Modifier = Modifier,
    viewModel: PickerViewModel = viewModel(LocalActivity.current as ComponentActivity),
    details: FineDetailsEntrySpec,
    colourSystem: FineDetailsOptions,
) {
    val textColour = themeColour(R.attr.defaultTextColour)
    val id = colourSystem.toString() + details.componentType.toString()
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
                    FineDetailsOptions.HEX
                ) {
                    String.format("%02X", it)
                } else {
                    it.toString()
                }
            },
        colors =
            OutlinedTextFieldDefaults.colors(
                focusedTextColor = textColour,
                unfocusedTextColor = textColour,
                focusedBorderColor = textColour,
                focusedLabelColor = textColour,
            ),
        onValueChange = { input ->
            viewModel.setLastUpdateId(id)
            val newValue =
                input.filter { it.isDigit() }.let {
                    if (it == "") 0.000001f else it.toFloat().adjust(details.componentType)
                }
            viewModel.updateValue(details.componentType, newValue)
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
    )
}
