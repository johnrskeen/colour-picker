package com.skeensystems.colorpicker.ui.camera

import androidx.camera.core.ImageProxy
import java.nio.ByteBuffer
import kotlin.math.roundToInt

fun ImageProxy.getColour(): Triple<Int, Int, Int> {
    val centreX = width / 2
    val centreY = height / 2

    val yArr = planes[0].buffer.toByteArray()
    val yPixelStride = planes[0].pixelStride
    val yRowStride = planes[0].rowStride

    val uArr = planes[1].buffer.toByteArray()
    val uPixelStride = planes[1].pixelStride
    val uRowStride = planes[1].rowStride

    val vArr = planes[2].buffer.toByteArray()
    val vPixelStride = planes[2].pixelStride
    val vRowStride = planes[2].rowStride

    // Calculate YUV values using the previously calculated byte arrays
    val y = yArr[(centreY * yRowStride + centreX * yPixelStride)].toInt() and 255
    val u = (uArr[(centreY * uRowStride + centreX * uPixelStride) / 2].toInt() and 255) - 128
    val v = (vArr[(centreY * vRowStride + centreX * vPixelStride) / 2].toInt() and 255) - 128

    // Convert YUV to RGB
    val r = (y + (1.370705 * v)).coerceIn(0.0, 255.0).roundToInt()
    val g = (y - (0.698001 * v) - (0.337633 * u)).coerceIn(0.0, 255.0).roundToInt()
    val b = (y + (1.732446 * u)).coerceIn(0.0, 255.0).roundToInt()

    return Triple(r, g, b)
}

private fun ByteBuffer.toByteArray(): ByteArray {
    rewind()
    val data = ByteArray(remaining())
    get(data)
    return data
}
