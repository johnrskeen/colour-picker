package com.skeensystems.colorpicker.database

import android.graphics.Color
import com.skeensystems.colorpicker.DARK_TEXT_COLOUR
import com.skeensystems.colorpicker.LIGHT_TEXT_COLOUR
import com.skeensystems.colorpicker.backgroundRequiresLightText
import kotlinx.serialization.Serializable

@Serializable
class DatabaseColour(
    private val name: String,
    override val r: Int,
    override val g: Int,
    override val b: Int,
) : Colour {
    private var complementaryColour: DatabaseColour? = null

    // When this colour is the background, should text be white (true) or black (false)
    private val requiresLightText = backgroundRequiresLightText(r, g, b)

    fun setComplementaryColour(
        complementaryName: String,
        complementaryR: Int,
        complementaryG: Int,
        complementaryB: Int,
    ) {
        complementaryColour = DatabaseColour(complementaryName, complementaryR, complementaryG, complementaryB)
    }

    fun getComplementaryColour(): DatabaseColour? = complementaryColour

    override fun getColour(): Int = Color.rgb(r, g, b)

    override fun getName(): String = name

    override fun getTextColour(): Int = if (requiresLightText) LIGHT_TEXT_COLOUR else DARK_TEXT_COLOUR

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DatabaseColour

        if (r != other.r) return false
        if (g != other.g) return false
        if (b != other.b) return false

        return true
    }

    override fun hashCode(): Int {
        var result = r
        result = 31 * result + g
        result = 31 * result + b
        return result
    }

    override fun toString(): String = name
}
