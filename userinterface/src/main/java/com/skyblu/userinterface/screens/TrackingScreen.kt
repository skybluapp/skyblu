package com.skyblu.userinterface.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.skyblu.configuration.Concept
import com.skyblu.configuration.metersToFeet
import com.skyblu.models.jump.JumpDatapoint
import com.skyblu.models.jump.JumpPhase
import com.skyblu.userinterface.R
import com.skyblu.userinterface.componants.ActionConcept
import com.skyblu.userinterface.componants.alerts.AppDialog
import com.skyblu.userinterface.componants.input.ActionConceptList
import com.skyblu.userinterface.componants.scaffold.AppTopAppBar
import com.skyblu.userinterface.viewmodels.TrackingScreenViewModel
import com.skyblu.userinterface.viewmodels.TrackingStatus
import kotlin.math.roundToInt

@Composable
fun TrackingScreen(
    navController: NavController,
    viewModel: TrackingScreenViewModel = hiltViewModel(),
) {

    val state = viewModel.state

    @Composable
    fun NotTrackingFab() {
        ExtendedFloatingActionButton(
            text = { Text(text = "Start Tracking") },
            onClick = { viewModel.startTracking() },
            icon = {
                Icon(
                    painterResource(id = R.drawable.blue_plane),
                    contentDescription = ""
                )
            },
            backgroundColor = MaterialTheme.colors.background,
            contentColor = MaterialTheme.colors.onBackground
        )
    }

    @Composable
    fun TrackingFab() {
        if (state.trackingPoints.value.size > 0) {
            ExtendedFloatingActionButton(
                text = { Text(text = "Stop Tracking") },
                onClick = {
                    if (state.trackingPoints.value.lastOrNull()?.phase != JumpPhase.LANDED) {
                        state.showStopEarlyDialog.value = true
                    } else {
                        viewModel.stopTracking(); navController.navigate(Concept.Tick.route)
                    }

                },
                icon = {
                    Icon(
                        painterResource(id = R.drawable.blue_plane),
                        contentDescription = ""
                    )
                },
                backgroundColor = MaterialTheme.colors.error
            )
        }

    }

    AppDialog(
        show = state.showStopEarlyDialog.value,
        dismiss = { state.showStopEarlyDialog.value = false },
        title = "Stop Tracking",
        text = "It doesn't look like you have landed yet! Are you sure you want to stop tracking early?",
        confirm = { viewModel.stopTracking(); navController.navigate(Concept.Tick.route); state.showStopEarlyDialog.value = false },
        buttonText = "Stop Tracking"
    )


    Scaffold(
        floatingActionButton = {
            when (state.trackingStatus.value) {
                TrackingStatus.NOT_TRACKING -> NotTrackingFab()
                TrackingStatus.TRACKING -> TrackingFab()
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        isFloatingActionButtonDocked = false,
        topBar = {
            AppTopAppBar(
                title = "Track Skydive",
                navigationIcon = {
                    ActionConceptList(
                        menuActions = listOf(
                            ActionConcept(
                                action = {
                                    viewModel.reset()
                                    navController.navigate(Concept.Home.route) {
                                        popUpTo(Concept.Home.route) {
                                            inclusive = true
                                        }
                                    }
                                },
                                concept = Concept.Close
                            )
                        )
                    )
                },
            )
        },
        content = {
            TrackingContent(viewModel.state.trackingPoints.value.lastOrNull())
        }
    )
}

@Preview
@Composable
fun TrackingContent(
    lastDatapoint: JumpDatapoint? = null
) {
    val typography = MaterialTheme.typography
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (lastDatapoint != null) {
                Text(
                    text = lastDatapoint.altitude.metersToFeet().roundToInt().toString() + " ft",
                    fontWeight = FontWeight.Bold,
                    style = typography.h1
                )
                when (lastDatapoint.phase) {
                    JumpPhase.WALKING -> Text(
                        text = "Walking Out...",
                        style = typography.h6
                    )
                    JumpPhase.AIRCRAFT -> Text(
                        text = "In Aircraft...",
                        style = typography.h6
                    )
                    JumpPhase.FREEFALL -> Text(
                        text = "In Freefall...",
                        style = typography.h6
                    )
                    JumpPhase.CANOPY -> Text(
                        text = "Under Canopy...",
                        style = typography.h6
                    )
                    JumpPhase.CANOPY -> Text(
                        text = "Landed",
                        style = typography.h6
                    )
                }
            }
        }

    }
}




