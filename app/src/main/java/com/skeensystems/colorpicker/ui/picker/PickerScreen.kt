package com.skeensystems.colorpicker.ui.picker

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.skeensystems.colorpicker.MainViewModel
import com.skeensystems.colorpicker.R
import com.skeensystems.colorpicker.themeColour
import com.skeensystems.colorpicker.ui.CaptureColourButton
import com.skeensystems.colorpicker.ui.picker.finedetails.FineDetailsContainer
import com.skeensystems.colorpicker.ui.picker.slider.PickerSlider

fun ComposeView.setPickerContent() {
    setContent {
        PickerScreen()
    }
}

@Composable
fun PickerScreen(
    mainViewModel: MainViewModel = viewModel(LocalActivity.current as ComponentActivity),
    localViewModel: PickerViewModel = viewModel(LocalActivity.current as ComponentActivity),
) {
    val currentColour by localViewModel.pickerColour.collectAsState()
    val focusManager = LocalFocusManager.current

    val editingColour by mainViewModel.editingColour
    LaunchedEffect(editingColour) {
        editingColour?.colour?.let {
            localViewModel.updateValue(R, it.r.toFloat().adjust(R))
            localViewModel.updateValue(G, it.g.toFloat().adjust(G))
            localViewModel.updateValue(B, it.b.toFloat().adjust(B))
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        floatingActionButton = {
            if (editingColour == null) {
                CaptureColourButton(snackbarHostState = snackbarHostState, colour = currentColour, icon = Icons.Outlined.BookmarkAdd)
            }
        },
        modifier =
            Modifier.pointerInput(Unit) {
                detectTapGestures { focusManager.clearFocus() }
            },
    ) { paddingValues ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(themeColour(R.attr.mainColour))
                    .padding(top = paddingValues.calculateTopPadding()),
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                GradientContainer(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .weight(4.25f)
                            .padding(20.dp),
                )
                PickerSlider(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .padding(bottom = 10.dp, start = 20.dp, end = 20.dp),
                )
                FineDetailsContainer(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .weight(2.5f),
                )
                Row(modifier = Modifier.fillMaxWidth().weight(3f)) {
                    editingColour?.colour?.let {
                        Box(modifier = Modifier.fillMaxHeight().weight(1f).background(it.getColour()))
                    }
                    Box(modifier = Modifier.fillMaxHeight().weight(1f).background(currentColour))
                }
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
            )
        }
    }
}
