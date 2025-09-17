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
    r: Int,
    g: Int,
    b: Int,
    favourite: Boolean,
) : Colour {
    override var r by mutableIntStateOf(r)
    override var g by mutableIntStateOf(g)
    override var b by mutableIntStateOf(b)

    var favourite by mutableStateOf(favourite)

    private var requiresLightText: Boolean = backgroundRequiresLightText(r, g, b)

    private var closestMatch: DatabaseColour? = null
    private var firstClosest: DatabaseColour? = null
    private var secondClosest: DatabaseColour? = null
    private var thirdClosest: DatabaseColour? = null

    override fun getColour(): Int = Color.rgb(r, g, b)

    override fun getName(): String = closestMatch?.getName() ?: ""

    override fun getTextColour(): Int = if (requiresLightText) LIGHT_TEXT_COLOUR else DARK_TEXT_COLOUR

    fun getDetailsList(): List<Pair<String, String>> =
        listOf(
            Pair("HEX", getHEXString()),
            Pair("RGB", getRGBString()),
            Pair("HSV", getHSVString()),
            Pair("HSL", getHSLString()),
            Pair("CMYK", getCMYKString()),
        )

    fun getClosestMatchString(): String =
        if (closestMatch != null) {
            "\u2248 $closestMatch"
        } else {
            ""
        }

    fun getSimilarColours(): List<DatabaseColour> = setOfNotNull(firstClosest, secondClosest, thirdClosest).toList()

    fun getComplementaryColours(): List<DatabaseColour> =
        setOfNotNull(
            closestMatch?.getComplementaryColour(),
            firstClosest?.getComplementaryColour(),
            secondClosest?.getComplementaryColour(),
            thirdClosest?.getComplementaryColour(),
        ).toList().take(3)

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
