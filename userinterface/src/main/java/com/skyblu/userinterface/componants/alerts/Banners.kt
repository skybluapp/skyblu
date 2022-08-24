package com.skyblu.userinterface.componants.alerts

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.skyblu.configuration.Concept
import com.skyblu.configuration.MEDIUM_PADDING
import com.skyblu.configuration.success
import com.skyblu.configuration.warning
import com.skyblu.userinterface.componants.ActionConcept
import com.skyblu.userinterface.viewmodels.Alert

/**
 * A composable for a banner that can be displayed at the top of a screen
 * @param text The text to display in the banner
 * @param actionConcept The
 * @param importance importance of the banner
 */
@Composable
fun AppBanner(
    text: String,
    actionConcept: ActionConcept,
    importance : Alert
) {
    AnimatedVisibility(visible = text.isNotEmpty()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = when (importance) {
                        Alert.SUCCESS -> MaterialTheme.colors.success()
                        Alert.WARNING -> MaterialTheme.colors.warning()
                        Alert.ERROR -> MaterialTheme.colors.error
                    }
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = text,
                Modifier
                    .weight(9F)
                    .padding(start = MEDIUM_PADDING),
                fontWeight = FontWeight.Bold
            )
            IconButton(
                onClick = { actionConcept.action() },
                modifier = Modifier.weight(1F)
            ) {
                Icon(
                    painter = painterResource(id = actionConcept.concept.icon),
                    contentDescription = actionConcept.concept.title
                )
            }
        }
    }

}

