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

    // TESTING

    /*oldSavedColours = new ArrayList<>(Arrays.asList(new SavedColour(0, 99, 25, 8, false),
                new SavedColour(1, 124, 116, 116, false),
                new SavedColour(2, 112, 93, 65, false),
                new SavedColour(3, 131, 73, 12, false),
                new SavedColour(4, 55, 26, 7, false),
                new SavedColour(5, 40, 10, 2, false),
                new SavedColour(6, 91, 51, 18, false),
                new SavedColour(7, 59, 34, 7, false),
                new SavedColour(8, 77, 31, 0, false),
                new SavedColour(9, 244, 226, 84, true),
                new SavedColour(10, 105, 183, 97, false),
                new SavedColour(11, 99, 138, 210, false),
                new SavedColour(12, 108, 132, 213, false),
                new SavedColour(13, 255, 119, 232, false),
                new SavedColour(14, 38, 98, 255, false),
                new SavedColour(15, 25, 94, 255, false),
                new SavedColour(16, 36, 86, 218, false),
                new SavedColour(17, 32, 81, 228, false),
                new SavedColour(18, 24, 36, 101, false),
                new SavedColour(19, 29, 90, 243, false),
                new SavedColour(20, 218, 233, 219, false),
                new SavedColour(21, 158, 180, 175, false),
                new SavedColour(22, 168, 186, 188, false),
                new SavedColour(23, 194, 203, 191, false),
                new SavedColour(24, 91, 87, 64, false),
                new SavedColour(25, 103, 83, 45, false),
                new SavedColour(26, 191, 146, 122, false),
                new SavedColour(27, 30, 154, 54, false),
                new SavedColour(28, 173, 112, 107, false),
                new SavedColour(29, 69, 110, 146, false),
                new SavedColour(30, 70, 129, 149, false),
                new SavedColour(31, 70, 129, 149, false),
                new SavedColour(32, 70, 129, 149, false),
                new SavedColour(33, 65, 51, 58, false),
                new SavedColour(34, 187, 166, 157, false),
                new SavedColour(35, 84, 33, 74, false),
                new SavedColour(36, 68, 44, 86, false),
                new SavedColour(37, 27, 55, 58, false),
                new SavedColour(38, 41, 106, 113, false),
                new SavedColour(39, 0, 87, 112, false),
                new SavedColour(40, 255, 0, 0, false),
                new SavedColour(41, 118, 73, 79, false),
                new SavedColour(42, 63, 87, 108, false),
                new SavedColour(43, 255, 191, 0, false)));*/

    oldSavedColours.forEach {
        colourDAO.insertAll(it)
    }
}
