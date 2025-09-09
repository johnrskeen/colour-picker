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
import com.skeensystems.colorpicker.calculateTextColour
import com.skeensystems.colorpicker.ui.saved.SavedColoursViewModel
import com.skeensystems.colorpicker.ui.saved.animateDetailsView
import kotlinx.coroutines.launch

@Composable
fun ColourDetails(
    viewModel: SavedColoursViewModel = viewModel(LocalActivity.current as ComponentActivity),
    topPaddingPx: Float,
) {
    val scope = rememberCoroutineScope()

    var visible by remember { mutableStateOf(false) }

    val x = remember { Animatable(0f) }
    val y = remember { Animatable(0f) }
    val width = remember { Animatable(0f) }
    val height = remember { Animatable(0f) }

    val visibilityStatus by remember { viewModel.visibilityStatus }
    var activeStatus by remember { mutableStateOf<VisibilityStatus.Show?>(null) }

    val colour =
        activeStatus?.let { status ->
            Color(status.savedColour.getR(), status.savedColour.getG(), status.savedColour.getB())
        } ?: Color.Black
    val textColour = colour.calculateTextColour()

    LaunchedEffect(visibilityStatus) {
        when (val status = visibilityStatus) {
            is VisibilityStatus.Hide -> {
                animateDetailsView(
                    scope = scope,
                    animationDuration = viewModel.animationDuration,
                    x = x,
                    xTo = status.to.x,
                    y = y,
                    yTo = status.to.y,
                    width = width,
                    widthTo = viewModel.colourViewDimension.value,
                    height = height,
                    heightTo = viewModel.colourViewDimension.value,
                    afterAnimation = { activeStatus = null },
                )
            }
            is VisibilityStatus.Show -> {
                scope.launch {
                    x.snapTo(status.from.x)
                    y.snapTo(status.from.y)
                    width.snapTo(viewModel.colourViewDimension.value)
                    height.snapTo(viewModel.colourViewDimension.value)

                    activeStatus = status
                    visible = true

                    animateDetailsView(
                        scope = scope,
                        animationDuration = viewModel.animationDuration,
                        x = x,
                        xTo = 0f,
                        y = y,
                        yTo = topPaddingPx,
                        width = width,
                        widthTo = viewModel.screenWidth.value,
                        height = height,
                        heightTo = viewModel.screenHeight.value,
                    )
                }
            }
        }
    }

    activeStatus?.let {
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
                        viewModel.setVisibilityStatus(VisibilityStatus.Hide(it.from))
                    },
        ) {
            AnimatedVisibility(
                visible = width.value > viewModel.colourViewDimension.value && height.value > viewModel.colourViewDimension.value,
                enter = fadeIn(animationSpec = tween(500)),
                exit = fadeOut(),
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Text(
                        modifier = Modifier.fillMaxWidth().padding(20.dp),
                        text = it.savedColour.getClosestMatchString(),
                        textAlign = TextAlign.Center,
                        color = textColour,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Medium,
                    )
                    LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f)) {
                        items(it.savedColour.getDetailsList()) { details ->
                            ColourCodeItem(
                                type = details.first,
                                value = details.second,
                                textColour = textColour,
                            )
                        }
                        item {
                            CopyEditColourActionBar(inspectedColour = it.savedColour)
                        }
                        item {
                            RelatedColoursContainer(inspectedColour = it.savedColour)
                        }
                    }
                    ColourDetailsActionBar(
                        hideDetailsView = {
                            viewModel.setVisibilityStatus(VisibilityStatus.Hide(it.from))
                        },
                        textColour = textColour,
                    )
                }
            }
        }
    }
}
