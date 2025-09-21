package com.skeensystems.colorpicker.ui.picker.slider

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SliderBackground() {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(vertical = 8.dp)
                .background(
                    brush =
                        Brush.linearGradient(
                            colors =
                                listOf(Color.Red, Color.Yellow, Color.Green, Color.Cyan, Color.Blue, Color.Magenta, Color.Red),
                        ),
                    shape = RoundedCornerShape(20.dp),
                ),
    )
}
