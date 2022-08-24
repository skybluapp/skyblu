package com.skyblu.userinterface.componants.scaffold

import androidx.compose.foundation.layout.Row
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.skyblu.configuration.Concept
import com.skyblu.userinterface.componants.ActionConcept
import com.skyblu.userinterface.componants.input.ActionConceptList

/**
 * A composable configurable Top bar for screens of the app
 * @param title A string to display in the top bar
 * @param color The color of the Top bar
 * @param navigationIcon A composable icon to display on the left side of the app bar
 * @param actionIcons Composable icons to display on the right of the top bar
 */
@Composable
fun AppTopAppBar(
    title: String,
    color: Color = MaterialTheme.colors.background,
    navigationIcon: @Composable (() -> Unit)? = null,
    actionIcons: @Composable () -> Unit = {},
) {
    TopAppBar(
        title = { Text(text = title) },
        backgroundColor = color,
        contentColor = MaterialTheme.colors.onBackground,
        actions = { Row { actionIcons() } },
        navigationIcon = navigationIcon,
        elevation = 0.dp
    )
}

@Composable
@Preview(showBackground = true)
fun PreviewAppTopAppBar() {
    AppTopAppBar(
        title = "TopAppBar",
        navigationIcon = {
            ActionConceptList(
                listOf(
                    ActionConcept(
                        concept = Concept.Previous,
                        action = {}),
                ),
            )
        },
        actionIcons = {
            ActionConceptList(
                menuActions = listOf<ActionConcept>(
                    ActionConcept(
                        concept = Concept.Info,
                        action = {}),
                    ActionConcept(
                        concept = Concept.Person,
                        action = {}),
                    ActionConcept(
                        concept = Concept.Parachute,
                        action = {}),
                    ActionConcept(
                        concept = Concept.Key,
                        action = {}),
                )
            )
        },
    )
}
