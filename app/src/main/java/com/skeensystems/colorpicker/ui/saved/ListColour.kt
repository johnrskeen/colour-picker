package com.skeensystems.colorpicker.ui.saved

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionOnScreen
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.skeensystems.colorpicker.database.SavedColour

@Composable
fun ListColour(
    savedColour: SavedColour,
    onDimensionMeasured: (Dp) -> Unit,
    onCoordinatesDetermined: (Offset) -> Unit,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    val colour = Color(savedColour.getR(), savedColour.getG(), savedColour.getB())
    BoxWithConstraints(
        modifier =
            Modifier
                .fillMaxSize()
                .aspectRatio(1f)
                .padding(2.dp)
                .background(
                    color = colour,
                    shape = RoundedCornerShape(10.dp),
                ).onGloballyPositioned { coordinates ->
                    onCoordinatesDetermined(coordinates.positionOnScreen())
                }.combinedClickable(
                    onClick = onClick,
                    onLongClick = onLongClick,
                ),
    ) {
        onDimensionMeasured(maxHeight)
    }
}
