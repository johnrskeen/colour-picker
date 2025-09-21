package com.skeensystems.colorpicker.ui

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.skeensystems.colorpicker.MainViewModel
import com.skeensystems.colorpicker.MainViewModelFactory
import com.skeensystems.colorpicker.database.ColourDAO
import com.skeensystems.colorpicker.database.ColourDatabase
import com.skeensystems.colorpicker.ui.ads.AdContainer
import com.skeensystems.colorpicker.ui.camera.CameraScreen
import com.skeensystems.colorpicker.ui.picker.PickerScreen
import com.skeensystems.colorpicker.ui.saved.SavedColoursScreen
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun App(colourDAO: ColourDAO) {
    val colourDatabase = ColourDatabase(LocalContext.current)
    val viewModel: MainViewModel =
        viewModel(
            factory = MainViewModelFactory(colourDAO, colourDatabase),
            viewModelStoreOwner = LocalActivity.current as ComponentActivity,
        )

    val pagerState = rememberPagerState(pageCount = { 3 }, initialPage = 1)
    var selectedTab by remember { mutableIntStateOf(1) }
    val scope = rememberCoroutineScope()

    val editingColour by viewModel.editingColour
    LaunchedEffect(editingColour) {
        editingColour?.let {
            scope.launch {
                pagerState.scrollToPage(2)
            }
        }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .collectLatest { page ->
                selectedTab = page
                viewModel.setOnCamera(page == 1)
                if (editingColour != null && page != 2) {
                    viewModel.clearEditingColour()
                }
            }
    }

    Scaffold(
        bottomBar = {
            NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
                Destination.entries.forEachIndexed { index, destination ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = {
                            selectedTab = index
                            scope.launch {
                                pagerState.scrollToPage(index)
                            }
                        },
                        icon = {
                            Icon(
                                destination.icon,
                                contentDescription = destination.contentDescription,
                            )
                        },
                        label = { Text(destination.label) },
                    )
                }
            }
        },
    ) { innerPadding ->
        BoxWithConstraints(modifier = Modifier.fillMaxSize().padding(bottom = innerPadding.calculateBottomPadding())) {
            val width = maxWidth
            Column(modifier = Modifier.fillMaxSize()) {
                HorizontalPager(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    state = pagerState,
                    beyondViewportPageCount = 3,
                ) { page ->
                    when (page) {
                        0 -> SavedColoursScreen()
                        1 -> CameraScreen()
                        else -> PickerScreen()
                    }
                }
                AdContainer(width = width)
            }
        }
    }
}
