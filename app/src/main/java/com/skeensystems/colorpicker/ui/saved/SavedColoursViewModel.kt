package com.skeensystems.colorpicker.ui.saved

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.skeensystems.colorpicker.database.SavedColour
import com.skeensystems.colorpicker.ui.saved.expandeddetails.VisibilityStatus
import com.skeensystems.colorpicker.ui.saved.sortandfilter.FilterOptions
import com.skeensystems.colorpicker.ui.saved.sortandfilter.SortOptions
import com.skeensystems.colorpicker.userpreferences.PreferenceKeys
import com.skeensystems.colorpicker.userpreferences.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SavedColoursViewModel(
    application: Application,
) : AndroidViewModel(application) {
    private val _selectingMode = mutableStateOf(false)
    val selectingMode: State<Boolean> = _selectingMode

    private val _selectedItems = mutableStateSetOf<SavedColour>()
    val selectedItems: Set<SavedColour> = _selectedItems

    private val _visibilityStatus = mutableStateOf<VisibilityStatus>(VisibilityStatus.Hide(Offset(0f, 0f)))
    val visibilityStatus: State<VisibilityStatus> = _visibilityStatus

    private val _sortStatus = mutableStateOf(SortOptions.NEWEST_FIRST)
    val sortStatus: State<SortOptions> = _sortStatus

    private val _filterStatus = mutableStateOf(FilterOptions.NO_FILTER)
    val filterStatus: State<FilterOptions> = _filterStatus

    val savedColourCoordinates = mutableMapOf<Long, Offset>()

    var animationDuration = 0
    var screenWidth = 0.dp
    var screenHeight = 0.dp
    var colourViewDimension = 0.dp

    private val context = getApplication<Application>()

    init {
        viewModelScope.launch {
            _sortStatus.value =
                when (
                    context.dataStore.data
                        .map { prefs -> prefs[PreferenceKeys.SORT_STATUS] ?: "Newest first" }
                        .first()
                ) {
                    "Newest first" -> SortOptions.NEWEST_FIRST
                    "Oldest first" -> SortOptions.OLDEST_FIRST
                    else -> SortOptions.BY_COLOUR
                }

            _filterStatus.value =
                when (
                    context.dataStore.data
                        .map { prefs -> prefs[PreferenceKeys.FILTER_STATUS] ?: "No filter" }
                        .first()
                ) {
                    "No filter" -> FilterOptions.NO_FILTER
                    else -> FilterOptions.FAVOURITES
                }
        }
    }

    fun enterSelectingMode(savedColour: SavedColour) {
        _selectingMode.value = true
        addSelectedItem(savedColour)
    }

    fun exitSelectingMode() {
        _selectedItems.clear()
        _selectingMode.value = false
    }

    fun addSelectedItem(savedColour: SavedColour) {
        _selectedItems.add(savedColour)
    }

    fun removeSelectedItem(savedColour: SavedColour) {
        _selectedItems.remove(savedColour)
        if (_selectedItems.isEmpty()) _selectingMode.value = false
    }

    fun setVisibilityStatus(visibilityStatus: VisibilityStatus) {
        _visibilityStatus.value = visibilityStatus
    }

    fun setSortStatus(sortStatus: SortOptions) {
        _sortStatus.value = sortStatus
        viewModelScope.launch {
            context.dataStore.edit { preferences ->
                preferences[PreferenceKeys.SORT_STATUS] = sortStatus.label
            }
        }
    }

    fun setFilterStatus(filterStatus: FilterOptions) {
        _filterStatus.value = filterStatus
        viewModelScope.launch {
            context.dataStore.edit { preferences ->
                preferences[PreferenceKeys.FILTER_STATUS] = filterStatus.label
            }
        }
    }
}
