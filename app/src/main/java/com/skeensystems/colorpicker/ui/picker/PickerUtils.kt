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

fun Float.adjust(componentType: ComponentType): Float =
    when (componentType) {
        is H -> this
        is S, is V -> 0.01f * this
        is R, is G, is B -> this / 255f
    }
