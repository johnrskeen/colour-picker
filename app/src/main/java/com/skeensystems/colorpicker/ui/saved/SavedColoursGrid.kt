package com.skeensystems.colorpicker.ui.saved

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.skeensystems.colorpicker.MainViewModel
import com.skeensystems.colorpicker.filter
import com.skeensystems.colorpicker.sort

/*
This uses a hack to stop scrolling to the end when elements are reordered.
This happens because the scroll position is tracked based on the index visible,
so we need a fixed index that doesn't move at the start side of the screen.
A very small column of invisible items is used, which is why the logic for
calculating the screen width and columns, and setting the min column size
to 1dp is needed.
*/
@Composable
fun SavedColoursGrid(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel = viewModel(LocalActivity.current as ComponentActivity),
    localViewModel: SavedColoursViewModel = viewModel(LocalActivity.current as ComponentActivity),
    sortStatus: SortOptions,
    filterStatus: FilterOptions,
) {
    val gridState = rememberLazyGridState()
    val density = LocalDensity.current

    val displayOrder = mainViewModel.savedColours.filter(filterStatus).sort(sortStatus)
    val placeHolderData = (-1 downTo -displayOrder.size).toList()

    val selectedItems = localViewModel.selectedItems
    val selectionMode by localViewModel.selectingMode

    val spanCount by remember {
        derivedStateOf { gridState.layoutInfo.maxSpan }
    }
    var columns by remember { mutableIntStateOf(4) }

    val itemSpan = ((spanCount - 1) / columns).coerceAtLeast(1)
    val firstSpan = (spanCount - (itemSpan * columns)).coerceAtLeast(1)

    LazyVerticalGrid(
        state = gridState,
        modifier =
            modifier.onGloballyPositioned { coordinates ->
                columns =
                    with(density) {
                        coordinates.size.width
                            .toDp()
                            .value / 100
                    }.toInt()
            },
        columns = GridCells.Adaptive(minSize = 1.dp),
    ) {
        val chunked = displayOrder.chunked(columns)
        chunked.forEachIndexed { index, row ->
            item(key = placeHolderData[index], span = { GridItemSpan(firstSpan) }) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .background(Color(0xFFE0E0E0)),
                )
            }

            items(row, key = { it.getId() }, span = { GridItemSpan(itemSpan) }) {
                ListColour(
                    modifier = Modifier.animateItem(),
                    savedColour = it,
                    selected = it in selectedItems,
                    animationDuration = localViewModel.animationDuration,
                    onDimensionMeasured = { dimension -> localViewModel.colourViewDimension = dimension },
                    onCoordinatesDetermined = { coordinates -> localViewModel.savedColourCoordinates[it.getId()] = coordinates },
                    onClick = {
                        if (!selectionMode) {
                            localViewModel.setVisibilityStatus(
                                VisibilityStatus.Show(localViewModel.savedColourCoordinates[it.getId()]!!, it),
                            )
                        } else {
                            if (it in selectedItems) {
                                localViewModel.removeSelectedItem(it)
                            } else {
                                localViewModel.addSelectedItem(it)
                            }
                        }
                    },
                    onLongClick = {
                        localViewModel.enterSelectingMode(it)
                    },
                )
            }
        }
    }
}
