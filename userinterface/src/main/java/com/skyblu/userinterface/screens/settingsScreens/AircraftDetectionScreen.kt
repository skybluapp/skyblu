package com.skyblu.userinterface.screens.settingsScreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.skyblu.configuration.*
import com.skyblu.userinterface.R
import com.skyblu.userinterface.componants.ActionConcept
import com.skyblu.userinterface.componants.input.*
import com.skyblu.userinterface.componants.scaffold.AppTopAppBar
import com.skyblu.userinterface.viewmodels.*
import timber.log.Timber

@Composable
fun AircraftDetectionScreen(
    viewModel: AircraftDetectionViewModel = hiltViewModel(),
    navController: NavController
) {
    val state = viewModel.state

    val focusManager = LocalFocusManager.current

    Scaffold(
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(SMALL_PADDING),
                verticalArrangement = Arrangement.spacedBy(SMALL_PADDING),

                ) {

                AppNumberPicker(
                    heading = GROUNDSPEED_THRESHOLD_STRING,
                    value = state.groundspeed.value ,
                    onValueChanged = {state.groundspeed.value = it},
                    leadingIcon = R.drawable.location,
                    range = 0..500,
                    transformation = {it -> "$it$MPS_UNIT_STRING"}
                )
                AppNumberPicker(
                    heading = ALTITUDE_THRESHOLD_STRING,
                    value = state.altitudeFt.value ,
                    onValueChanged = {state.altitudeFt.value = it},
                    leadingIcon = R.drawable.location,
                    range = 0..14000,
                    transformation = {it -> "${it}$FT_UNIT_STRING"}
                )

            }
        },
        topBar = {
            AppTopAppBar(
                title = Concept.AircraftDetection.title,
                navigationIcon = {
                    ActionConceptList(
                        menuActions = listOf(
                            ActionConcept(
                                action = {
                                    navController.popBackStack()
                                },
                                concept = Concept.Previous
                            )
                        )
                    )
                },
                actionIcons = {
                    ActionConceptList(
                        menuActions = listOf(
                            ActionConcept(
                                action = { viewModel.save() },
                                concept = Concept.Save
                            ),
                        )
                    )
                },
            )
        },
    )
}

@Composable
fun FreefallDetectionScreen(
    viewModel: FreefallDetectionViewModel = hiltViewModel(),
    navController: NavController
) {
    val state = viewModel.state

    val focusManager = LocalFocusManager.current

    Scaffold(
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(SMALL_PADDING),
                verticalArrangement = Arrangement.spacedBy(SMALL_PADDING),

                ) {

                AppNumberPicker(
                    heading = GROUNDSPEED_THRESHOLD_STRING,
                    value = state.groundspeed.value ,
                    onValueChanged = {state.groundspeed.value = it},
                    leadingIcon = R.drawable.location,
                    range = 0..500,
                    transformation = {it -> "$it$MPS_UNIT_STRING"}
                )
                AppNumberPicker(
                    heading = VERTICAL_SPEED_THRESHOLD_STRING,
                    value = state.verticalspeed.value ,
                    onValueChanged = {state.verticalspeed.value = it},
                    leadingIcon = R.drawable.location,
                    range = 0..5000,
                    transformation = {it -> "$it$MPS_UNIT_STRING"}
                )

            }
        },
        topBar = {
            AppTopAppBar(
                title = Concept.FreefallDetection.title,
                navigationIcon = {
                    ActionConceptList(
                        menuActions = listOf(
                            ActionConcept(
                                action = {
                                    navController.popBackStack()
                                },
                                concept = Concept.Previous
                            )
                        )
                    )
                },
                actionIcons = {
                    ActionConceptList(
                        menuActions = listOf(
                            ActionConcept(
                                action = { viewModel.save() },
                                concept = Concept.Save
                            ),
                        )
                    )
                },
            )
        },
    )
}

@Composable
fun CanopyDetectionScreen(
    viewModel: CanopyDetectionViewModel = hiltViewModel(),
    navController: NavController
) {
    val state = viewModel.state

    val focusManager = LocalFocusManager.current

    Scaffold(
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(SMALL_PADDING),
                verticalArrangement = Arrangement.spacedBy(SMALL_PADDING),

                ) {
                AppNumberPicker(
                    heading = VERTICAL_SPEED_THRESHOLD_STRING,
                    value = state.verticalspeed.value ,
                    onValueChanged = {state.verticalspeed.value = it},
                    leadingIcon = R.drawable.location,
                    range = 0..5000,
                    transformation = {it -> "${it}$MPS_UNIT_STRING"}
                )

            }
        },
        topBar = {
            AppTopAppBar(
                title = Concept.CanopyDetection.title,
                navigationIcon = {
                    ActionConceptList(
                        menuActions = listOf(
                            ActionConcept(
                                action = {
                                    navController.popBackStack()
                                },
                                concept = Concept.Previous
                            )
                        )
                    )
                },
                actionIcons = {
                    ActionConceptList(
                        menuActions = listOf(
                            ActionConcept(
                                action = { viewModel.save() },
                                concept = Concept.Save
                            ),
                        )
                    )
                },
            )
        },
    )
}

@Composable
fun LandingDetectionScreen(
    viewModel: LandingDetectionViewModel = hiltViewModel(),
    navController: NavController
) {
    val state = viewModel.state

    val focusManager = LocalFocusManager.current

    Scaffold(
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(SMALL_PADDING),
                verticalArrangement = Arrangement.spacedBy(SMALL_PADDING),

                ) {

                AppNumberPicker(
                    heading = ALTITUDE_THRESHOLD_STRING,
                    value = state.altitudeFt.value ,
                    onValueChanged = {state.altitudeFt.value = it},
                    leadingIcon = R.drawable.location,
                    range = 0..500,
                    transformation = {it -> "$it$FT_UNIT_STRING"}
                )
            }
        },
        topBar = {
            AppTopAppBar(
                title = Concept.LandingDetection.title,
                navigationIcon = {
                    ActionConceptList(
                        menuActions = listOf(
                            ActionConcept(
                                action = {
                                    navController.popBackStack()
                                },
                                concept = Concept.Previous
                            )
                        )
                    )
                },
                actionIcons = {
                    ActionConceptList(
                        menuActions = listOf(
                            ActionConcept(
                                action = { viewModel.save() },
                                concept = Concept.Save
                            ),
                        )
                    )
                },
            )
        },
    )
}