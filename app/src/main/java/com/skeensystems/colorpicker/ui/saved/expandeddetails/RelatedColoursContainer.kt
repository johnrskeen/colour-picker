package com.skeensystems.colorpicker.ui.saved.expandeddetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skeensystems.colorpicker.database.SavedColour

@Composable
fun RelatedColoursContainer(inspectedColour: SavedColour) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(10.dp)
                .background(color = MaterialTheme.colorScheme.background, shape = RoundedCornerShape(20.dp))
                .padding(10.dp),
    ) {
        Text(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            text = "Similar Colours",
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
        )
        inspectedColour.similarColours.forEach {
            DropDownButton(colour = it)
        }

        Text(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            text = "Complementary Colours",
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
        )
        inspectedColour.complementaryColours.forEach {
            DropDownButton(colour = it)
        }
    }
}
