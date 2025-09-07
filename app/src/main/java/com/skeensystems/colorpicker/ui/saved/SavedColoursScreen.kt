package com.skeensystems.colorpicker.ui.saved

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.skeensystems.colorpicker.MainViewModel
import com.skeensystems.colorpicker.R
import com.skeensystems.colorpicker.copyToClipboard
import com.skeensystems.colorpicker.themeColour
import com.skeensystems.colorpicker.ui.saved.expandeddetails.ColourDetails
import com.skeensystems.colorpicker.ui.saved.selectionmode.SelectionModeActionBar
import com.skeensystems.colorpicker.ui.saved.sortandfilter.DropDownMenu
import com.skeensystems.colorpicker.ui.saved.sortandfilter.FilterOptions
import com.skeensystems.colorpicker.ui.saved.sortandfilter.SortOptions
import kotlinx.coroutines.launch

fun ComposeView.setSavedColoursContent() {
    setContent {
        SavedColoursScreen()
    }
}

@Composable
fun SavedColoursScreen(
    mainViewModel: MainViewModel = viewModel(LocalActivity.current as ComponentActivity),
    localViewModel: SavedColoursViewModel = viewModel(LocalActivity.current as ComponentActivity),
) {
    localViewModel.animationDuration =
        with(LocalContext.current) {
            resources.getInteger(android.R.integer.config_mediumAnimTime)
        }

    val selectedItems = localViewModel.selectedItems
    val selectionMode by localViewModel.selectingMode

    var confirmingDelete by remember { mutableStateOf(false) }

    var sortStatus by remember { mutableStateOf(SortOptions.NEWEST_FIRST) }
    var filterStatus by remember { mutableStateOf(FilterOptions.NO_FILTER) }

    val clipboardManager = LocalClipboard.current
    val scope = rememberCoroutineScope()

    Scaffold { paddingValues ->
        val topPaddingDp = paddingValues.calculateTopPadding()
        val topPaddingPx = with(LocalDensity.current) { topPaddingDp.toPx() }
        BoxWithConstraints(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(themeColour(R.attr.mainColour))
                    .padding(top = topPaddingDp),
        ) {
            localViewModel.screenWidth = maxWidth
            localViewModel.screenHeight = maxHeight
            Column {
                Text(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                    text = "Saved Colours",
                    textAlign = TextAlign.Center,
                    color = themeColour(R.attr.defaultTextColour),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Medium,
                )

                Row(modifier = Modifier.fillMaxWidth().padding(10.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    DropDownMenu(
                        modifier = Modifier.weight(1f),
                        title = "Sort",
                        options = SortOptions.entries,
                        onOptionSelected = { selectedOption -> sortStatus = selectedOption },
                        selectedOption = sortStatus,
                    )
                    DropDownMenu(
                        modifier = Modifier.weight(1f),
                        title = "Filter",
                        options = FilterOptions.entries,
                        onOptionSelected = { selectedOption -> filterStatus = selectedOption },
                        selectedOption = filterStatus,
                    )
                }

                SavedColoursGrid(
                    modifier = Modifier.fillMaxWidth().weight(1f).padding(end = 1.dp),
                    sortStatus = sortStatus,
                    filterStatus = filterStatus,
                )

                Box(modifier = Modifier.animateContentSize()) {
                    if (selectionMode) {
                        SelectionModeActionBar(
                            onCancel = {
                                localViewModel.exitSelectingMode()
                            },
                            onCopy = {
                                scope.launch {
                                    selectedItems.copyToClipboard(clipboardManager = clipboardManager)
                                    localViewModel.exitSelectingMode()
                                }
                            },
                            onSetFavouriteStatus = { favourite ->
                                selectedItems.map { it.setFavorite(favourite) }
                                localViewModel.exitSelectingMode()
                            },
                            onDelete = {
                                confirmingDelete = true
                            },
                        )
                    }
                }
            }

            ColourDetails(
                topPaddingPx = topPaddingPx,
            )

            ConfirmDelete(
                confirmingDelete = confirmingDelete,
                onDelete = {
                    selectedItems.forEach { mainViewModel.removeColour(it) }
                    localViewModel.exitSelectingMode()
                },
                exitConfirmingDeleteMode = { confirmingDelete = false },
            )
        }
    }
}
