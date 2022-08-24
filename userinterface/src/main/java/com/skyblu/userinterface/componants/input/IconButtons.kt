package com.skyblu.userinterface.componants.input

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.skyblu.configuration.Concept
import com.skyblu.userinterface.componants.ActionConcept

/**
 * A composable icon with an action that is taken when clicked
 * @param menuActions A list of ActionConcepts to generate composable icons with assigned actions
 */
@Composable
fun ActionConceptList(
    menuActions: List<ActionConcept>,
) {
    for (action in menuActions) {
        IconButton(onClick = { action.action() }) {
            Icon(
                painter = painterResource(id = action.concept.icon),
                contentDescription = null,
                tint = MaterialTheme.colors.onBackground
            )
        }
    }
}

/**
 * A composable icon that navigates to a route when clicked
 * @param appConcepts A list of concepts that can be navigated to
 * @param navController The controller used to navigate to the concepts route
 */
@Composable
fun ActionConceptList(
    appConcepts: List<Concept>,
    navController: NavController
) {
    for (action in appConcepts) {
        IconButton(onClick = { navController.navigate(action.route) }) {
            Icon(
                painter = painterResource(id = action.icon),
                contentDescription = null,
                tint = MaterialTheme.colors.onBackground
            )
        }
    }
}

