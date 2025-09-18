package com.skeensystems.colorpicker.ui.saved.expandeddetails

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.skeensystems.colorpicker.MainViewModel
import com.skeensystems.colorpicker.calculateTextColour
import com.skeensystems.colorpicker.copyToClipboard
import com.skeensystems.colorpicker.database.Colour
import com.skeensystems.colorpicker.database.SavedColour
import com.skeensystems.colorpicker.ui.IconAndTextButton
import kotlinx.coroutines.launch

@Composable
fun DropDownButton(
    viewModel: MainViewModel = viewModel(LocalActivity.current as ComponentActivity),
    colour: Colour,
) {
    var displayingContent by remember { mutableStateOf(false) }

    val clipboardManager = LocalClipboard.current
    val scope = rememberCoroutineScope()

    // TODO provide method to get compose colour
    val colourObject = Color(colour.r, colour.g, colour.b)
    val textColour = colourObject.calculateTextColour()

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(5.dp)
                .background(
                    color = colourObject,
                    shape = RoundedCornerShape(20.dp),
                ).animateContentSize(),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .clickable { displayingContent = !displayingContent },
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = colour.name,
                textAlign = TextAlign.Center,
                color = textColour,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
            )
            Icon(
                modifier = Modifier.align(Alignment.CenterEnd),
                imageVector = if (displayingContent) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                contentDescription = "See more details.",
                tint = textColour,
            )
        }

        if (displayingContent) {
            // TODO use same method as in SavedColour for DatabaseColour details
            ColourCodeItem(type = "HEX", value = colour.getHEXString(), textColour = textColour, smallText = true)
            ColourCodeItem(type = "RGB", value = colour.getRGBString(), textColour = textColour, smallText = true)
            ColourCodeItem(type = "HSV", value = colour.getHSVString(), textColour = textColour, smallText = true)
            ColourCodeItem(type = "HSL", value = colour.getHSLString(), textColour = textColour, smallText = true)
            ColourCodeItem(type = "CMYK", value = colour.getCMYKString(), textColour = textColour, smallText = true)
            Row(modifier = Modifier.fillMaxWidth()) {
                IconAndTextButton(
                    modifier = Modifier.weight(1f).padding(20.dp),
                    onClick = {
                        scope.launch {
                            colour.copyToClipboard(clipboardManager = clipboardManager)
                        }
                    },
                    icon = Icons.Filled.ContentCopy,
                    text = "Copy",
                    contentDescription = "Copy colour details.",
                    colour = textColour,
                )
                IconAndTextButton(
                    modifier = Modifier.weight(1f).padding(20.dp),
                    onClick = { viewModel.saveColour(colour.r, colour.g, colour.b) },
                    icon = Icons.Outlined.BookmarkAdd,
                    text = "Save",
                    contentDescription = "Save colour.",
                    colour = textColour,
                )
            }
        }
    }
}
