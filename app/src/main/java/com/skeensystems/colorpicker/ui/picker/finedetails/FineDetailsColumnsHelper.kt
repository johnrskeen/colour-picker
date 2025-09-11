package com.skeensystems.colorpicker.ui.picker.finedetails

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.skeensystems.colorpicker.ui.picker.B
import com.skeensystems.colorpicker.ui.picker.G
import com.skeensystems.colorpicker.ui.picker.H
import com.skeensystems.colorpicker.ui.picker.PickerViewModel
import com.skeensystems.colorpicker.ui.picker.R
import com.skeensystems.colorpicker.ui.picker.S
import com.skeensystems.colorpicker.ui.picker.V

@Composable
fun getFineDetailsColumns(
    viewModel: PickerViewModel = viewModel(LocalActivity.current as ComponentActivity),
    colourSystem: ColourSystem,
): List<FineDetailsEntrySpec> {
    val h by viewModel.h
    val s by viewModel.s
    val v by viewModel.v
    val r by viewModel.r
    val g by viewModel.g
    val b by viewModel.b

    return when (colourSystem) {
        ColourSystem.RGB ->
            listOf(
                FineDetailsEntrySpec(componentType = R, value = r),
                FineDetailsEntrySpec(componentType = G, value = g),
                FineDetailsEntrySpec(componentType = B, value = b),
            )
        ColourSystem.HEX ->
            listOf(
                FineDetailsEntrySpec(componentType = R, value = r),
                FineDetailsEntrySpec(componentType = G, value = g),
                FineDetailsEntrySpec(componentType = B, value = b),
            )
        ColourSystem.HSV ->
            listOf(
                FineDetailsEntrySpec(componentType = H, value = h),
                FineDetailsEntrySpec(componentType = S, value = s),
                FineDetailsEntrySpec(componentType = V, value = v),
            )
    }
}
