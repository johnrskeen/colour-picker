package com.skeensystems.colorpicker.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.skeensystems.colorpicker.database.ColourDAO
import com.skeensystems.colorpicker.ui.camera.CameraScreen
import com.skeensystems.colorpicker.ui.picker.PickerScreen
import com.skeensystems.colorpicker.ui.saved.SavedColoursScreen
import kotlinx.coroutines.launch

@Composable
fun App(
    navController: NavHostController,
    colourDAO: ColourDAO,
) {
    val pagerState = rememberPagerState(pageCount = { 3 })

    val startDestination = Destination.CAMERA
    var selectedTab by remember { mutableIntStateOf(1) }
    val scope = rememberCoroutineScope()

    Scaffold(
        bottomBar = {
            val currentDestination = navController.currentBackStackEntryAsState().value?.destination
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
        Box(Modifier.padding(bottom = innerPadding.calculateBottomPadding())) {
                HorizontalPager(modifier = Modifier.fillMaxSize(), state = pagerState) { page ->
                    when (page) {
                        0 -> SavedColoursScreen()
                        1 -> CameraScreen()
                        else -> PickerScreen()
                    }
                }
//            Destination.entries.forEachIndexed { index, destination ->
//                AnimatedVisibility(
//                    visible = selectedTab == index,
//                    enter = slideIn(initialOffset = { IntOffset(0, 100) }),
//                    exit = fadeOut(),
//                ) {
//                    stateHolder.SaveableStateProvider(destination) {
//                        when (destination) {
//                            Destination.SAVED -> SavedColoursScreen()
//                            Destination.CAMERA -> CameraScreen()
//                            Destination.PICKER -> PickerScreen()
//                        }
//                    }
//                }
//            }
        }
//        NavHost(
//            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
//            navController = navController,
//            startDestination = startDestination.route,
//            enterTransition = { EnterTransition.None },
//            exitTransition = { ExitTransition.None },
//        ) {
//            Destination.entries.forEach { destination ->
//                composable(destination.route) {
//                    when (destination) {
//                        Destination.SAVED -> SavedColoursScreen()
//                        Destination.CAMERA -> CameraScreen()
//                        Destination.PICKER -> PickerScreen()
//                    }
//                }
//            }
//        }
    }
}

enum class Destination(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val contentDescription: String,
) {
    SAVED("saved", "Saved Colours", Icons.Default.Bookmarks, "Saved Colours"),
    CAMERA("camera", "Camera", Icons.Default.CameraAlt, "Camera"),
    PICKER("picker", "Manual Picker", Icons.Default.ColorLens, "Manual Picker"),
}
