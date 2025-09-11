package com.skeensystems.colorpicker.ui.picker

import android.graphics.Color.HSVToColor
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PickerViewModel : ViewModel() {
    private val _pickerColour = MutableStateFlow(Color.Black)
    val pickerColour: StateFlow<Color> = _pickerColour.asStateFlow()

    private val _cornerColour = MutableStateFlow(Color.Red)
    val cornerColour: StateFlow<Color> = _cornerColour.asStateFlow()

    private val _lastUpdateId = mutableStateOf("")
    val lastUpdateId: State<String> = _lastUpdateId

    private val _h = mutableFloatStateOf(0f)
    val h: State<Float> = _h
    private val _s = mutableFloatStateOf(0f)
    val s: State<Float> = _s
    private val _v = mutableFloatStateOf(0f)
    val v: State<Float> = _v

    private val _r = mutableFloatStateOf(0f)
    val r: State<Float> = _r
    private val _g = mutableFloatStateOf(0f)
    val g: State<Float> = _g
    private val _b = mutableFloatStateOf(0f)
    val b: State<Float> = _b

    fun setLastUpdateId(value: String) {
        _lastUpdateId.value = value
    }

    fun updateValue(
        componentType: ComponentType,
        newValue: Float,
    ) {
        val clampedValue = componentType.clampValue(newValue)
        when (componentType) {
            is H -> _h.floatValue = clampedValue
            is S -> _s.floatValue = clampedValue
            is V -> _v.floatValue = clampedValue
            is R -> _r.floatValue = clampedValue
            is G -> _g.floatValue = clampedValue
            is B -> _b.floatValue = clampedValue
        }
        componentType.updateOtherColourSystem()
        updateCornerColour()
        updateColour()
    }

    fun changeValue(
        componentType: ComponentType,
        valueChange: Int,
    ) {
        when (componentType) {
            is H -> updateValue(H, _h.floatValue + valueChange)
            is S -> updateValue(S, _s.floatValue + 0.01f * valueChange)
            is V -> updateValue(V, _v.floatValue + 0.01f * valueChange)
            is R -> updateValue(R, _r.floatValue + valueChange / 255f)
            is G -> updateValue(G, _g.floatValue + valueChange / 255f)
            is B -> updateValue(B, _b.floatValue + valueChange / 255f)
        }
    }

    private fun ComponentType.updateOtherColourSystem() {
        when (this) {
            is H, is S, is V -> {
                val (r, g, b) = toRGB(_h.floatValue, _s.floatValue, _v.floatValue)
                _r.floatValue = r
                _g.floatValue = g
                _b.floatValue = b
            }
            is R, is G, is B -> {
                val (h, s, v) = toHSV(_r.floatValue, _g.floatValue, _b.floatValue)
                _h.floatValue = h
                _s.floatValue = s
                _v.floatValue = v
            }
        }
    }

    private fun updateColour() {
        val argb = HSVToColor(floatArrayOf(h.value, s.value, v.value))
        _pickerColour.value = Color(argb)
    }

    private fun updateCornerColour() {
        val argb = HSVToColor(floatArrayOf(_h.floatValue, 1f, 1f))
        _cornerColour.value = Color(argb)
    }
}

fun toRGB(
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

fun toHSV(
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
