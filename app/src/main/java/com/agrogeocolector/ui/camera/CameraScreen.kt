package com.agrogeocolector.ui.camera

import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.agrogeocolector.util.ImageFileUtils
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Tela de captura de foto usando CameraX.
 * 
 * Features:
 * - Preview em tempo real
 * - Captura com alta qualidade
 * - Compressão automática via ImageFileUtils
 * - Retorna o path da foto salva
 * 
 * Uso:
 * ```kotlin
 * CameraScreen(
 *     onPhotoCaptured = { path ->
 *         viewModel.addSample(lat, lng, photoPath = path)
 *     },
 *     onDismiss = { navController.popBackStack() }
 * )
 * ```
 */
@Composable
fun CameraScreen(
    onPhotoCaptured: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var isCapturing by remember { mutableStateOf(false) }
    
    Box(modifier = Modifier.fillMaxSize()) {
        // Preview da câmera
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    
                    // Preview use case
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
                    
                    // ImageCapture use case
                    imageCapture = ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                        .build()
                    
                    // Seleciona câmera traseira
                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                    
                    try {
                        // Desvincula tudo antes de rebind
                        cameraProvider.unbindAll()
                        
                        // Vincula use cases ao lifecycle
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageCapture
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    
                }, ContextCompat.getMainExecutor(ctx))
                
                previewView
            },
            modifier = Modifier.fillMaxSize()
        )
        
        // Botões de controle
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Botão de captura
            FloatingActionButton(
                onClick = {
                    scope.launch {
                        isCapturing = true
                        val photoUri = takePicture(context, imageCapture)
                        
                        if (photoUri != null) {
                            // Comprime e salva
                            val savedPath = ImageFileUtils.saveAndCompressImage(context, photoUri)
                            
                            if (savedPath != null) {
                                onPhotoCaptured(savedPath)
                            }
                        }
                        
                        isCapturing = false
                    }
                },
                modifier = Modifier.size(72.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                if (isCapturing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(
                        Icons.Default.PhotoCamera,
                        contentDescription = "Capturar foto",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Botão de cancelar
            OutlinedButton(
                onClick = onDismiss,
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                )
            ) {
                Text("Cancelar")
            }
        }
    }
}

/**
 * Tira a foto usando ImageCapture.
 * Retorna a URI do arquivo temporário.
 */
private suspend fun takePicture(
    context: android.content.Context,
    imageCapture: ImageCapture?
): Uri? = suspendCoroutine { continuation ->
    
    if (imageCapture == null) {
        continuation.resume(null)
        return@suspendCoroutine
    }
    
    // Cria arquivo temporário
    val photoFile = File(
        context.cacheDir,
        SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
            .format(System.currentTimeMillis()) + "_temp.jpg"
    )
    
    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
    
    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                continuation.resume(Uri.fromFile(photoFile))
            }
            
            override fun onError(exception: ImageCaptureException) {
                exception.printStackTrace()
                continuation.resume(null)
            }
        }
    )
}
