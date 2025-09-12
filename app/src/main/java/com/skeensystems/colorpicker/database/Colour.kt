package com.skeensystems.colorpicker.database

interface Colour {
    val r: Int
    val g: Int
    val b: Int

    fun getColour(): Int

    fun getName(): String

    fun getTextColour(): Int

    fun getHEXString(): String

    fun getRGBString(): String

    fun getHSVString(): String

    fun getHSLString(): String

    fun getCMYKString(): String
}
