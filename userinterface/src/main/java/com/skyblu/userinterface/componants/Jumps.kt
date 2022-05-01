package com.skyblu.userinterface.componants

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.skyblu.models.jump.Jump
import com.skyblu.models.jump.User
import com.skyblu.models.jump.generateSampleJump
import com.skyblu.userinterface.componants.cards.AppJumpCardHeader
import com.skyblu.userinterface.screens.skydiveContent
import timber.log.Timber


@Composable
fun JumpCard(
    skydive: Jump = generateSampleJump(),
    onMapClick : () -> Unit = {},
    username : String = "Meep Moop",
    user : User,
    isMine : Boolean,
    onEditClicked : () -> Unit,
    onProfileClicked : () -> Unit
){





    Column {
        AppJumpCardHeader(skydive = skydive, username = username, user = user, isMine, onEditClicked = {onEditClicked()}, onProfileClicked = {onProfileClicked()})
        Box(modifier = Modifier
            .height(350.dp)
            .fillMaxWidth()){
            StaticGoogleMap(skydive = skydive, onClick = { onMapClick() })
        }
        skydiveContent(skydive = skydive, clip = true)

    }
}