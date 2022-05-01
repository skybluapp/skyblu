package com.skyblu.userinterface.componants.data

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.skyblu.configuration.*
import com.skyblu.models.jump.User
import timber.log.Timber

@Composable
fun ProfileHeader(user: User) {
    Column {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(
                modifier = Modifier.height(100.dp),
                verticalArrangement = Arrangement.Center
            ) {
                LabelledText(
                    user.jumpNumber.toString(),
                    TOTAL_JUMPS_STRING
                )
            }

            val data = if(user.photoUrl.isNullOrBlank() || user.photoUrl.isNullOrEmpty() || user.photoUrl == "null"){
                "https://firebasestorage.googleapis.com/v0/b/skyblu-skydiving-tracker.appspot.com/o/profilePictures%2FEmptyProfilePic.png?alt=media&token=b3360aec-bfde-4dd3-b519-039a8f0e8dca"
            } else {
                user.photoUrl

            }

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(data)
                    .crossfade(true)
                    .build(),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp)
                    .size(100.dp)
                    .clip(CircleShape)
            )
            Column(
                modifier = Modifier.height(100.dp),
                verticalArrangement = Arrangement.Center
            ) {
                LabelledText(
                    user.licence.toString(),
                    LICENCE_STRING
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(MEDIUM_PADDING)
        ) {
            Text(
                text = user.bio,
                fontWeight = FontWeight.Bold
            )
        }
    }
}