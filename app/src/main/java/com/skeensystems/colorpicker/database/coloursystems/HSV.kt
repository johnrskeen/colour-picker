package com.skeensystems.colorpicker.database.coloursystems

import kotlin.math.roundToInt

class HSV(
    h: Float,
    s: Float,
    v: Float,
) {
    val h = h.roundToInt()
    val s = (s * 100).roundToInt()
    val v = (v * 100).roundToInt()
}
