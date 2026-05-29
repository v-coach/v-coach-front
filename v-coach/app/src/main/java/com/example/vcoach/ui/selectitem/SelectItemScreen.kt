package com.example.vcoach.ui.selectitem

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.vcoach.ui.components.VCoachPrimaryButton
import com.example.vcoach.ui.components.VCoachTopBar
import com.example.vcoach.ui.photo.SelectedPhoto
import java.io.File

@Composable
fun SelectItemScreen(
    onBackClick: () -> Unit = {},
    onPhotoAdded: (SelectedPhoto) -> Unit = {},
) {
    val context = LocalContext.current
    val foodItems = remember { emptyList<String>() }
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }
    var pendingCameraPath by remember { mutableStateOf<String?>(null) }
    val photoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        val selectedPhoto = if (result.resultCode == Activity.RESULT_OK) {
            result.data
                ?.toSelectedPhoto(
                    context = context,
                    cameraOutputUri = pendingCameraUri,
                    cameraImagePath = pendingCameraPath,
                )
                ?: pendingCameraUri?.let {
                    SelectedPhoto(
                        uri = it,
                        imagePath = pendingCameraPath,
                    )
                }
        } else {
            null
        }

        selectedPhoto?.let(onPhotoAdded)

        pendingCameraUri = null
        pendingCameraPath = null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .background(Color.White),
    ) {
        VCoachTopBar(onBackClick = onBackClick)

        if (foodItems.isEmpty()) {
            EmptyFoodItemContent(
                onAddPhotoClick = {
                    try {
                        val cameraImageFile = createInternalImageFile(context)
                        pendingCameraPath = cameraImageFile.absolutePath
                        pendingCameraUri = createInternalImageUri(context, cameraImageFile)
                        photoLauncher.launch(createPhotoChooserIntent(pendingCameraUri))
                    } catch (_: ActivityNotFoundException) {
                        pendingCameraUri = null
                        pendingCameraPath = null
                    }
                },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

private fun createPhotoChooserIntent(cameraOutputUri: Uri?): Intent {
    val selectPhotoIntent = Intent(Intent.ACTION_GET_CONTENT).apply {
        addCategory(Intent.CATEGORY_OPENABLE)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        type = "image/*"
    }
    val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        cameraOutputUri?.let {
            putExtra(MediaStore.EXTRA_OUTPUT, it)
            clipData = ClipData.newRawUri("camera_output", it)
        }
    }

    return Intent.createChooser(selectPhotoIntent, "사진 추가").apply {
        putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(takePhotoIntent))
    }
}

private fun createInternalImageFile(context: Context): File {
    return File.createTempFile(
        "v-coach-",
        ".jpg",
        context.filesDir,
    )
}

private fun createInternalImageUri(context: Context, imageFile: File): Uri {
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        imageFile,
    )
}

private fun Intent.toSelectedPhoto(
    context: Context,
    cameraOutputUri: Uri?,
    cameraImagePath: String?,
): SelectedPhoto? {
    val selectedUri = data
    val cameraBitmap = getCameraPreviewBitmap()

    return when {
        selectedUri != null -> selectedUri.copyToInternalPhoto(context)
        cameraOutputUri != null -> SelectedPhoto(
            uri = cameraOutputUri,
            imagePath = cameraImagePath,
        )
        cameraBitmap != null -> SelectedPhoto(bitmap = cameraBitmap)
        else -> null
    }
}

private fun Uri.copyToInternalPhoto(context: Context): SelectedPhoto? {
    val imageFile = createInternalImageFile(context)
    context.contentResolver.openInputStream(this)?.use { inputStream ->
        imageFile.outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }
    } ?: return null

    return SelectedPhoto(
        uri = createInternalImageUri(context, imageFile),
        imagePath = imageFile.absolutePath,
    )
}

private fun Intent.getCameraPreviewBitmap(): Bitmap? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        extras?.getParcelable("data", Bitmap::class.java)
    } else {
        @Suppress("DEPRECATION")
        extras?.get("data") as? Bitmap
    }
}

@Composable
private fun EmptyFoodItemContent(
    onAddPhotoClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "사진을 통해 식품을 분석해 보세요",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(28.dp))

        VCoachPrimaryButton(
            text = "사진 추가하기",
            onClick = onAddPhotoClick,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
