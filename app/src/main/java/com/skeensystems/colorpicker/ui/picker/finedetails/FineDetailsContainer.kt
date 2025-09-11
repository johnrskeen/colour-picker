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
import androidx.compose.ui.unit.dp
import com.skeensystems.colorpicker.R
import com.skeensystems.colorpicker.themeColour
import kotlinx.coroutines.launch

@Composable
fun FineDetailsContainer(modifier: Modifier = Modifier) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()
    Row(modifier = modifier) {
        Column(modifier = Modifier.fillMaxHeight().wrapContentWidth()) {
            listOf(FineDetailsOptions.RGB, FineDetailsOptions.HEX, FineDetailsOptions.HSV).forEachIndexed { index, title ->
                Button(
                    onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                if (pagerState.currentPage ==
                                    index
                                ) {
                                    themeColour(R.attr.reversedColourAccent)
                                } else {
                                    themeColour(R.attr.mainColourAccent)
                                },
                        ),
                    modifier =
                        Modifier
                            .weight(1f)
                            .padding(4.dp),
                ) {
                    Text(title.name, color = themeColour(R.attr.defaultTextColour), maxLines = 1)
                }
            }
        }
        VerticalPager(modifier = Modifier.fillMaxHeight().weight(1f), state = pagerState) { page ->
            when (page) {
                0 -> FineDetailsPage(colourSystem = FineDetailsOptions.RGB)
                1 -> FineDetailsPage(colourSystem = FineDetailsOptions.HEX)
                else -> FineDetailsPage(colourSystem = FineDetailsOptions.HSV)
            }
        }
    }
}
