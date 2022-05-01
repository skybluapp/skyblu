package com.skyblu.userinterface.componants.icons

import androidx.annotation.DrawableRes
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.skyblu.configuration.Concept

@Composable
fun BasicIcon(list: List<Concept>) {
    for (action in list) {
        Icon(
            painter = painterResource(id = action.icon),
            contentDescription = null,
            tint = MaterialTheme.colors.onBackground
        )
    }
}

@Composable
fun BasicIcon(@DrawableRes icon:  Int) {
    Icon(
        painter = painterResource(id = icon),
        contentDescription = null,
        tint = MaterialTheme.colors.onBackground
    )
}
