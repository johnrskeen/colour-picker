package com.skeensystems.colorpicker.database

interface Colour {

    fun getColour(): Int

    fun getR(): Int

    fun getG(): Int

    fun getB(): Int

    fun getTextColour(): Int

    fun getHEXString(): String

    fun getRGBString(): String

    fun getHSVString(): String

    fun getHSLString(): String

    fun getCMYKString(): String

    fun getName(): String
}