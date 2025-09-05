package com.skeensystems.colorpicker.ui.saved

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.skeensystems.colorpicker.MainViewModel
import com.skeensystems.colorpicker.R
import com.skeensystems.colorpicker.copyToClipboard
import com.skeensystems.colorpicker.database.SavedColour
import com.skeensystems.colorpicker.sort
import com.skeensystems.colorpicker.themeColour
import com.skeensystems.colorpicker.ui.saved.expandeddetails.ColourDetails
import com.skeensystems.colorpicker.ui.saved.selectionmode.SelectionModeActionBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun ComposeView.setSavedColoursContent() {
    setContent {
        SavedColoursScreen()
    }
}

@Composable
fun SavedColoursScreen(viewModel: MainViewModel = viewModel(LocalActivity.current as ComponentActivity)) {
    var selectedItems by remember { mutableStateOf(setOf<SavedColour>()) }
    var selectionMode by remember { mutableStateOf(false) }
    val exitSelectionMode = {
        selectedItems = emptySet()
        selectionMode = false
    }

    var confirmingDelete by remember { mutableStateOf(false) }

    var sortStatus by remember { mutableStateOf(SortOptions.NEWEST_FIRST) }
    var filterStatus by remember { mutableStateOf(FilterOptions.NO_FILTER) }

    val inspectColourX = remember { Animatable(0f) }
    val inspectColourY = remember { Animatable(0f) }
    val inspectColourWidth = remember { Animatable(0f) }
    val inspectColourHeight = remember { Animatable(0f) }
    val animationDuration =
        with(LocalContext.current) {
            resources.getInteger(android.R.integer.config_mediumAnimTime)
        }

    var listColourDimension by remember { mutableStateOf(0.dp) }

    val inspectedColourState = remember { mutableStateOf<SavedColour?>(null) }

    val savedColourCoordinates = remember { mutableMapOf<Long, Offset>() }

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
            val width = maxWidth
            val height = maxHeight
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

                LazyVerticalGrid(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    columns = GridCells.Adaptive(minSize = 100.dp),
                ) {
                    items(viewModel.savedColours.sort()) {
                        ListColour(
                            savedColour = it,
                            selected = it in selectedItems,
                            animationDuration = animationDuration,
                            onDimensionMeasured = { dimension -> listColourDimension = dimension },
                            onCoordinatesDetermined = { coordinates -> savedColourCoordinates[it.getId()] = coordinates },
                            onClick = {
                                if (!selectionMode) {
                                    showDetailsView(
                                        scope = scope,
                                        inspectedColour = it,
                                        inspectedColourState = inspectedColourState,
                                        animationDuration = animationDuration,
                                        x = inspectColourX,
                                        y = inspectColourY,
                                        width = inspectColourWidth,
                                        height = inspectColourHeight,
                                        screenWidth = width,
                                        screenHeight = height,
                                        originCoordinates = savedColourCoordinates[it.getId()]!!,
                                        originDimension = listColourDimension,
                                        topPaddingPx = topPaddingPx,
                                    )
                                } else {
                                    if (it in selectedItems) {
                                        selectedItems = selectedItems.minus(it)
                                        if (selectedItems.isEmpty()) selectionMode = false
                                    } else {
                                        selectedItems = selectedItems.plus(it)
                                    }
                                }
                            },
                            onLongClick = {
                                selectionMode = true
                                selectedItems = selectedItems.plus(it)
                            },
                        )
                    }
                }

                Box(modifier = Modifier.animateContentSize()) {
                    if (selectionMode) {
                        SelectionModeActionBar(
                            onCancel = {
                                exitSelectionMode()
                            },
                            onCopy = {
                                scope.launch {
                                    selectedItems.copyToClipboard(clipboardManager = clipboardManager)
                                    exitSelectionMode()
                                }
                            },
                            onSetFavouriteStatus = { favourite ->
                                selectedItems.map { it.setFavorite(favourite) }
                                exitSelectionMode()
                            },
                            onDelete = {
                                confirmingDelete = true
                            },
                        )
                    }
                }
            }

            inspectedColourState.value?.let {
                ColourDetails(
                    inspectedColourState = inspectedColourState,
                    inspectedColour = it,
                    topPaddingPx = topPaddingPx,
                    animationDuration = animationDuration,
                    x = inspectColourX,
                    y = inspectColourY,
                    width = inspectColourWidth,
                    height = inspectColourHeight,
                    originCoordinates = savedColourCoordinates[it.getId()]!!,
                    originDimension = listColourDimension,
                )
            }

            AnimatedVisibility(
                visible = confirmingDelete,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut(),
            ) {
                AlertDialog(
                    onDismissRequest = { confirmingDelete = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                selectedItems.forEach { viewModel.removeColour(it) }
                                confirmingDelete = false
                                exitSelectionMode()
                            },
                            colors = ButtonDefaults.textButtonColors(contentColor = Color.Red),
                        ) { Text("Delete") }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { confirmingDelete = false },
                            colors = ButtonDefaults.textButtonColors(contentColor = themeColour(R.attr.defaultTextColour)),
                        ) { Text("Cancel") }
                    },
                    title = { Text("Delete all currently selected items?") },
                    text = { Text("This action cannot be undone.") },
                    containerColor = themeColour(R.attr.mainColour),
                )
            }
        }
    }
}

// TODO extract this and merge with the hide details view function
fun showDetailsView(
    scope: CoroutineScope,
    inspectedColour: SavedColour,
    inspectedColourState: MutableState<SavedColour?>,
    animationDuration: Int,
    x: Animatable<Float, AnimationVector1D>,
    y: Animatable<Float, AnimationVector1D>,
    width: Animatable<Float, AnimationVector1D>,
    height: Animatable<Float, AnimationVector1D>,
    screenWidth: Dp,
    screenHeight: Dp,
    originCoordinates: Offset,
    originDimension: Dp,
    topPaddingPx: Float,
) {
    if (inspectedColourState.value == null) {
        scope.launch {
            x.snapTo(originCoordinates.x)
            y.snapTo(originCoordinates.y)
            width.snapTo(originDimension.value)
            height.snapTo(originDimension.value)

            inspectedColourState.value = inspectedColour

            launch {
                x.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = animationDuration),
                )
            }
            launch {
                y.animateTo(
                    targetValue = topPaddingPx,
                    animationSpec = tween(durationMillis = animationDuration),
                )
            }
            launch {
                width.animateTo(
                    targetValue = screenWidth.value,
                    animationSpec = tween(durationMillis = animationDuration),
                )
            }
            launch {
                height.animateTo(
                    targetValue = screenHeight.value,
                    animationSpec = tween(durationMillis = animationDuration),
                )
            }
        }
    }
}
