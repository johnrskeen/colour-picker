package com.skeensystems.colorpicker.database

interface Colour {

    fun getColour(): Int

    fun getName(): String

    fun getR(): Int

    fun getG(): Int

    fun getB(): Int

    fun getTextColour(): Int

    fun getHEXString(): String

    fun getRGBString(): String

    fun getHSVString(): String

    fun getHSLString(): String

    fun getCMYKString(): String
}