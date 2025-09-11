package com.skeensystems.colorpicker.ui.picker

sealed interface ComponentType

data object H : ComponentType {
    override fun toString(): String = "Hue"
}

data object S : ComponentType {
    override fun toString(): String = "Saturation"
}

data object V : ComponentType {
    override fun toString(): String = "Value"
}

data object R : ComponentType {
    override fun toString(): String = "Red"
}

data object G : ComponentType {
    override fun toString(): String = "Green"
}

data object B : ComponentType {
    override fun toString(): String = "Blue"
}

fun ComponentType.clampValue(value: Float): Float =
    when (this) {
        is H -> value.coerceIn(0f, 360f)
        else -> value.coerceIn(0f, 1f)
    }
