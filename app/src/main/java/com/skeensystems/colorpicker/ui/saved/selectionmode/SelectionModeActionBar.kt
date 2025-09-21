package com.skeensystems.colorpicker.ui.saved.selectionmode

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.skeensystems.colorpicker.measureTextWidth
import com.skeensystems.colorpicker.ui.IconAndTextButton

@Composable
fun SelectionModeActionBar(
    onCancel: () -> Unit,
    onCopy: () -> Unit,
    onSetFavouriteStatus: (favourite: Boolean) -> Unit,
    onDelete: () -> Unit,
) {
    val actionBarItems = getActionBarItems(onCancel, onCopy, onSetFavouriteStatus, onDelete)

    var maxWidth by remember { mutableIntStateOf(0) }

    val defaultStyle = LocalTextStyle.current
    val style = remember { defaultStyle.copy(fontWeight = FontWeight.Medium) }
    maxWidth = actionBarItems.maxOf { measureTextWidth(it.text, style).value.toInt() }

    LazyRow(
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        items(actionBarItems) {
            IconAndTextButton(
                modifier = Modifier.width(maxWidth.dp + 20.dp),
                onClick = it.onClick,
                icon = it.icon,
                text = it.text,
                contentDescription = it.contentDescription,
                colour = MaterialTheme.colorScheme.onSurface,
                afterOnClickPadding = PaddingValues(10.dp),
            )
        }
    }
}
