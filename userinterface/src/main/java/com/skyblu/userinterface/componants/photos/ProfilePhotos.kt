package com.skyblu.userinterface.componants.photos

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.skyblu.userinterface.R

/**
 * A composable circular profile picture image
 * @param size size of the image
 * @param image an ImageBitmap to display
 * @param onClick a function that is run when the image is clicked
 */
@Composable
fun AppDisplayPhoto(
    size: Dp,
    image: ImageBitmap?,
    onClick: () -> Unit
) {
    if (image == null) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data("https://media.wired.com/photos/5b899992404e112d2df1e94e/master/pass/trash2-01.jpg")
                .crossfade(true)
                .build(),
            contentDescription = "barcode image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(size = size)
                .clip(CircleShape)
                .clickable { onClick() }
                .background(Color.LightGray),
        )
    } else {
        Image(
            bitmap = image,
            contentDescription = "avatar",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(size = size)
                .clip(CircleShape)
                .clickable { onClick() }
                .background(Color.LightGray),
        )
    }
}

@Composable
@Preview(showBackground = true)
fun AppDisplayPhotoPreview() {
    val bitmap = BitmapFactory.decodeResource(
        LocalContext.current.resources,
        R.drawable.skydiver
    ).asImageBitmap()
    AppDisplayPhoto(
        size = 60.dp,
        image = bitmap,
        onClick = {}
    )
}
