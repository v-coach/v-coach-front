package com.example.vcoach.ui.selectitem.components

import android.net.Uri
import android.widget.ImageView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.vcoach.ui.theme.VCoachBorderGray
import com.example.vcoach.ui.theme.VCoachDisabledGray
import java.io.File

@Composable
fun FoodItemCard(
    foodName: String,
    imagePath: String?,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        ),
        border = BorderStroke(
            width = 2.dp,
            color = VCoachBorderGray,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FoodThumbnail(imagePath = imagePath)

            Spacer(modifier = Modifier.width(18.dp))

            Text(
                text = foodName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
            )
        }
    }
}

@Composable
private fun FoodThumbnail(
    imagePath: String?,
    modifier: Modifier = Modifier,
) {
    val thumbnailModifier = modifier
        .size(78.dp)
        .clip(RoundedCornerShape(10.dp))
        .background(VCoachDisabledGray)

    if (imagePath.isNullOrBlank()) {
        Box(modifier = thumbnailModifier)
    } else {
        AndroidView(
            modifier = thumbnailModifier,
            factory = { context ->
                ImageView(context).apply {
                    scaleType = ImageView.ScaleType.CENTER_CROP
                    setBackgroundColor(android.graphics.Color.WHITE)
                }
            },
            update = { imageView ->
                imageView.setImageURI(Uri.fromFile(File(imagePath)))
            },
        )
    }
}
