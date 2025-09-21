package com.skeensystems.colorpicker.database

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class DatabaseColour(
    @SerialName("n") override val name: String,
    override val r: Int,
    override val g: Int,
    override val b: Int,
    @SerialName("c") val complementaryName: String,
) : Colour() {
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
