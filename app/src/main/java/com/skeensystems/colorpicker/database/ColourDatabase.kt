package com.skeensystems.colorpicker.database

import android.content.Context
import com.skeensystems.colorpicker.R
import kotlinx.serialization.json.Json
import kotlin.math.pow

class ColourDatabase(
    context: Context,
) {
    private val colourDatabase: List<DatabaseColour> = loadColors(context)

    private fun loadColors(context: Context): List<DatabaseColour> {
        val inputStream = context.resources.openRawResource(R.raw.colour_database_new)
        val text = inputStream.bufferedReader().readText()

        return Json.decodeFromString(text)
    }

    fun getClosestMatches(colour: Colour): Pair<DatabaseColour, Set<DatabaseColour>> {
        var closestMatches: List<Pair<Float, DatabaseColour>> = mutableListOf(Pair(Float.MAX_VALUE, DatabaseColour("", 0, 0, 0, "")))
        colourDatabase.forEach { databaseColour ->
            val distance = distance(colour, databaseColour)
            closestMatches = closestMatches.plus(Pair(distance, databaseColour)).sortedBy { it.first }.take(4)
        }
        return Pair(closestMatches.first().second, closestMatches.drop(1).map { it.second }.toSet())
    }

    private fun distance(
        colour1: Colour,
        colour2: Colour,
    ): Float =
        (colour1.r - colour2.r).toFloat().pow(2) +
            (colour1.g - colour2.g).toFloat().pow(2) +
            (colour1.b - colour2.b).toFloat().pow(2)
}
