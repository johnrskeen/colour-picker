package com.skeensystems.colorpicker.ui.saved.selectionmode

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.skeensystems.colorpicker.R
import com.skeensystems.colorpicker.measureTextWidth
import com.skeensystems.colorpicker.themeColour
import com.skeensystems.colorpicker.ui.IconAndTextButton

@Composable
fun SelectionModeActionBar(onCancel: () -> Unit) {
    val actionBarItems = getActionBarItems(onCancel)

    var maxWidth by remember { mutableIntStateOf(0) }

    val style = remember { TextStyle(fontWeight = FontWeight.Medium) }
    maxWidth = actionBarItems.maxOf { measureTextWidth(it.text, style).value.toInt() }

    LazyRow(
        modifier = Modifier.fillMaxWidth().padding(vertical = 0.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        items(actionBarItems) {
            IconAndTextButton(
                modifier = Modifier.padding(20.dp).width(maxWidth.dp),
                onClick = it.onClick,
                icon = it.icon,
                text = it.text,
                contentDescription = it.contentDescription,
                colour = themeColour(R.attr.defaultTextColour),
            )
        }
    }
}
