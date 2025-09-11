package com.skeensystems.colorpicker.ui.picker

import kotlin.math.roundToInt

fun Float.format(
    componentType: ComponentType,
    id: String,
    lastUpdateId: String,
): Int =
    if (this == 0.000001f && id == lastUpdateId) {
        -1
    } else {
        when (componentType) {
            is H -> roundToInt()
            is S, is V -> (this * 100).roundToInt()
            else -> (this * 255).roundToInt()
        }
    }
