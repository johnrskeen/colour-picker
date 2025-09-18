package com.skeensystems.colorpicker

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.skeensystems.colorpicker.database.ColourDAO
import com.skeensystems.colorpicker.database.ColourDatabase
import com.skeensystems.colorpicker.database.DatabaseColour
import com.skeensystems.colorpicker.database.SavedColour
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(
    private val colourDAO: ColourDAO,
    private val colourDatabase: ColourDatabase,
) : ViewModel() {
    private val _savedColours = mutableStateListOf<SavedColour>()
    val savedColours: List<SavedColour> = _savedColours

    private val _editingColour = mutableStateOf<EditEvent?>(null)
    val editingColour: State<EditEvent?> = _editingColour

    init {
        viewModelScope.launch(Dispatchers.IO) {
            colourDAO.all
                .forEach {
                    val (closestMatch, similarColours, complementaryColours) = calculateRelatedColours(it.r, it.g, it.b)
                    _savedColours.add(it.toSavedColour(closestMatch, similarColours, complementaryColours))
                }
        }
    }

    private fun calculateRelatedColours(
        r: Int,
        g: Int,
        b: Int,
    ): Triple<DatabaseColour, Set<DatabaseColour>, Set<DatabaseColour>> {
        val (closestMatch, similarColours) = colourDatabase.getClosestMatches(r, g, b)
        val complementaryColours =
            similarColours
                .asSequence()
                .plus(closestMatch)
                .mapNotNull { colourDatabase.getColourByName(it.complementaryName) }
                .toSet()
                .take(3)
                .toSet()
        return Triple(closestMatch, similarColours, complementaryColours)
    }

    // TODO temporary, while colours are manually added, will be removed with full migration
    fun clearSavedColours() {
        _savedColours.clear()
    }

    fun saveColour(
        r: Int,
        g: Int,
        b: Int,
    ): SavedColour {
        val (closestMatch, similarColours, complementaryColours) = calculateRelatedColours(r, g, b)
        val newColour = SavedColour(System.currentTimeMillis(), r, g, b, false, closestMatch, similarColours, complementaryColours)
        _savedColours.add(newColour)
        viewModelScope.launch(Dispatchers.IO) {
            colourDAO.insertAll(newColour.toSavedColourEntity())
        }
        return newColour
    }

    fun deleteColour(savedColour: SavedColour) {
        _savedColours.remove(savedColour)
        viewModelScope.launch(Dispatchers.IO) {
            colourDAO.delete(savedColour.toSavedColourEntity())
        }
    }

    fun setFavouriteStatus(
        savedColour: SavedColour,
        favouriteStatus: Boolean,
    ) {
        savedColour.favourite = favouriteStatus
        viewModelScope.launch(Dispatchers.IO) {
            colourDAO.updateFavoriteColour(savedColour.id, savedColour.favourite)
        }
    }

    fun setEditingColour(editEvent: EditEvent) {
        _editingColour.value = editEvent
    }
}

class MainViewModelFactory(
    private val colourDAO: ColourDAO,
    private val colourDatabase: ColourDatabase,
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(colourDAO, colourDatabase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
