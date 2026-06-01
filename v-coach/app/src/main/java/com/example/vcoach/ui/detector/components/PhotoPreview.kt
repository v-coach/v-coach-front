package com.example.vcoach.ui.detector.components

import android.widget.ImageView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.vcoach.ui.photo.SelectedPhoto
import com.example.vcoach.ui.theme.VCoachBorderGray
import com.example.vcoach.ui.theme.VCoachLightGreen

@Composable
fun PhotoPreview(
    selectedPhoto: SelectedPhoto? = null,
    modifier: Modifier = Modifier,
) {
    val previewModifier = modifier
        .padding(horizontal = 16.dp)
        .border(
            width = 1.dp,
            color = VCoachBorderGray,
            shape = RoundedCornerShape(12.dp),
        )
        .clip(RoundedCornerShape(12.dp))
        .background(VCoachLightGreen)

    if (selectedPhoto == null) {
        Box(modifier = previewModifier)
    } else {
        AndroidView(
            modifier = previewModifier,
            factory = { context ->
                ImageView(context).apply {
                    setBackgroundColor(android.graphics.Color.WHITE)
                    scaleType = ImageView.ScaleType.CENTER_CROP
                }
            },
            update = { imageView ->
                when {
                    selectedPhoto.uri != null -> imageView.setImageURI(selectedPhoto.uri)
                    selectedPhoto.bitmap != null -> imageView.setImageBitmap(selectedPhoto.bitmap)
                }
            },
        )
    }
}
