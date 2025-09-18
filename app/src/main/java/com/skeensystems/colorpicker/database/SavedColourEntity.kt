package com.skeensystems.colorpicker.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "SavedColour")
data class SavedColourEntity(
    @PrimaryKey val id: Long,
    @ColumnInfo(name = "r") val r: Int,
    @ColumnInfo(name = "g") val g: Int,
    @ColumnInfo(name = "b") val b: Int,
    @ColumnInfo(name = "favorite") val favourite: Boolean,
) {
    fun toSavedColour(
        name: String,
        similarColours: Set<DatabaseColour>,
        complementaryColours: Set<DatabaseColour>,
    ): SavedColour = SavedColour(id, name, r, g, b, favourite, similarColours, complementaryColours)
}
