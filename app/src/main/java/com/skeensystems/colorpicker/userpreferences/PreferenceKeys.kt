package com.skeensystems.colorpicker.userpreferences

import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferenceKeys {
    val SORT_STATUS = stringPreferencesKey("sort_status")
    val FILTER_STATUS = stringPreferencesKey("filter_status")
    val PICKER_R = intPreferencesKey("picker_r")
    val PICKER_G = intPreferencesKey("picker_g")
    val PICKER_B = intPreferencesKey("picker_b")
}
