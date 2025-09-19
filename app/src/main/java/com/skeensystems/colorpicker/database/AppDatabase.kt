package com.skeensystems.colorpicker.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [SavedColourEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun colourDAO(): ColourDAO
}
