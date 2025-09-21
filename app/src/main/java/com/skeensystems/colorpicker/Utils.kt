package com.skeensystems.colorpicker

import android.content.ClipData
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.toClipEntry
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import com.skeensystems.colorpicker.database.Colour

fun Color.calculateTextColour(): Color =
    // Counting the perceptive luminance - human eye favors green color
    (0.299 * red + 0.587 * green + 0.114 * blue).let { luminance ->
        if (luminance > 0.4) {
            Color.Black // bright colors - black font
        } else {
            Color.LightGray // dark colors - white font
        }
    }

fun Color.getClosestColour(viewModel: MainViewModel): String =
    viewModel.getClosestColour(
        (red * 255).toInt(),
        (green * 255).toInt(),
        (blue * 255).toInt(),
    )

fun Color.getHexString(): String = String.format("#%02X%02X%02X", (red * 255).toInt(), (green * 255).toInt(), (blue * 255).toInt())

suspend fun Colour.copyToClipboard(clipboardManager: Clipboard) {
    val clip = ClipData.newPlainText("Colour", generateCopyString()).toClipEntry()
    clipboardManager.setClipEntry(clip)
}

suspend fun Set<Colour>.copyToClipboard(clipboardManager: Clipboard) {
    val text = this.joinToString(separator = "\n\n") { it.generateCopyString() }
    val clip = ClipData.newPlainText("Colour", text).toClipEntry()
    clipboardManager.setClipEntry(clip)
}

@Composable
fun measureTextWidth(
    text: String,
    style: TextStyle,
): Dp {
    val textMeasurer = rememberTextMeasurer()
    val widthInPixels = textMeasurer.measure(text, style).size.width * 1.01f
    return with(LocalDensity.current) { widthInPixels.toDp() }
}

fun hsvToRGB(
    h: Float,
    s: Float,
    v: Float,
): Triple<Float, Float, Float> {
    val c = v * s
    val x = c * (1 - kotlin.math.abs((h / 60f) % 2 - 1))
    val m = v - c

    val (r, g, b) =
        when {
            h < 60f -> Triple(c, x, 0f)
            h < 120f -> Triple(x, c, 0f)
            h < 180f -> Triple(0f, c, x)
            h < 240f -> Triple(0f, x, c)
            h < 300f -> Triple(x, 0f, c)
            else -> Triple(c, 0f, x)
        }

    return Triple(
        (r + m).coerceIn(0f, 1f),
        (g + m).coerceIn(0f, 1f),
        (b + m).coerceIn(0f, 1f),
    )
}

fun rgbToHSV(
    r: Float,
    g: Float,
    b: Float,
): Triple<Float, Float, Float> {
    val max = maxOf(r, g, b)
    val min = minOf(r, g, b)
    val delta = max - min

    val h =
        when {
            delta == 0f -> 0f
            max == r -> 60 * (((g - b) / delta) % 6)
            max == g -> 60 * (((b - r) / delta) + 2)
            else -> 60 * (((r - g) / delta) + 4)
        }.let { if (it < 0) it + 360f else it }

    val s =
        when {
            max == 0f -> 0f
            else -> delta / max
        }

    return Triple(h, s, max)
}
