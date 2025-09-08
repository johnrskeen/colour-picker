package com.skeensystems.colorpicker.ui.picker

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush.Companion.horizontalGradient
import androidx.compose.ui.graphics.Brush.Companion.verticalGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.skeensystems.colorpicker.clamp
import kotlin.math.roundToInt

@Composable
fun GradientContainer(
    modifier: Modifier = Modifier,
    viewModel: PickerViewModel = viewModel(LocalActivity.current as ComponentActivity),
) {
    var touchPosition by remember { mutableStateOf(IntOffset(0, 0)) }
    val cornerColour by viewModel.cornerColour.collectAsState()

    var widthPx by remember { mutableIntStateOf(0) }
    var heightPx by remember { mutableIntStateOf(0) }

    LaunchedEffect(touchPosition) {
        viewModel.updateS(1f * touchPosition.x / widthPx.toFloat())
        viewModel.updateV(1f - (1f * touchPosition.y / heightPx.toFloat()))
    }

    Box(modifier = modifier.padding(20.dp)) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(
                        brush =
                            horizontalGradient(
                                colors = listOf(Color.White, cornerColour),
                            ),
                    ),
        )
        BoxWithConstraints(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(
                        brush =
                            verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black),
                            ),
                    ).pointerInput(Unit) {
                        detectTapGestures { offset ->
                            touchPosition = offset.clamp(widthPx, heightPx)
                        }
                    }.pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                touchPosition = offset.clamp(widthPx, heightPx)
                            },
                            onDrag = { change, _ ->
                                change.consume()
                                touchPosition = change.position.clamp(widthPx, heightPx)
                            },
                        )
                    },
        ) {
            widthPx = with(LocalDensity.current) { maxWidth.toPx() }.roundToInt()
            heightPx = with(LocalDensity.current) { maxHeight.toPx() }.roundToInt()
            PickerTarget(touchPosition = touchPosition)
        }
    }
}
