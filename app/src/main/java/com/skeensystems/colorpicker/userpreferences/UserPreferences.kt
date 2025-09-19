package com.skeensystems.colorpicker.userpreferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "preferences")

suspend fun <T> Context.loadPreference(
    key: Preferences.Key<T>,
    defaultValue: T,
): T =
    dataStore.data
        .map { preferences -> preferences[key] ?: defaultValue }
        .first()

fun <T> Context.savePreference(
    key: Preferences.Key<T>,
    value: T,
) {
    CoroutineScope(Dispatchers.IO).launch {
        dataStore.edit { preferences ->
            preferences[key] = value
        }
    }
}
