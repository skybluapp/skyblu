package com.skyblu.userinterface.screens

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.material.FabPosition
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.skyblu.configuration.Concept
import com.skyblu.configuration.LARGE_PADDING
import com.skyblu.models.jump.Jump
import com.skyblu.models.jump.User
import com.skyblu.userinterface.R
import com.skyblu.userinterface.componants.*

import com.skyblu.userinterface.componants.input.ActionConceptList
import com.skyblu.userinterface.componants.lists.PagingList
import com.skyblu.userinterface.componants.scaffold.AppBottomAppBar
import com.skyblu.userinterface.componants.scaffold.AppTopAppBar
import com.skyblu.userinterface.componants.users.ProfileHeader
import com.skyblu.userinterface.viewmodels.ProfileViewModel
import com.skyblu.userinterface.viewmodels.AppViewModel
import timber.log.Timber


/**
 * A screen that allows the user to view a users profile
 * @param navController Controls navigation between screens
 * @param viewModel Manages the state for the screen
 */
@Composable()
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel(),
    appViewModel: AppViewModel,
    userID: String
) {
    val appState = appViewModel.state
    val screenState = viewModel.state
    val savedUsers = appViewModel.savedUsers.userMap
    val activity = LocalContext.current as Activity

    val navIcon = Concept.Profile

    LaunchedEffect(
        key1 = true,
        block = {
            viewModel.setUser(userID)
        }
    )

    LaunchedEffect(
        key1 = appState.thisUser.value,
        block = {
            if (appState.thisUser.value.isNullOrBlank()) {
                navController.navigate(Concept.LoggedOut.route)
            }
        }
    )


    val navMenuAction: List<ActionConcept> =
        listOf(
            ActionConcept(
                action = { navController.popBackStack() },
                concept = Concept.Previous
            )
        )

    val menuAction: List<ActionConcept> = if (screenState.profileUser.value == appState.thisUser.value) {
        listOf(
            ActionConcept(
                action = { navController.navigate(Concept.Settings.route) },
                concept = Concept.Settings
            )
        )
    } else {
        listOf()
    }



    Scaffold(
        topBar = {
            AppTopAppBar(
                title = navIcon.title,
                navigationIcon = { ActionConceptList(menuActions = navMenuAction) },
                actionIcons = { ActionConceptList(menuActions = menuAction) }
            )
        },
        bottomBar = {
            if(userID == appState.thisUser.value) {
                AppBottomAppBar(
                    navController = navController,
                    userID
                )
            }
        },
        content = {
            PagingList<Jump>(
                Heading = {
                    ProfileHeader(
                        appViewModel.savedUsers.userMap[userID] ?: User("")
                    )
                    Spacer(Modifier.height(LARGE_PADDING))
                },
                list = screenState.skydives,
                endReached = screenState.endReached,
                isLoading = screenState.isLoading.value,
                loadNextPage = { viewModel.loadNextSkydivePage() },
                Content = { skydive ->
                    val user = appViewModel.savedUsers.userMap[userID]
                    JumpCard(
                        skydive = skydive,
                        onMapClick = { navController.navigate(Concept.Map.route + "/" + skydive.userID + "/" + skydive.jumpID) },
                        username = appViewModel.savedUsers.userMap[userID]?.username
                            ?: "unknown",
                        user = savedUsers[userID] ?: User(""),
                        isMine = savedUsers[userID]?.ID ?: "" == skydive.userID,
                        onProfileClicked = {

                        },
                        onEditClicked = {
                            viewModel.savedSkydives.skydive = skydive
                            navController.navigate(Concept.Edit.route)
                        }
                    )
                },
                swipeState = screenState.swipeRefreshState.value,
                refresh = {
                    viewModel.refresh()
                    viewModel.setUser(userID)
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (viewModel.permissions.checkPermissions()) {
                        navController.navigate(Concept.TrackSkydive.route)
                    } else {
                        viewModel.permissions.requestPermissions(activity)
                    }
                },
                content = {
                    Icon(
                        painterResource(id = R.drawable.blue_plane),
                        contentDescription = ""
                    )
                },
            )
        },
        isFloatingActionButtonDocked = true,
        floatingActionButtonPosition = FabPosition.Center
    )
}

const val TIMEOUT_MILLIS = 10000

fun timeout(startTime: Long): Boolean {
    return System.currentTimeMillis() - TIMEOUT_MILLIS > startTime
}

