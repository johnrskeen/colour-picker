package com.skeensystems.colorpicker.ui.picker.finedetails

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun FineDetailsContainer(modifier: Modifier = Modifier) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(modifier = modifier) {
        TabRow(selectedTabIndex = selectedTab, containerColor = MaterialTheme.colorScheme.background) {
            listOf(
                ColourSystem.RGB,
                ColourSystem.HEX,
                ColourSystem.HSV,
            ).forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title.name) },
                )
            }
        }
        when (selectedTab) {
            0 -> FineDetailsPage(modifier = Modifier.weight(1f), colourSystem = ColourSystem.RGB)
            1 -> FineDetailsPage(modifier = Modifier.weight(1f), colourSystem = ColourSystem.HEX)
            2 -> FineDetailsPage(modifier = Modifier.weight(1f), colourSystem = ColourSystem.HSV)
        }
    }
}
