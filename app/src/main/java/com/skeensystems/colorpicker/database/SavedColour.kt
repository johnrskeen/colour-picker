package com.skeensystems.colorpicker.database

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.skeensystems.colorpicker.ui.saved.sortandfilter.SortOptions
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

    fun getSortValue(sortStatus: SortOptions): Float =
        when (sortStatus) {
            SortOptions.BY_R_VALUE -> r - 0.5f * (g + b)
            SortOptions.BY_G_VALUE -> g - 0.5f * (r + b)
            SortOptions.BY_B_VALUE -> b - 0.5f * (r + g)
            else -> String.format(Locale.getDefault(), "%03d%03d%03d", hsv.h, hsv.s, hsv.v).toFloat()
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
