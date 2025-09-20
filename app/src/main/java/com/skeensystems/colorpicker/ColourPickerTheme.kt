package com.skeensystems.colorpicker

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
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
                    ).copy(background = Color(context.resources.getColor(android.R.color.system_neutral2_900, context.theme)))
                } else {
                    dynamicLightColorScheme(
                        context,
                    ).copy(background = Color(context.resources.getColor(android.R.color.system_neutral2_100, context.theme)))
                }
            }
            darkTheme -> darkColorScheme().copy(primary = Color.LightGray, background = Color.DarkGray)
            else -> lightColorScheme().copy(primary = Color.Black, background = Color.LightGray)
        }

    val extendedColorScheme =
        when {
            dynamicColor || Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                if (darkTheme) {
                    dynamicExtendedDark(context)
                } else {
                    dynamicExtendedLight(context)
                }
            }
            darkTheme -> extendedDark()
            else -> extendedLight()
        }

    CompositionLocalProvider(
        LocalColourScheme provides extendedColorScheme,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content,
        )
    }
}

@Immutable
data class ExtendedColorScheme(
    val background: Color,
)

@RequiresApi(Build.VERSION_CODES.S)
fun dynamicExtendedLight(context: Context) =
    ExtendedColorScheme(
        background = Color(context.resources.getColor(android.R.color.system_neutral2_100, context.theme)),
    )

@RequiresApi(Build.VERSION_CODES.S)
fun dynamicExtendedDark(context: Context) =
    ExtendedColorScheme(background = Color(context.resources.getColor(android.R.color.system_neutral2_900, context.theme)))

fun extendedLight() = ExtendedColorScheme(background = Color.LightGray)

fun extendedDark() = ExtendedColorScheme(background = Color.DarkGray)

val LocalColourScheme =
    staticCompositionLocalOf {
        ExtendedColorScheme(
            background = Color.White,
        )
    }
