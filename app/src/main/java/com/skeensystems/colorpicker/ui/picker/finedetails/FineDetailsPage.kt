package com.skeensystems.colorpicker.ui.picker.finedetails

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.skeensystems.colorpicker.ui.picker.PickerViewModel

@Composable
fun FineDetailsPage(
    modifier: Modifier = Modifier,
    viewModel: PickerViewModel = viewModel(LocalActivity.current as ComponentActivity),
    colourSystem: ColourSystem,
) {
    val columns = getFineDetailsColumns(colourSystem = colourSystem)

    Row(modifier = modifier.padding(10.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        columns.forEach { column ->
            Column(modifier = Modifier.fillMaxHeight().weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                AdjustButtons(
                    onAdjust = { change -> viewModel.changeValue(column.componentType, change) },
                )
                FineDetailsEntry(
                    modifier = Modifier.weight(1f),
                    details = column,
                    colourSystem = colourSystem,
                )
            }
        }
    }
}
