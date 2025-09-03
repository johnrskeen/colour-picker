package com.skeensystems.colorpicker.ui.saved.expandeddetails

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.skeensystems.colorpicker.calculateTextColour
import com.skeensystems.colorpicker.database.SavedColour
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

@Composable
fun ColourDetails(
    inspectedColourState: MutableState<SavedColour?>,
    inspectedColour: SavedColour,
    topPaddingPx: Float,
    animationDuration: Int,
    x: Animatable<Float, AnimationVector1D>,
    y: Animatable<Float, AnimationVector1D>,
    width: Animatable<Float, AnimationVector1D>,
    height: Animatable<Float, AnimationVector1D>,
    originCoordinates: Offset,
    originDimension: Dp,
) {
    val scope = rememberCoroutineScope()
    val colour = Color(inspectedColour.getR(), inspectedColour.getG(), inspectedColour.getB())
    val textColour = colour.calculateTextColour()

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
                    hideDetailsView(scope, inspectedColourState, animationDuration, x, y, width, height, originCoordinates, originDimension)
                },
    ) {
        AnimatedVisibility(
            visible = width.value > originDimension.value && height.value > originDimension.value,
            enter = fadeIn(animationSpec = tween(500)),
            exit = fadeOut(),
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Text(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    text = inspectedColour.getClosestMatchString(),
                    textAlign = TextAlign.Center,
                    color = textColour,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Medium,
                )
                LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    items(inspectedColour.getDetailsList()) {
                        ColourCodeItem(type = it.first, value = it.second, textColour = textColour)
                    }
                    item {
                        CopyEditColourActionBar(inspectedColour = inspectedColour)
                    }
                    item {
                        RelatedColoursContainer(inspectedColour = inspectedColour)
                    }
                }
                ColourDetailsActionBar(
                    hideDetailsView = {
                        hideDetailsView(
                            scope,
                            inspectedColourState,
                            animationDuration,
                            x,
                            y,
                            width,
                            height,
                            originCoordinates,
                            originDimension,
                        )
                    },
                    textColour = textColour,
                )
            }
        }
    }
}

fun hideDetailsView(
    scope: CoroutineScope,
    inspectedColourState: MutableState<SavedColour?>,
    animationDuration: Int,
    x: Animatable<Float, AnimationVector1D>,
    y: Animatable<Float, AnimationVector1D>,
    width: Animatable<Float, AnimationVector1D>,
    height: Animatable<Float, AnimationVector1D>,
    originCoordinates: Offset,
    originDimension: Dp,
) {
    scope.launch {
        awaitAll(
            async {
                x.animateTo(
                    targetValue = originCoordinates.x,
                    animationSpec = tween(durationMillis = animationDuration),
                )
            },
            async {
                y.animateTo(
                    targetValue = originCoordinates.y,
                    animationSpec = tween(durationMillis = animationDuration),
                )
            },
            async {
                width.animateTo(
                    targetValue = originDimension.value,
                    animationSpec = tween(durationMillis = animationDuration),
                )
            },
            async {
                height.animateTo(
                    targetValue = originDimension.value,
                    animationSpec = tween(durationMillis = animationDuration),
                )
            },
        )
        inspectedColourState.value = null
    }
}
