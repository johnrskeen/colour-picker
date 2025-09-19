package com.skeensystems.colorpicker.database

import androidx.compose.ui.graphics.Color
import com.skeensystems.colorpicker.calculateTextColour
import com.skeensystems.colorpicker.database.coloursystems.CMYK
import com.skeensystems.colorpicker.database.coloursystems.HSL
import com.skeensystems.colorpicker.database.coloursystems.HSV
import com.skeensystems.colorpicker.rgbToHSV

abstract class Colour {
    abstract val name: String
    abstract val r: Int
    abstract val g: Int
    abstract val b: Int

    val textColour by lazy { getColour().calculateTextColour() }

    private val hsvFloat by lazy {
        rgbToHSV(r / 255f, g / 255f, b / 255f)
    }
    protected val hsv by lazy { HSV(hsvFloat.first, hsvFloat.second, hsvFloat.third) }
    private val hsl by lazy { HSL(hsvFloat.first, hsvFloat.second, hsvFloat.third) }
    private val cmyk by lazy { CMYK(r / 255f, g / 255f, b / 255f) }

    fun getColour(): Color = Color(r, g, b)

    fun getHEXString(): String = String.format("#%02X%02X%02X", r, g, b)

    fun getRGBString(): String = "$r, $g, $b"

    fun getHSVString(): String = "${hsv.h}\u00B0, ${hsv.s}%, ${hsv.v}%"

    fun getHSLString(): String = "${hsl.h}\u00B0, ${hsl.s}%, ${hsl.l}%"

    fun getCMYKString(): String = "${cmyk.c}%, ${cmyk.m}%, ${cmyk.y}%, ${cmyk.k}%"

    fun generateCopyString(): String =
        name +
            "\nHEX ${getHEXString()}" +
            "\nHEX ${getRGBString()}" +
            "\nHSV ${getHSVString()}" +
            "\nHSL ${getHSLString()}" +
            "\nCMYK ${getCMYKString()}"
}
