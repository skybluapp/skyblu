package com.skyblu.userinterface.componants.cards

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.skyblu.configuration.SMALL_PADDING
import com.skyblu.configuration.UNKNOWN_USER_STRING
import com.skyblu.configuration.millisToDateString
import com.skyblu.models.jump.Jump
import com.skyblu.models.jump.User
import com.skyblu.userinterface.R

@Composable
fun AppJumpCardHeader(
    skydive: Jump,
    username: String,
    user: User,
    isMine: Boolean,
    onEditClicked: () -> Unit,
    onProfileClicked: () -> Unit,
) {

    val data = if(user.photoUrl.isNullOrBlank() || user.photoUrl.isNullOrEmpty() || user.photoUrl == "null"){
        "https://firebasestorage.googleapis.com/v0/b/skyblu-skydiving-tracker.appspot.com/o/profilePictures%2FEmptyProfilePic.png?alt=media&token=b3360aec-bfde-4dd3-b519-039a8f0e8dca"
    } else {
        user.photoUrl
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = SMALL_PADDING),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            Modifier
                .padding(SMALL_PADDING)
                .weight(2F), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(data)
                    .crossfade(true)
                    .build(),
                contentDescription = "barcode image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(45.dp)
                    .clip(CircleShape)
                    .clickable {
                        onProfileClicked()
                    }

            )
        }
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.weight(8F)
        ) {
            Text(
                text = username,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.h6
            )
            Text(
                text = "${
                    if (!skydive.dropzone.title.isNullOrBlank()) {
                        skydive.dropzone.title + " . "
                    } else {
                        ""
                    }
                }${skydive.date.millisToDateString()}"
            )

        }
        if (isMine) {
            IconButton(onClick = {
                onEditClicked()
            }, modifier = Modifier.weight(2F)) {
                Icon(
                    painter = painterResource(id = R.drawable.more),
                    contentDescription = "Options"
                )
            }
        }
    }
}


@Composable
fun UserCard(
    user: User,
    onProfileClicked: () -> Unit = {},
    onAddFriendButtonClicked: () -> Unit = {},
    onRemoveFriendButtonClicked: () -> Unit = {},
    onClick : () -> Unit = {},
    isFriend: Boolean = false
) {


    Row(
        modifier = Modifier
            .fillMaxWidth().clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            Modifier
                .padding(SMALL_PADDING)
                .weight(2F), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(user.photoUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "barcode image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(45.dp)
                    .clip(CircleShape)
                    .clickable {
                        onProfileClicked()
                    }

            )
        }
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.weight(8F)
        ) {
            Text(
                text = user.username ?: UNKNOWN_USER_STRING,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.h6
            )

        }

        IconButton(onClick = {
                             if(!isFriend){
                                 onAddFriendButtonClicked()
                             } else {
                                 onRemoveFriendButtonClicked()
                             }

        }, modifier = Modifier.weight(2F)) {
            if(isFriend){
                Icon(
                    painter = painterResource(id = R.drawable.unfreind),
                    contentDescription = "Add Friend"
                )
            } else {

                Icon(
                    painter = painterResource(id = R.drawable.add_friend),
                    contentDescription = "Add Friend"
                )
            }

        }
    }
}