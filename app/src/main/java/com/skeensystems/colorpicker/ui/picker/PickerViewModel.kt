package com.skeensystems.colorpicker.ui.picker

import android.graphics.Color.HSVToColor
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableFloatStateOf
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

    private val _h = mutableFloatStateOf(0f)
    val h: State<Float> = _h
    private val _s = mutableFloatStateOf(0f)
    val s: State<Float> = _s
    private val _v = mutableFloatStateOf(0f)
    val v: State<Float> = _v

    fun updateH(h: Float) {
        _h.floatValue = h.coerceIn(0f, 360f)
        val argb = HSVToColor(floatArrayOf(h, 1f, 1f))
        _cornerColour.value = Color(argb)
        updateColour()
    }

    fun updateS(s: Float) {
        _s.floatValue = s.coerceIn(0f, 1f)
        updateColour()
    }

    fun updateV(v: Float) {
        _v.floatValue = v.coerceIn(0f, 1f)
        updateColour()
    }

    private fun updateColour() {
        val argb = HSVToColor(floatArrayOf(h.value, s.value, v.value))
        _pickerColour.value = Color(argb)
    }
}
