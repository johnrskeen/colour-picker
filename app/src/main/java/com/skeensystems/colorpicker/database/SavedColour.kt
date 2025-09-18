package com.skeensystems.colorpicker.database

import android.graphics.Color
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.skeensystems.colorpicker.DARK_TEXT_COLOUR
import com.skeensystems.colorpicker.LIGHT_TEXT_COLOUR
import com.skeensystems.colorpicker.backgroundRequiresLightText
import java.util.Locale

class SavedColour(
    val id: Long,
    override val name: String,
    override val r: Int,
    override val g: Int,
    override val b: Int,
    favourite: Boolean,
    val similarColours: Set<DatabaseColour>,
    val complementaryColours: Set<DatabaseColour>,
) : Colour() {
    // override var r by mutableIntStateOf(r)
    // override var g by mutableIntStateOf(g)
    // override var b by mutableIntStateOf(b)

    var favourite by mutableStateOf(favourite)

    fun getDetailsList(): List<Pair<String, String>> =
        listOf(
            Pair("HEX", getHEXString()),
            Pair("RGB", getRGBString()),
            Pair("HSV", getHSVString()),
            Pair("HSL", getHSLString()),
            Pair("CMYK", getCMYKString()),
        )

    fun getClosestMatchString(): String = "\u2248 $name"

    fun getSortValue(): String {
        val hsv = FloatArray(3)
        Color.RGBToHSV(r, g, b, hsv)

        val newH = hsv[0].toInt()
        val newS = Math.round(hsv[1] * 100)
        val newV = Math.round(hsv[2] * 100)

        return (
            String.format(Locale.getDefault(), "%03d", newH) +
                String.format(Locale.getDefault(), "%03d", newS) +
                String.format(Locale.getDefault(), "%03d", newV)
        )
    }

    fun toSavedColourEntity(): SavedColourEntity = SavedColourEntity(id, r, g, b, favourite)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SavedColour

        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "Id: $id, Colour: $r, $g, $b, Favourite: $favourite"
}
