package com.skeensystems.colorpicker.database

import android.graphics.Color
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.skeensystems.colorpicker.DARK_TEXT_COLOUR
import com.skeensystems.colorpicker.LIGHT_TEXT_COLOUR
import com.skeensystems.colorpicker.backgroundRequiresLightText
import com.skeensystems.colorpicker.getCMYKStringHelper
import com.skeensystems.colorpicker.getHEXStringHelper
import com.skeensystems.colorpicker.getHSLStringHelper
import com.skeensystems.colorpicker.getHSVStringHelper
import com.skeensystems.colorpicker.getRGBStringHelper
import java.util.Locale

// Saved colour object, provides useful functions for saved colours and stores data about them
@Entity
class SavedColour : Colour {

    @PrimaryKey
    private var id: Long

    // RGB colour values
    @ColumnInfo(name = "r")
    private var r: Int
    @ColumnInfo(name = "g")
    private var g: Int
    @ColumnInfo(name = "b")
    private var b: Int

    // True if colour is favorite, otherwise false
    @ColumnInfo(name = "favorite")
    private var favorite: Boolean

    // When this colour is the background, should text be white (true) or black (false)
    @Ignore
    private var requiresLightText: Boolean = false

    // Stores the closest colour to this in the colour database
    @Ignore
    private var closestMatch: DatabaseColour? = null
    @Ignore
    private var firstClosest: DatabaseColour? = null
    @Ignore
    private var secondClosest: DatabaseColour? = null
    @Ignore
    private var thirdClosest: DatabaseColour? = null

    constructor() {
        id = 0
        r = 0
        g = 0
        b = 0
        favorite = false
    }

    @Ignore
    constructor(id: Long, r: Int, g: Int, b: Int, favorite: Boolean) {
        this.id = id
        this.r = r
        this.g = g
        this.b = b
        this.favorite = favorite
        updateTextColour()
    }

    fun getId(): Long = id
    fun setId(id: Long) {
        this.id = id
    }

    override fun getColour(): Int = Color.rgb(r, g, b)

    override fun getName(): String = closestMatch!!.getName()

    override fun getR(): Int = r

    fun setR(r: Int) {
        this.r = r
        updateTextColour()
    }

    override fun getG(): Int = g

    fun setG(g: Int) {
        this.g = g
        updateTextColour()
    }

    override fun getB(): Int = b

    fun setB(b: Int) {
        this.b = b
        updateTextColour()
    }

    override fun getTextColour(): Int = if (requiresLightText) LIGHT_TEXT_COLOUR else DARK_TEXT_COLOUR

    fun updateTextColour() {
        requiresLightText = backgroundRequiresLightText(r, g, b)
    }

    override fun getHEXString(): String = getHEXStringHelper(r, g, b)

    override fun getRGBString(): String = getRGBStringHelper(r, g, b)

    override fun getHSVString(): String  = getHSVStringHelper(r, g, b)

    override fun getHSLString(): String = getHSLStringHelper(r, g, b)

    override fun getCMYKString(): String = getCMYKStringHelper(r, g, b)

    fun getClosestMatchString(): String =
        if (closestMatch != null) {
            "\u2248 $closestMatch"
        } else {
            ""
        }

    fun getFavorite(): Boolean = favorite

    fun setFavorite(favorite: Boolean) {
        this.favorite = favorite
    }

    fun getClosestMatch(): DatabaseColour = closestMatch!!

    fun setClosestMatch(closestMatch: DatabaseColour) {
        this.closestMatch = closestMatch
    }

    fun getFirstClosest(): DatabaseColour = firstClosest!!

    fun setFirstClosest(firstClosest: DatabaseColour?) {
        this.firstClosest = firstClosest
    }

    fun getSecondClosest(): DatabaseColour = secondClosest!!

    fun setSecondClosest(secondClosest: DatabaseColour?) {
        this.secondClosest = secondClosest
    }

    fun getThirdClosest(): DatabaseColour = thirdClosest!!

    fun setThirdClosest(thirdClosest: DatabaseColour?) {
        this.thirdClosest = thirdClosest
    }

    fun getSortValue(): String {
        val hsv = FloatArray(3)
        Color.RGBToHSV(r, g, b, hsv)

        val newH = hsv[0].toInt()
        val newS = Math.round(hsv[1] * 100)
        val newV = Math.round(hsv[2] * 100)

        return (String.format(Locale.getDefault(), "%03d", newH)
                + String.format(Locale.getDefault(), "%03d", newS)
                + String.format(Locale.getDefault(), "%03d", newV))
    }

    override fun toString(): String {
        return "Id: $id, Colour: $r, $g, $b, Favourite: $favorite"
    }
}