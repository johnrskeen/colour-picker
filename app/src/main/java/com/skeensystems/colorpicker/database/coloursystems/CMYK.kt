package com.skeensystems.colorpicker.database.coloursystems

import kotlin.math.max
import kotlin.math.roundToInt

class CMYK(
    r: Float,
    g: Float,
    b: Float,
) {
    private val kP = 1 - max(r, max(g, b))
    val c = ((1 - r - kP) / (1 - kP) * 100).roundToInt()
    val m = ((1 - g - kP) / (1 - kP) * 100).roundToInt()
    val y = ((1 - b - kP) / (1 - kP) * 100).roundToInt()
    val k = (kP * 100).roundToInt()
}
