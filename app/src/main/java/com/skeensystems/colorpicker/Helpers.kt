package com.skeensystems.colorpicker

import android.graphics.Color
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

// Dark and light text colours (to be used on light and dark backgrounds respectively)
@JvmField
var DARK_TEXT_COLOUR = Color.BLACK
@JvmField
var LIGHT_TEXT_COLOUR = -0x222223

/**
 * Calculates whether text should be light or dark when inputted colour is the background
 * @param r red value, 0 <= r <= 255
 * @param g green value, 0 <= g <= 255
 * @param b blue value, 0 <= b <= 255
 * @return true if inputted colour requires light text,
 * false if inputted colour requires dark text
 */
fun backgroundRequiresLightText(r: Int, g: Int, b: Int): Boolean {
    // Convert r from [0, 255] to [0, 1]
    var newR = r / 255.0
    // Run the sums to do the magic
    newR = if (newR <= 0.04045) {
        newR / 12.92
    } else {
        ((newR + 0.055) / 1.055).pow(2.4)
    }
    // Convert g from [0, 255] to [0, 1]
    var newG = g / 255.0
    // Run the sums to do the magic
    newG = if (newG <= 0.04045) {
        newG / 12.92
    } else {
        ((newG + 0.055) / 1.055).pow(2.4)
    }
    // Convert b from [0, 255] to [0, 1]
    var newB = b / 255.0
    // Run the sums to do the magic
    newB = if (newB <= 0.04045) {
        newB / 12.92
    } else {
        ((newB + 0.055) / 1.055).pow(2.4)
    }
    // Final calculations
    val l = 0.2126 * newR + 0.7152 * newG + 0.0722 * newB
    // Return answer to the question
    return l <= 0.179

    // As you probably guessed, I did not write this function, but so far it has given acceptable results
}

/**
 * Converts RGB colour to HSV
 * This function already exists, Color.RGBtoHSV(r, g, b, hsv) (hsv is float[3])
 * @param r red value, 0 <= r <= 255
 * @param g green value, 0 <= g <= 255
 * @param b blue value, 0 <= b <= 255
 * @return Triple(h, s, v)
 */
fun RGBtoHSV(r: Int, g: Int, b: Int): Triple<Double, Double, Double> {
    // Just a formula to calculate HSV values from RGB values
    val rScaled = r.toFloat() / 255
    val gScaled = g.toFloat() / 255
    val bScaled = b.toFloat() / 255

    var comparison = max(rScaled.toDouble(), gScaled.toDouble()).toFloat()
    val cMax = max(comparison.toDouble(), bScaled.toDouble()).toFloat()

    comparison = min(rScaled.toDouble(), gScaled.toDouble()).toFloat()
    val cMin = min(comparison.toDouble(), bScaled.toDouble()).toFloat()

    val delta = cMax - cMin

    var h = if (delta == 0f) {
        0.0
    } else if (cMax == rScaled) {
        (60 * (((gScaled - bScaled) / delta) % 6)).toDouble()
    } else if (cMax == gScaled) {
        (60 * (((bScaled - rScaled) / delta) + 2)).toDouble()
    } else {
        (60 * (((rScaled - gScaled) / delta) + 4)).toDouble()
    }

    if (h < 0) {
        h += 360.0
    }

    val s = if (cMax == 0f) {
        0.0
    } else {
        (100 * delta / cMax).toDouble()
    }

    val v = (cMax * 100).toDouble()

    return Triple(h, s, v)
}

/**
 * Converts HSV colour to RGB
 * @param h 0 <= h <= 360
 * @param s 0 <= s <= 1
 * @param v 0 <= v <= 1
 * @return Triple(r, g, b)
 */
fun HSVtoRGB(h: Float, s: Float, v: Float): Triple<Double, Double, Double> {
    // Just a formula to calculate RGB values from HSV values
    var h = h
    assert(h >= 0)
    if (h == 360f) h = 0f
    val c = (s * v).toDouble()
    val x = c * (1 - abs(((h / 60) % 2 - 1).toDouble()))
    val m = v - c
    var rgbTemp = Triple(0.0, 0.0, 0.0)
    if (h < 60) {
        rgbTemp = Triple(c, x, 0.0)
    } else if (h < 120) {
        rgbTemp = Triple(x, c, 0.0)
    } else if (h < 180) {
        rgbTemp = Triple(0.0, c, x)
    } else if (h < 240) {
        rgbTemp = Triple(0.0, x, c)
    } else if (h < 300) {
        rgbTemp = Triple(x, 0.0, c)
    } else if (h < 360) {
        rgbTemp = Triple(c, 0.0, x)
    } else {
        assert(false)
    }

    return Triple(
        (rgbTemp.component1() + m) * 255,
        (rgbTemp.component2() + m) * 255,
        (rgbTemp.component3() + m) * 255
    )
}


/**
 * Returns HEX string from inputted RGB values
 * @param r red value, 0 <= r <= 255
 * @param g green value, 0 <= g <= 255
 * @param b blue value, 0 <= b <= 255
 * @return HEX string of inputted RGB colour
 */
fun getHEXString(r: Int, g: Int, b: Int): String {
    return String.format("#%06X", (0xFFFFFF and Color.rgb(r, g, b)))
}

/**
 * Returns RGB string from inputted RGB values
 * @param r red value, 0 <= r <= 255
 * @param g green value, 0 <= g <= 255
 * @param b blue value, 0 <= b <= 255
 * @return RGB string of inputted RGB colour
 */
fun getRGBString(r: Int, g: Int, b: Int): String {
    return "$r, $g, $b"
}

/**
 * Returns HSV string from inputted RGB values
 * @param r red value, 0 <= r <= 255
 * @param g green value, 0 <= g <= 255
 * @param b blue value, 0 <= b <= 255
 * @return HSV string of inputted RGB colour
 */
fun getHSVString(r: Int, g: Int, b: Int): String {
    // Convert RGB values to HSV values
    val hsv = FloatArray(3)
    Color.RGBToHSV(r, g, b, hsv)
    // Construct HSV string
    return hsv[0].toInt().toString() + "\u00B0, " + Math.round(hsv[1] * 100) + "%, " + Math.round(
        hsv[2] * 100
    ) + "%"
}

/**
 * Returns HSL string from inputted RGB values
 * @param r red value, 0 <= r <= 255
 * @param g green value, 0 <= g <= 255
 * @param b blue value, 0 <= b <= 255
 * @return HSL string of inputted RGB colour
 */
fun getHSLString(r: Int, g: Int, b: Int): String {
    // Convert RGB values to HSV values
    val hsv = FloatArray(3)
    Color.RGBToHSV(r, g, b, hsv)
    // Convert HSV values to HSL values
    val h = hsv[0].toInt()
    val l = hsv[2] * (1 - 0.5 * hsv[1])
    var s = 0.0
    if (!(l == 0.0 || l == 100.0)) {
        s = (hsv[2] - l) / min(l, 1 - l)
    }
    // Construct HSL string
    return h.toString() + "\u00B0, " + Math.round(s * 100) + "%, " + Math.round(l * 100) + "%"
}

/**
 * Returns CMYK string from inputted RGB values
 * @param r red value, 0 <= r <= 255
 * @param g green value, 0 <= g <= 255
 * @param b blue value, 0 <= b <= 255
 * @return CMYK string of inputted RGB colour
 */
fun getCMYKString(r: Int, g: Int, b: Int): String {
    // Convert RGB values to CMYK values
    val rP = (r / 255.0f).toDouble()
    val gP = (g / 255.0f).toDouble()
    val bP = (b / 255.0f).toDouble()
    val k = 1 - max(rP, max(gP, bP))
    val c = (1 - rP - k) / (1 - k)
    val m = (1 - gP - k) / (1 - k)
    val y = (1 - bP - k) / (1 - k)
    // Convert CMYK string
    return Math.round(c * 100)
        .toString() + "%, " + Math.round(m * 100) + "%, " + Math.round(y * 100) + "%, " + Math.round(
        k * 100
    ) + "%"
}