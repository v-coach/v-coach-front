package com.example.vcoach.ui.photo

import android.graphics.Bitmap
import android.net.Uri

data class SelectedPhoto(
    val uri: Uri? = null,
    val bitmap: Bitmap? = null,
)
