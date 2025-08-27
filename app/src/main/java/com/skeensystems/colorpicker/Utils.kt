package com.skeensystems.colorpicker

import android.util.TypedValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

fun Color.calculateTextColour(): Color =
    // Counting the perceptive luminance - human eye favors green color
    (0.299 * red + 0.587 * green + 0.114 * blue).let { luminance ->
        if (luminance > 0.5) {
            Color.Black // bright colors - black font
        } else {
            Color.LightGray // dark colors - white font
        }
    }

fun Color.getClosestColour(): String = "Red"

fun Color.getHexString(): String = String.format("#%02X%02X%02X", (red * 255).toInt(), (green * 255).toInt(), (blue * 255).toInt())

@Composable
fun themeColour(attrResId: Int): Color {
    val context = LocalContext.current
    val typedValue = TypedValue()
    context.theme.resolveAttribute(attrResId, typedValue, true)
    return Color(typedValue.data)
}
