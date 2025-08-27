package com.skeensystems.colorpicker

import androidx.compose.ui.graphics.Color

fun Color.calculateTextColour(): Color =
    // Counting the perceptive luminance - human eye favors green color
    (0.299 * red + 0.587 * green + 0.114 * blue).let { luminance ->
        if (luminance > 0.5) {
            Color.Black // bright colors - black font
        } else {
            Color.White // dark colors - white font
        }
    }
