package com.skeensystems.colorpicker.database.coloursystems

import kotlin.math.min
import kotlin.math.roundToInt

class HSL(
    h: Float,
    s: Float,
    v: Float,
) {
    private val lFloat = v * (1 - 0.5f * s)
    val h = h.roundToInt()
    val l = (lFloat * 100).roundToInt()
    val s = ((if (l == 0 || l == 100) 0f else ((v - lFloat) / min(lFloat, 1 - lFloat))) * 100).roundToInt()
}
