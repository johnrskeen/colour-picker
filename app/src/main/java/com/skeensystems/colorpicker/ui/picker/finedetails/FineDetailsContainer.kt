package com.skeensystems.colorpicker.ui.picker.finedetails

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

// TODO add temporary colours to replace R.attr as buttons will be replaced with tabs
@Composable
fun FineDetailsContainer(modifier: Modifier = Modifier) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()
    Row(modifier = modifier) {
        Column(modifier = Modifier.fillMaxHeight().wrapContentWidth()) {
            listOf(ColourSystem.RGB, ColourSystem.HEX, ColourSystem.HSV).forEachIndexed { index, title ->
                Button(
                    onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                if (pagerState.currentPage ==
                                    index
                                ) {
                                    Color.LightGray // themeColour(R.attr.reversedColourAccent)
                                } else {
                                    Color.DarkGray // themeColour(R.attr.mainColourAccent)
                                },
                        ),
                    modifier =
                        Modifier
                            .weight(1f)
                            .padding(4.dp),
                ) {
                    Text(title.name, maxLines = 1)
                }
            }
        }
        VerticalPager(modifier = Modifier.fillMaxHeight().weight(1f), state = pagerState) { page ->
            when (page) {
                0 -> FineDetailsPage(colourSystem = ColourSystem.RGB)
                1 -> FineDetailsPage(colourSystem = ColourSystem.HEX)
                else -> FineDetailsPage(colourSystem = ColourSystem.HSV)
            }
        }
    }
}
