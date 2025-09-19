package com.skeensystems.colorpicker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.room.Room.databaseBuilder
import com.google.android.gms.ads.MobileAds
import com.skeensystems.colorpicker.database.AppDatabase
import com.skeensystems.colorpicker.database.ColourDAO
import com.skeensystems.colorpicker.ui.App
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var colourDAO: ColourDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        CoroutineScope(Dispatchers.IO).launch {
            val db =
                databaseBuilder(
                    applicationContext,
                    AppDatabase::class.java,
                    "colour_picker_database",
                ).build()
            colourDAO = db.colourDAO()
        }
        CoroutineScope(Dispatchers.IO).launch {
            MobileAds.initialize(this@MainActivity) {}
        }

        enableEdgeToEdge()
        setContent {
            ColourPickerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    App(colourDAO = colourDAO)
                }
            }
        }
    }
}
