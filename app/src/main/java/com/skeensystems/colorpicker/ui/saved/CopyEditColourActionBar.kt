package com.skeensystems.colorpicker.ui.saved

import android.content.ClipData
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.toClipEntry
import androidx.compose.ui.unit.dp
import com.skeensystems.colorpicker.R
import com.skeensystems.colorpicker.database.SavedColour
import com.skeensystems.colorpicker.themeColour
import kotlinx.coroutines.launch

@Composable
fun CopyEditColourActionBar(inspectedColour: SavedColour) {
    val clipboardManager = LocalClipboard.current
    val scope = rememberCoroutineScope()

    Row(modifier = Modifier.fillMaxWidth().padding(10.dp)) {
        ExtendedFloatingActionButton(
            modifier = Modifier.weight(1f).padding(5.dp),
            containerColor = themeColour(R.attr.mainColour),
            contentColor = themeColour(R.attr.defaultTextColour),
            onClick = {
                scope.launch {
                    val text =
                        inspectedColour.getName() +
                            "\nHEX ${inspectedColour.getHEXString()}" +
                            "\nHEX ${inspectedColour.getRGBString()}" +
                            "\nHSV ${inspectedColour.getHSVString()}" +
                            "\nHSL ${inspectedColour.getHSLString()}" +
                            "\nCMYK ${inspectedColour.getCMYKString()}"
                    val clip = ClipData.newPlainText("Colour", text).toClipEntry()
                    clipboardManager.setClipEntry(clip)
                }
            },
            icon = { Icon(Icons.Filled.ContentCopy, "Copy colour details.") },
            text = { Text("Copy") },
        )
        ExtendedFloatingActionButton(
            modifier = Modifier.weight(1f).padding(5.dp),
            containerColor = themeColour(R.attr.mainColour),
            contentColor = themeColour(R.attr.defaultTextColour),
            onClick = { TODO("Not yet implemented") },
            icon = { Icon(Icons.Filled.Edit, "Edit colour details.") },
            text = { Text("Edit") },
        )
    }
}
