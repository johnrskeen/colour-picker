package com.skeensystems.colorpicker.ui.picker.finedetails

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.skeensystems.colorpicker.themeColour
import com.skeensystems.colorpicker.ui.picker.PickerViewModel

@Composable
fun FineDetailsPage(
    modifier: Modifier = Modifier,
    viewModel: PickerViewModel = viewModel(LocalActivity.current as ComponentActivity),
    colourSystem: FineDetailsOptions,
) {
    val columns = getFineDetailsColumns(colourSystem = colourSystem)

    val textColour = themeColour(com.skeensystems.colorpicker.R.attr.defaultTextColour)
    Row(modifier = modifier.fillMaxSize()) {
        columns.forEach { column ->
            Column(modifier = Modifier.fillMaxHeight().weight(1f), verticalArrangement = Arrangement.Center) {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = column.componentType.toString(),
                        color = textColour,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
                Column(modifier = Modifier.fillMaxWidth().weight(2f).padding(10.dp)) {
                    AdjustButtons(
                        modifier = Modifier.weight(0.5f),
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
}
