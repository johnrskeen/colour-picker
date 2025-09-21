package com.skeensystems.colorpicker.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ColourDAO {
    @Query("SELECT * FROM SavedColour")
    fun getAll(): List<SavedColourEntity>

    @Query("SELECT * FROM SavedColour WHERE r = :r AND g = :g AND b = :b LIMIT 1")
    fun findByColour(
        r: Int,
        g: Int,
        b: Int,
    ): SavedColourEntity

    @Query("SELECT * FROM SavedColour WHERE favorite LIKE :favorite")
    fun loadFavoriteColours(favorite: Boolean): List<SavedColourEntity>

    @Query("UPDATE SavedColour SET favorite = :favorite WHERE id = :id")
    fun updateFavoriteColour(
        id: Long,
        favorite: Boolean,
    )

    @Query("UPDATE SavedColour SET r = :r, g = :g, b = :b WHERE id = :id")
    fun updateColour(
        id: Long,
        r: Int,
        g: Int,
        b: Int,
    )

    @Insert
    fun insertAll(vararg savedColours: SavedColourEntity)

    @Delete
    fun delete(savedColour: SavedColourEntity)

    @Query("DELETE FROM SavedColour WHERE id = :id")
    fun deleteById(id: Int)
}
