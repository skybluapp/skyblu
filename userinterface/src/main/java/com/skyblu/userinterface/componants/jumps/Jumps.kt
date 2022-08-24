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
import com.skyblu.userinterface.componants.cards.AppJumpCardHeader
import com.skyblu.userinterface.componants.maps.StaticGoogleMap
import com.skyblu.userinterface.screens.skydiveContent
import timber.log.Timber

/**
 * A composable card used in scrolling screens to represent a single skydive
 * @param skydive The skydive to represent
 * @param onMapClick A function to perform when the map is clicked
 * @param username The username of the user who recorded the jump
 * @param user The user who recorded the jump
 * @param isMine True if the user who recorded the jump is the currently logged in user
 * @param onEditClicked A function to perform when the edit button is clicked
 * @param onProfileClicked A function to perform when the profile button is clicked
 */
@Composable
fun JumpCard(
    skydive: Jump,
    onMapClick : () -> Unit = {},
    username : String = "",
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