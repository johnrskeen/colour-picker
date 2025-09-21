package com.skeensystems.colorpicker

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

@Composable
fun ColourPickerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val colorScheme =
        when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                if (darkTheme) {
                    dynamicDarkColorScheme(
                        context,
                    ).copy(
                        background =
                            Color(
                                context.resources.getColor(
                                    android.R.color.system_neutral2_900,
                                    context.theme,
                                ),
                            ),
                    )
                } else {
                    dynamicLightColorScheme(
                        context,
                    ).copy(
                        background =
                            Color(
                                context.resources.getColor(
                                    android.R.color.system_neutral2_100,
                                    context.theme,
                                ),
                            ),
                    )
                }
            }

            darkTheme ->
                darkColorScheme().copy(
                    primary = Color.LightGray,
                    primaryContainer = Color(30, 30, 30),
                    onPrimaryContainer = Color.White,
                    background = Color(30, 30, 30),
                )

            else ->
                lightColorScheme().copy(
                    primary = Color.Black,
                    primaryContainer = Color.LightGray,
                    onPrimaryContainer = Color.Black,
                    background = Color.LightGray,
                )
        }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
    )
}
