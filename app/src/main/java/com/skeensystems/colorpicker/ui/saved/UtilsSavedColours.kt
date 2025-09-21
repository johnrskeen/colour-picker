package com.skeensystems.colorpicker.ui.saved

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import com.skeensystems.colorpicker.database.SavedColour
import com.skeensystems.colorpicker.ui.saved.sortandfilter.FilterOptions
import com.skeensystems.colorpicker.ui.saved.sortandfilter.SortOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

fun animateDetailsView(
    scope: CoroutineScope,
    animationDuration: Int,
    x: Animatable<Float, AnimationVector1D>,
    xTo: Float,
    y: Animatable<Float, AnimationVector1D>,
    yTo: Float,
    width: Animatable<Float, AnimationVector1D>,
    widthTo: Float,
    height: Animatable<Float, AnimationVector1D>,
    heightTo: Float,
    afterAnimation: () -> Unit = {},
) {
    val animationSpec = tween<Float>(durationMillis = animationDuration)
    scope.launch {
        awaitAll(
            async {
                x.animateTo(targetValue = xTo, animationSpec = animationSpec)
            },
            async {
                y.animateTo(targetValue = yTo, animationSpec = animationSpec)
            },
            async {
                width.animateTo(targetValue = widthTo, animationSpec = animationSpec)
            },
            async {
                height.animateTo(targetValue = heightTo, animationSpec = animationSpec)
            },
        )
        afterAnimation()
    }
}

fun List<SavedColour>.filter(filterStatus: FilterOptions): List<SavedColour> =
    when (filterStatus) {
        FilterOptions.NO_FILTER -> this
        FilterOptions.FAVOURITES -> filter { it.favourite }
    }

fun List<SavedColour>.sort(sortStatus: SortOptions): List<SavedColour> =
    when (sortStatus) {
        SortOptions.NEWEST_FIRST -> sortedByDescending { it.id }
        SortOptions.OLDEST_FIRST -> sortedBy { it.id }
        SortOptions.BY_COLOUR -> sortedByDescending { it.getSortValue() }
    }
