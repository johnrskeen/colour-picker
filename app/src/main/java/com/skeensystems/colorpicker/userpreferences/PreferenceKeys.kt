package com.skeensystems.colorpicker.userpreferences

import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferenceKeys {
    val SORT_STATUS = stringPreferencesKey("sort_status")
    val FILTER_STATUS = stringPreferencesKey("filter_status")
    val PICKER_R = floatPreferencesKey("picker_r")
    val PICKER_G = floatPreferencesKey("picker_g")
    val PICKER_B = floatPreferencesKey("picker_b")
}
