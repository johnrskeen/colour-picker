package com.skeensystems.colorpicker.database

import androidx.compose.ui.graphics.Color
import com.skeensystems.colorpicker.calculateTextColour
import com.skeensystems.colorpicker.getCMYKStringHelper
import com.skeensystems.colorpicker.getHSLStringHelper
import com.skeensystems.colorpicker.getHSVStringHelper

abstract class Colour {
    abstract val name: String
    abstract val r: Int
    abstract val g: Int
    abstract val b: Int

    val textColour by lazy { getColour().calculateTextColour() }

    fun getColour(): Color = Color(r, g, b)

    fun getHEXString(): String = String.format("#%02x%02x%02x", r, g, b)

    fun getRGBString(): String = "$r, $g, $b"

    fun getHSVString(): String = getHSVStringHelper(r, g, b)

    fun getHSLString(): String = getHSLStringHelper(r, g, b)

    fun getCMYKString(): String = getCMYKStringHelper(r, g, b)

    fun generateCopyString(): String =
        name +
            "\nHEX ${getHEXString()}" +
            "\nHEX ${getRGBString()}" +
            "\nHSV ${getHSVString()}" +
            "\nHSL ${getHSLString()}" +
            "\nCMYK ${getCMYKString()}"
}
