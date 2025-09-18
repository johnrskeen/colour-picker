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
        val inputStream = context.resources.openRawResource(R.raw.colour_database)
        val text = inputStream.bufferedReader().readText()

        return Json.decodeFromString(text)
    }

    fun getClosestMatches(
        r: Int,
        g: Int,
        b: Int,
    ): Pair<DatabaseColour, Set<DatabaseColour>> {
        var closestMatches: List<Pair<Float, DatabaseColour>> = mutableListOf(Pair(Float.MAX_VALUE, DatabaseColour("", 0, 0, 0, "")))
        colourDatabase.forEach { databaseColour ->
            val distance = distance(r, g, b, databaseColour)
            closestMatches = closestMatches.plus(Pair(distance, databaseColour)).sortedBy { it.first }.take(4)
        }
        return Pair(closestMatches.first().second, closestMatches.drop(1).map { it.second }.toSet())
    }

    private fun distance(
        r: Int,
        g: Int,
        b: Int,
        colour: Colour,
    ): Float =
        (r - colour.r).toFloat().pow(2) +
            (g - colour.g).toFloat().pow(2) +
            (b - colour.b).toFloat().pow(2)

    fun getColourByName(name: String): DatabaseColour? = colourDatabase.find { it.name == name }
}
