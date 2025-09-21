package com.skeensystems.colorpicker.ui.picker

import android.app.Application
import android.graphics.Color.HSVToColor
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.skeensystems.colorpicker.hsvToRGB
import com.skeensystems.colorpicker.rgbToHSV
import com.skeensystems.colorpicker.userpreferences.PreferenceKeys
import com.skeensystems.colorpicker.userpreferences.loadPreference
import com.skeensystems.colorpicker.userpreferences.savePreference
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PickerViewModel(
    application: Application,
) : AndroidViewModel(application) {
    private val context = getApplication<Application>()

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

    init {
        viewModelScope.launch {
            _r.floatValue = context.loadPreference(PreferenceKeys.PICKER_R, 1f)
            _g.floatValue = context.loadPreference(PreferenceKeys.PICKER_G, 0f)
            _b.floatValue = context.loadPreference(PreferenceKeys.PICKER_B, 0f)

            R.updateOtherColourSystem()
            updateCornerColour()
            updateColour()
        }
    }

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
            is R -> {
                _r.floatValue = clampedValue
                context.savePreference(PreferenceKeys.PICKER_R, clampedValue)
            }
            is G -> {
                _g.floatValue = clampedValue
                context.savePreference(PreferenceKeys.PICKER_G, clampedValue)
            }
            is B -> {
                _b.floatValue = clampedValue
                context.savePreference(PreferenceKeys.PICKER_B, clampedValue)
            }
        }
        componentType.updateOtherColourSystem()
        updateCornerColour()
        updateColour()
    }

    // TODO merge this with adjust extension function in PickerUtils
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
                val (r, g, b) = hsvToRGB(_h.floatValue, _s.floatValue, _v.floatValue)
                _r.floatValue = r
                _g.floatValue = g
                _b.floatValue = b
                context.savePreference(PreferenceKeys.PICKER_R, r)
                context.savePreference(PreferenceKeys.PICKER_G, g)
                context.savePreference(PreferenceKeys.PICKER_B, b)
            }
            is R, is G, is B -> {
                val (h, s, v) = rgbToHSV(_r.floatValue, _g.floatValue, _b.floatValue)
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
