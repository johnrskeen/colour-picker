package com.skeensystems.colorpicker.database

import android.content.Context
import java.io.File

fun migrateToDatabase(
    context: Context,
    colourDAO: ColourDAO,
) {
    val oldSavedColours: MutableList<SavedColourEntity> = mutableListOf()

    val pickedColoursFile = File(context.filesDir.toString() + "pickedColours.txt")

    var idCounter = 0
    if (pickedColoursFile.exists()) {
        val lines = pickedColoursFile.bufferedReader().use { it.readLines() }
        lines.forEach {
            // Set r, g and b values using #RRGGBB from file
            val r = it.substring(1, 3).toInt(16)
            val g = it.substring(3, 5).toInt(16)
            val b = it.substring(5).toInt(16)
            val savedColour = SavedColourEntity(idCounter.toLong(), r, g, b, false)
            oldSavedColours.add(savedColour)
            idCounter++
        }
    } else {
        return
    }

    val favouritesFile = File(context.filesDir.toString() + "favoriteColours.txt")
    if (pickedColoursFile.exists()) {
        val lines = favouritesFile.bufferedReader().use { it.readLines() }
        lines.forEach {
            val index = it.toInt()
            oldSavedColours[index] = oldSavedColours[index].copy(favourite = true)
        }
    }

    println(File(context.filesDir.toString() + "pickedColours.txt").delete())
    println(File(context.filesDir.toString() + "favoriteColours.txt").delete())

    oldSavedColours.forEach {
        colourDAO.insertAll(it)
    }
}
