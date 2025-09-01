package com.skeensystems.colorpicker.ui.saved

import android.content.ClipData
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.toClipEntry
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun ColourCodeItem(
    type: String,
    value: String,
    textColour: Color,
    smallText: Boolean = false,
) {
    val clipboardManager = LocalClipboard.current
    val scope = rememberCoroutineScope()

    Row(
        Modifier.fillMaxWidth().padding(10.dp).clickable {
            scope.launch {
                val clip = ClipData.newPlainText(type, "$type $value").toClipEntry()
                clipboardManager.setClipEntry(clip)
            }
        },
    ) {
        Text(
            modifier = Modifier.weight(1f).padding(horizontal = 10.dp),
            text = type,
            textAlign = TextAlign.Start,
            color = textColour,
            fontSize = if (smallText) 16.sp else 18.sp,
            fontWeight = FontWeight.Medium,
        )
        Text(
            modifier = Modifier.weight(1f).padding(horizontal = 10.dp),
            text = value,
            textAlign = TextAlign.End,
            color = textColour,
            fontSize = if (smallText) 16.sp else 18.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}
