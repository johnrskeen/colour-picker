package com.skeensystems.colorpicker.ui.camera

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.skeensystems.colorpicker.ui.camera.requestpermission.CameraPermissionDeniedScreen

fun ComposeView.setCameraContent() {
    setContent {
        CameraScreen()
    }
}

@Composable
fun CameraScreen(viewModel: CameraViewModel = viewModel(LocalActivity.current as ComponentActivity)) {
    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA,
            ) == PackageManager.PERMISSION_GRANTED,
        )
    }

    val launcher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
        ) { isGranted: Boolean ->
            hasPermission = isGranted
        }

    LaunchedEffect(Unit) {
        if (!hasPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    val targetedColour by viewModel.cameraColour.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        floatingActionButton = {
            if (hasPermission) CaptureColourButton(snackbarHostState = snackbarHostState, colour = targetedColour, icon = Icons.Filled.Add)
        },
    ) { paddingValues ->
        if (hasPermission) {
            Box(modifier = Modifier.fillMaxSize()) {
                CameraPreview()

                CameraPickerTarget(modifier = Modifier.align(Alignment.Center))

                CameraTargetedColourDetails(
                    modifier = Modifier.padding(paddingValues).align(Alignment.TopCenter),
                )

                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter),
                )
            }
        } else {
            CameraPermissionDeniedScreen(context = context)
        }
    }
}
