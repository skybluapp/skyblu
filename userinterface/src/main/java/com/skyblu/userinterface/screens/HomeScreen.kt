package com.skyblu.userinterface.screens

import android.app.Activity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.navigation.compose.rememberNavController
import coil.annotation.ExperimentalCoilApi
import coil.imageLoader
import com.skyblu.configuration.Concept
import com.skyblu.configuration.HOME_STRING
import com.skyblu.configuration.UNKNOWN_USER_STRING
import com.skyblu.models.jump.Jump
import com.skyblu.models.jump.User
import com.skyblu.userinterface.R
import com.skyblu.userinterface.componants.ActionConcept
import com.skyblu.userinterface.componants.JumpCard
import com.skyblu.userinterface.componants.alerts.AppBanner
import com.skyblu.userinterface.componants.input.ActionConceptList
import com.skyblu.userinterface.componants.lists.PagingList
import com.skyblu.userinterface.componants.scaffold.AppBottomAppBar
import com.skyblu.userinterface.componants.scaffold.AppTopAppBar
import com.skyblu.userinterface.viewmodels.AppViewModel
import com.skyblu.userinterface.viewmodels.HomeViewModel
import timber.log.Timber

/**
 * A screen that shows a scrolling feed of skydives
 * @param navController Controls navigation between screens
 * @param viewModel Manages the state for the screen
 */
@OptIn(ExperimentalCoilApi::class)
@Composable
fun HomeScreen(
    navController: NavController = rememberNavController(),
    viewModel: HomeViewModel = hiltViewModel(),
    appViewModel: AppViewModel
) {

    val appState = appViewModel.state
    val screenState = viewModel.state
    val savedUsers = appViewModel.savedUsers.userMap

    val activity = LocalContext.current as Activity

    LaunchedEffect(
        key1 = appViewModel.state.thisUser.value,
        block = {
            val thisUser = appState.thisUser.value
            Timber.d("USER" + thisUser)
            if (thisUser.isNullOrBlank()) {
                navController.navigate(Concept.LoggedOut.route)
            } else {
                if(savedUsers[thisUser]?.username.isNullOrEmpty()){
                    Timber.d("Null User")
                    navController.navigate(Concept.Account.route)
                }
                viewModel.loadNextSkydivePage()
            }
        }
    )

    val context = LocalContext.current

    Scaffold(
        content = {
            Column(Modifier.fillMaxSize()) {
                AppBanner(
                    text = appState.message.value,
                    actionConcept = ActionConcept(
                        concept = Concept.Close
                    ) { appState.message.value = "" },
                    importance = appState.messageImportance.value
                )
                PagingList<Jump>(
                    Heading = {

                    },
                    list = screenState.skydives,
                    endReached = screenState.endReached,
                    isLoading = screenState.isLoading.value,
                    loadNextPage = { viewModel.loadNextSkydivePage() },
                    refresh = {
                        screenState.isRefreshing.value = true
                        viewModel.refresh()
                        appViewModel.refresh()

                        context.imageLoader.diskCache?.clear()
                        context.imageLoader.memoryCache?.clear()

                    },
                    swipeState = screenState.swipeRefreshState.value,
                    Content = { skydive ->

                        JumpCard(
                            skydive = skydive,
                            onMapClick = { navController.navigate("${Concept.Map.route}/${skydive.userID}/${skydive.jumpID}") },
                            username = savedUsers[skydive.userID]?.username
                                ?: UNKNOWN_USER_STRING,
                            user = savedUsers[skydive.userID] ?: User(""),
                            isMine = appState.thisUser.value == skydive.userID,
                            onProfileClicked = {
                                navController.navigate(Concept.Profile.route + skydive.userID)

                            },
                            onEditClicked = {
                                viewModel.savedSkydives.skydive = skydive
                                navController.navigate(Concept.Edit.route)
                            },
                        )
                    }
                )
            }

        },
        topBar = {
            AppTopAppBar(
                title = HOME_STRING,
                actionIcons = {
                    ActionConceptList(
                        menuActions = listOf<ActionConcept>(
                            ActionConcept(
                                concept = Concept.Search,
                                action = {navController.navigate(Concept.Search.route)}),
                        )
                    )
                }
            )
        },
        bottomBar = {
            appState.thisUser.value?.let {
                AppBottomAppBar(
                    navController = navController,
                    it
                )
            }
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


