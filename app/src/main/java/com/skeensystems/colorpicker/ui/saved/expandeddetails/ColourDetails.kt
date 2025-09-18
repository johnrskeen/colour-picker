package com.skeensystems.colorpicker.ui.saved.expandeddetails

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.skeensystems.colorpicker.MainViewModel
import com.skeensystems.colorpicker.calculateTextColour
import com.skeensystems.colorpicker.database.SavedColour
import com.skeensystems.colorpicker.ui.saved.ConfirmDelete
import com.skeensystems.colorpicker.ui.saved.SavedColoursViewModel
import com.skeensystems.colorpicker.ui.saved.animateDetailsView
import kotlinx.coroutines.launch

@Composable
fun ColourDetails(
    mainViewModel: MainViewModel = viewModel(LocalActivity.current as ComponentActivity),
    localViewModel: SavedColoursViewModel = viewModel(LocalActivity.current as ComponentActivity),
    topPaddingPx: Float,
) {
    val scope = rememberCoroutineScope()

    var visible by remember { mutableStateOf(false) }
    var confirmingDelete by remember { mutableStateOf(false) }

    val x = remember { Animatable(0f) }
    val y = remember { Animatable(0f) }
    val width = remember { Animatable(0f) }
    val height = remember { Animatable(0f) }

    val visibilityStatus by remember { localViewModel.visibilityStatus }
    var activeStatus by remember { mutableStateOf<VisibilityStatus.Show?>(null) }

    // TODO make default saved colour object for both of these in this file
    val colour =
        activeStatus?.let { status ->
            val savedColour = mainViewModel.savedColours.find { it.id == status.savedColourId }
            savedColour?.let { Color(it.r, it.g, it.b) } ?: Color(0, 0, 0)
        } ?: Color.Black
    val textColour = colour.calculateTextColour()

    LaunchedEffect(visibilityStatus) {
        when (val status = visibilityStatus) {
            is VisibilityStatus.Hide -> {
                animateDetailsView(
                    scope = scope,
                    animationDuration = localViewModel.animationDuration,
                    x = x,
                    xTo = status.to.x,
                    y = y,
                    yTo = status.to.y,
                    width = width,
                    widthTo = localViewModel.colourViewDimension.value,
                    height = height,
                    heightTo = localViewModel.colourViewDimension.value,
                    afterAnimation = { activeStatus = null },
                )
            }
            is VisibilityStatus.Show -> {
                scope.launch {
                    x.snapTo(status.from.x)
                    y.snapTo(status.from.y)
                    width.snapTo(localViewModel.colourViewDimension.value)
                    height.snapTo(localViewModel.colourViewDimension.value)

                    activeStatus = status
                    visible = true

                    animateDetailsView(
                        scope = scope,
                        animationDuration = localViewModel.animationDuration,
                        x = x,
                        xTo = 0f,
                        y = y,
                        yTo = topPaddingPx,
                        width = width,
                        widthTo = localViewModel.screenWidth.value,
                        height = height,
                        heightTo = localViewModel.screenHeight.value,
                    )
                }
            }
        }
    }

    activeStatus?.let { status ->
        val savedColour = mainViewModel.savedColours.find { it.id == status.savedColourId }
        savedColour?.let {
            Box(
                modifier =
                    Modifier
                        .offset {
                            IntOffset(
                                x = x.value.toInt(),
                                y = (y.value - topPaddingPx).toInt(),
                            )
                        }.size(width = width.value.dp, height = height.value.dp)
                        .background(color = colour, shape = RoundedCornerShape(10.dp))
                        .clickable {
                            localViewModel.setVisibilityStatus(VisibilityStatus.Hide(status.from))
                        },
            ) {
                AnimatedVisibility(
                    visible =
                        width.value > localViewModel.colourViewDimension.value && height.value > localViewModel.colourViewDimension.value,
                    enter = fadeIn(animationSpec = tween(500)),
                    exit = fadeOut(),
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            Text(
                                modifier = Modifier.fillMaxWidth().padding(20.dp),
                                text = it.getClosestMatchString(),
                                textAlign = TextAlign.Center,
                                color = textColour,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Medium,
                            )
                            LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f)) {
                                items(it.getDetailsList()) { details ->
                                    ColourCodeItem(
                                        type = details.first,
                                        value = details.second,
                                        textColour = textColour,
                                    )
                                }
                                item {
                                    CopyEditColourActionBar(inspectedColour = it)
                                }
                                item {
                                    RelatedColoursContainer(inspectedColour = it)
                                }
                            }
                            ColourDetailsActionBar(
                                favouriteStatus = it.favourite,
                                onHideDetailsView = {
                                    localViewModel.setVisibilityStatus(VisibilityStatus.Hide(status.from))
                                },
                                onChangeFavouriteStatus = {
                                    mainViewModel.setFavouriteStatus(it, it.favourite)
                                },
                                onDelete = { confirmingDelete = true },
                                textColour = textColour,
                            )
                        }
                        ConfirmDelete(
                            title = "Delete colour?",
                            confirmingDelete = confirmingDelete,
                            onDelete = {
                                mainViewModel.deleteColour(it)
                                localViewModel.setVisibilityStatus(VisibilityStatus.Hide(status.from))
                            },
                            exitConfirmingDeleteMode = { confirmingDelete = false },
                        )
                    }
                }
            }
        }
    }
}
