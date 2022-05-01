package com.skyblu.userinterface.componants.scaffold

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.skyblu.configuration.Concept

@Composable
fun AppBottomAppBar(
    navController: NavController,
    userID : String
) {
    val bottomNavIcons = listOf<Concept>(
        Concept.Home,
        Concept.Profile
    )
    BottomAppBar(
        cutoutShape = RoundedCornerShape(50),
        backgroundColor = MaterialTheme.colors.background,
        modifier = Modifier.fillMaxWidth()
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        bottomNavIcons.forEach { bottomNavIcon ->
            var selected =
                currentDestination?.hierarchy?.any { it.route?.startsWith(bottomNavIcon.route) ?: false } == true
            BottomNavigationItem(
                selected = currentDestination?.hierarchy?.any { it.route == bottomNavIcon.route } == true,
                onClick = {
                    navController.navigate(if(bottomNavIcon.route.last() == '/')"${Concept.Profile.route}${userID}" else bottomNavIcon.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(id = bottomNavIcon.icon),
                        contentDescription = bottomNavIcon.title,
                        tint = MaterialTheme.colors.onBackground,
                    )
                },
                label = {
                    Text(
                        text = bottomNavIcon.title,
                        color = if (selected) {
                            MaterialTheme.colors.primary
                        } else {
                            MaterialTheme.colors.onBackground
                        }
                    )
                },
                modifier = Modifier.background(MaterialTheme.colors.background)
            )
        }
    }
}
