package com.skeensystems.colorpicker.ui.saved

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionOnScreen
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.skeensystems.colorpicker.R
import com.skeensystems.colorpicker.database.SavedColour

@Composable
fun ListColour(
    modifier: Modifier = Modifier,
    savedColour: SavedColour,
    selected: Boolean,
    animationDuration: Int,
    onDimensionMeasured: (Dp) -> Unit,
    onCoordinatesDetermined: (Offset) -> Unit,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    var selectedCornerRadius = 20f
    val scale by animateFloatAsState(
        targetValue = if (selected) 0.8f else 1f,
        animationSpec = tween(durationMillis = animationDuration),
    )
    val cornerRadius by animateFloatAsState(
        targetValue = if (selected) selectedCornerRadius else 10f,
        animationSpec = tween(durationMillis = animationDuration),
    )
    BoxWithConstraints(
        modifier =
            modifier
                .fillMaxSize()
                .aspectRatio(1f)
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                ).padding(2.dp)
                .background(
                    color = savedColour.getColour(),
                    shape = RoundedCornerShape(cornerRadius.dp),
                ).onGloballyPositioned { coordinates ->
                    onCoordinatesDetermined(coordinates.positionOnScreen())
                }.combinedClickable(
                    onClick = onClick,
                    onLongClick = onLongClick,
                ),
    ) {
        selectedCornerRadius = maxHeight.value
        onDimensionMeasured(maxHeight)

        if (savedColour.favourite) {
            Icon(
                modifier = Modifier.size(maxHeight / 3.5f, maxHeight / 3.5f),
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = savedColour.textColour,
            )
        }

        if (selected) {
            Icon(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(id = R.drawable.ic_tick),
                contentDescription = null,
                tint = savedColour.textColour,
            )
        }
    }
}
