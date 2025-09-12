package com.skeensystems.colorpicker.database

import com.skeensystems.colorpicker.getCMYKStringHelper
import com.skeensystems.colorpicker.getHEXStringHelper
import com.skeensystems.colorpicker.getHSLStringHelper
import com.skeensystems.colorpicker.getHSVStringHelper
import com.skeensystems.colorpicker.getRGBStringHelper

interface Colour {
    val r: Int
    val g: Int
    val b: Int

    fun getColour(): Int

    fun getName(): String

    fun getTextColour(): Int

    fun getHEXString(): String = getHEXStringHelper(r, g, b)

    fun getRGBString(): String = getRGBStringHelper(r, g, b)

    fun getHSVString(): String = getHSVStringHelper(r, g, b)

    fun getHSLString(): String = getHSLStringHelper(r, g, b)

    fun getCMYKString(): String = getCMYKStringHelper(r, g, b)
}
