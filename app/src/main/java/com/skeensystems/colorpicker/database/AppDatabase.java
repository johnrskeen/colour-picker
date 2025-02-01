package com.skeensystems.colorpicker.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {SavedColour.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ColourDAO colourDAO();
}