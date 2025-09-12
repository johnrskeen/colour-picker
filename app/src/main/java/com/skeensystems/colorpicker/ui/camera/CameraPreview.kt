package com.skeensystems.colorpicker.ui.camera

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.camera.compose.CameraXViewfinder
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.viewfinder.core.ImplementationMode
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.skeensystems.colorpicker.GetColour
import com.skeensystems.colorpicker.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

@Composable
fun CameraPreview(viewModel: CameraViewModel = viewModel(LocalActivity.current as ComponentActivity)) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var surfaceRequest by remember { mutableStateOf<SurfaceRequest?>(null) }

    LaunchedEffect(Unit) {
        var lastFrame = System.currentTimeMillis() - 100
        val cameraProvider = ProcessCameraProvider.getInstance(context).get()

        val preview =
            Preview.Builder().build().also { preview ->
                preview.setSurfaceProvider { request ->
                    surfaceRequest = request
                }
            }

        val imageAnalyzer =
            ImageAnalysis
                .Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

        imageAnalyzer.setAnalyzer(Executors.newSingleThreadExecutor()) { image ->

            val includeFrame = System.currentTimeMillis() >= lastFrame + 100
            if (includeFrame) lastFrame = System.currentTimeMillis()

            // TODO tidy this up when full migration complete
            if (MainActivity.onCamera && includeFrame) {
                val colour = GetColour.getColour(image)

                val r = Math.toIntExact(Math.round(colour.component1()))
                val g = Math.toIntExact(Math.round(colour.component2()))
                val b = Math.toIntExact(Math.round(colour.component3()))

                viewModel.updateColour(r, g, b)

                launch(Dispatchers.Main) {
                    MainActivity.mainActivityViewModel.cameraR = r
                    MainActivity.mainActivityViewModel.cameraG = g
                    MainActivity.mainActivityViewModel.cameraB = b
                }
            }
            image.close()
        }

        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            CameraSelector.DEFAULT_BACK_CAMERA,
            preview,
            imageAnalyzer,
        )

        awaitCancellation()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        surfaceRequest?.let { request ->
            CameraXViewfinder(
                surfaceRequest = request,
                implementationMode = ImplementationMode.EMBEDDED,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
