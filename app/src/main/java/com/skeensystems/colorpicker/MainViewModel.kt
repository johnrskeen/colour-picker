package com.skeensystems.colorpicker

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.skeensystems.colorpicker.database.ColourDAO
import com.skeensystems.colorpicker.database.SavedColour
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(
    private val colourDAO: ColourDAO,
) : ViewModel() {
    private val _savedColours = mutableStateListOf<SavedColour>()
    val savedColours: List<SavedColour> = _savedColours

    init {
        viewModelScope.launch(Dispatchers.IO) {
            colourDAO.all
                .forEach {
                    _savedColours.add(it.toSavedColour())
                }
        }
    }

    // TODO temporary, while colours are manually added, will be removed with full migration
    fun clearSavedColours() {
        _savedColours.clear()
    }

    fun savedColour(savedColour: SavedColour) {
        _savedColours.add(savedColour)
        viewModelScope.launch(Dispatchers.IO) {
            colourDAO.insertAll(savedColour.toSavedColourEntity())
        }
    }

    fun deleteColour(savedColour: SavedColour) {
        _savedColours.remove(savedColour)
        viewModelScope.launch(Dispatchers.IO) {
            colourDAO.delete(savedColour.toSavedColourEntity())
        }
    }

    fun toggleFavourite(savedColour: SavedColour) {
        savedColour.favourite = !savedColour.favourite
        viewModelScope.launch(Dispatchers.IO) {
            colourDAO.updateFavoriteColour(savedColour.id, savedColour.favourite)
        }
    }
}

class MainViewModelFactory(
    private val colourDAO: ColourDAO,
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(colourDAO) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
