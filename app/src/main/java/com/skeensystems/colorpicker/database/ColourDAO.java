package com.skeensystems.colorpicker.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ColourDAO {
    @Query("SELECT * FROM SavedColour")
    List<SavedColourEntity> getAll();

    @Query("SELECT * FROM SavedColour WHERE id IN (:colourIds)")
    List<SavedColourEntity> loadAllByIds(int[] colourIds);

    @Query("SELECT * FROM SavedColour WHERE r = :r AND g = :g AND b = :b LIMIT 1")
    SavedColourEntity findByColour(int r, int g, int b);

    @Query("SELECT * FROM SavedColour WHERE favorite LIKE :favorite")
    List<SavedColourEntity> loadFavoriteColours(boolean favorite);

    @Query("UPDATE SavedColour SET favorite = :favorite WHERE id = :id")
    void updateFavoriteColour(long id, boolean favorite);

    @Query("UPDATE SavedColour SET r = :r, g = :g, b = :b WHERE id = :id")
    void updateColour(long id, int r, int g, int b);

    @Insert
    void insertAll(SavedColourEntity... savedColours);

    @Delete
    void delete(SavedColourEntity savedColour);

    @Query("DELETE FROM SavedColour WHERE id = :id")
    void deleteById(int id);
}