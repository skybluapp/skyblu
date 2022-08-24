package com.skyblu.userinterface.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.skyblu.configuration.*
import com.skyblu.models.jump.Jump
import com.skyblu.models.jump.JumpPhase
import com.skyblu.userinterface.componants.*
import com.skyblu.userinterface.componants.ActionConcept
import com.skyblu.userinterface.componants.data.AppDataPoint
import com.skyblu.userinterface.componants.input.ActionConceptList
import com.skyblu.userinterface.componants.maps.JumpMap
import com.skyblu.userinterface.componants.scaffold.AppTopAppBar
import com.skyblu.userinterface.viewmodels.MapViewModel
import com.skyblu.userinterface.viewmodels.MapViewModelState
import java.util.*


/**
 * A screen that allows the user to view data for a skydive
 * @param navController Controls navigation between screens
 * @param viewModel Manages the state for the screen
 */
@OptIn(
    ExperimentalMaterialApi::class,
    androidx.compose.foundation.ExperimentalFoundationApi::class
)
@Composable
fun MapScreen(
    navController: NavController,
    jumpID: String,
    userID: String,
    viewModel: MapViewModel = hiltViewModel(),
) {
    val p = MaterialTheme.typography.body1
    val state = viewModel.state

    viewModel.downloadJump("$jumpID", userID = userID)

        BottomSheetScaffold(
            topBar = {
                AppTopAppBar(
                    title = if (state.jump.value?.jumpID.isNullOrBlank()) "" else state.jump.value!!.title,
                    navigationIcon = {
                        ActionConceptList(
                            menuActions = listOf(
                                ActionConcept(
                                    action = {
                                        navController.navigate(Concept.Home.route) {
                                            popUpTo(Concept.Home.route) {
                                                inclusive = true
                                            }
                                        }
                                    },
                                    concept = Concept.Previous
                                )
                            )
                        )
                    },
                    actionIcons = {
                        if (state.jump.value?.userID == state.currentUser.value) {
                            ActionConceptList(
                                menuActions = listOf(
                                    ActionConcept(
                                        action = {
                                            viewModel.savedSkydives.skydive = state.jump.value
                                            navController.navigate(Concept.Edit.route)
                                        },
                                        concept = Concept.Edit
                                    )
                                )
                            )
                        }
                    },
                    )
            },
            content = {
                if (!state.isLoading.value) {
                    JumpMap(
                        points = viewModel.state.datapoints,
                        isLoading = state.isLoading.value
                    )
                }
            },
            sheetContent = {
                Column(
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colors.background)
                ) {
                    AppTopAppBar(title = JUMP_DETAILS_STRING)

                    LazyColumn(
                        Modifier.padding(LARGE_PADDING),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            skydiveContent(skydive = state.jump.value)
                            Spacer(modifier = Modifier.height(MEDIUM_PADDING))
                        }

                        item {
                            Row(Modifier.fillMaxWidth()) {
                                SkydiveTab(
                                    phase = JumpPhase.FREEFALL,
                                    modifier = Modifier.weight(1f),
                                    selected = (state.selectedTab.value == JumpPhase.FREEFALL),
                                    onClick = { state.selectedTab.value = JumpPhase.FREEFALL })
                                SkydiveTab(
                                    phase = JumpPhase.CANOPY,
                                    modifier = Modifier.weight(1f),
                                    selected = (state.selectedTab.value == JumpPhase.CANOPY),
                                    onClick = { state.selectedTab.value = JumpPhase.CANOPY })
                            }
                        }
                        item {
                            canopyContent(state = state)
                        }
                        item {
                            FreefallContent(state = state)
                        }
                    }
                }
            },
            sheetGesturesEnabled = true,
            drawerShape = CircleShape,
        )
}

@Preview(showBackground = true)
@Composable
fun SkydiveTab(
    phase: JumpPhase = JumpPhase.CANOPY,
    onClick: (JumpPhase) -> Unit = {},
    selected: Boolean = true,
    modifier: Modifier = Modifier
) {
    Tab(
        selected = true,
        onClick = { onClick(phase) },
        modifier = modifier
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                phase.title,
                fontWeight = FontWeight.Bold,
                color = if (selected) MaterialTheme.colors.primary else MaterialTheme.colors.onBackground
            )
            Icon(
                painter = painterResource(id = phase.icon),
                contentDescription = FREEFALL_STRING,
                tint = if (selected) MaterialTheme.colors.primary else MaterialTheme.colors.onBackground,
                modifier = Modifier.padding(SMALL_PADDING)
            )
        }
    }
}

@Composable
fun canopyContent(state: MapViewModelState) {
    if (state.selectedTab.value == JumpPhase.CANOPY) {

        Text(
            "Altitudes",
            fontWeight = FontWeight.Bold,
            fontStyle = MaterialTheme.typography.body2.fontStyle,
            modifier = Modifier.padding(start = SMALL_PADDING)
        )
        AppDataPoint(
            "Exit Altitude",
            data = ("${state.exitAltitude.value} $M_UNIT_STRING")
        )
        AppDataPoint(
            "Opening Altitude",
            data = ("${state.openingAltitude.value} $M_UNIT_STRING")
        )

        Text(
            SPEEDS_STRING,
            fontWeight = FontWeight.Bold,
            fontStyle = MaterialTheme.typography.body2.fontStyle
        )
        AppDataPoint(
            MAX_VERTICAL_SPEED_STRING,
            data = ("${TWO_DP_FORMAT_STRING.format(state.canopyMaxVerticalSpeed.value)} $MAX_VERTICAL_SPEED_STRING").removePrefix("-")
        )
        AppDataPoint(
            MAX_GROUND_SPEED_STRING,
            data = ("${TWO_DP_FORMAT_STRING.format(state.canopyMaxGroundSpeed.value)} $MAX_GROUND_SPEED_STRING")
        )
        AppDataPoint(
            AVERAGE_VERTICAL_SPEED_STRING,
            data = ("${TWO_DP_FORMAT_STRING.format(state.canopyAverageVerticalSpeed.value)} $M_UNIT_STRING").removePrefix("-")
        )
        AppDataPoint(
            AVERAGE_GROUND_SPEED_STRING,
            data = ("${TWO_DP_FORMAT_STRING.format(state.canopyAverageGroundSpeed.value)} $M_UNIT_STRING")
        )

        AppDataPoint(
            EXIT_ALTITUDE_STRING,
            data = ("${state.exitAltitude.value} $M_UNIT_STRING")
        )
        AppDataPoint(
            OPENING_ALTITUDE_STRING,
            data = ("${state.openingAltitude.value} $M_UNIT_STRING")
        )

        Text(
            DISTANCES_STRING,
            fontWeight = FontWeight.Bold,
            fontStyle = MaterialTheme.typography.body2.fontStyle
        )
        AppDataPoint(
            VERTICAL_DISTANCE_TRAVELLED_STRING,
            data = ("${TWO_DP_FORMAT_STRING.format(state.canopyVerticalDistanceTravelled.value)} $M_UNIT_STRING")
        )
        AppDataPoint(
            GROUND_DISTANCE_TRAVELLED_STRING,
            data = ("${TWO_DP_FORMAT_STRING.format(state.canopyGroundDistanceTravelled.value)} $M_UNIT_STRING")
        )
    }
}

@Composable
fun FreefallContent(state: MapViewModelState) {
    if (state.selectedTab.value == JumpPhase.FREEFALL) {

        Text(
            "Altitudes",
            fontWeight = FontWeight.Bold,
            fontStyle = MaterialTheme.typography.body2.fontStyle,
            modifier = Modifier.padding(start = SMALL_PADDING)
        )
        AppDataPoint(
            "Exit Altitude",
            data = ("${state.exitAltitude.value} $M_UNIT_STRING")
        )
        AppDataPoint(
            "Opening Altitude",
            data = ("${state.openingAltitude.value} $M_UNIT_STRING")
        )

        Text(
            SPEEDS_STRING,
            fontWeight = FontWeight.Bold,
            fontStyle = MaterialTheme.typography.body2.fontStyle
        )
        AppDataPoint(
            MAX_VERTICAL_SPEED_STRING,
            data = ("${TWO_DP_FORMAT_STRING.format(state.freefallMaxVerticalSpeed.value)} $MAX_VERTICAL_SPEED_STRING").removePrefix("-")
        )
        AppDataPoint(
            MAX_VERTICAL_SPEED_STRING,
            data = ("${TWO_DP_FORMAT_STRING.format(state.freefallMaxGroundSpeed.value)} $MAX_VERTICAL_SPEED_STRING"),

        )
        AppDataPoint(
            "Average Vertical Speed",
            data = ("${TWO_DP_FORMAT_STRING.format(state.freefallAverageVerticalSpeed.value)} $M_UNIT_STRING").removePrefix("-"),
            icon = Concept.Altitude.icon
        )
        AppDataPoint(
            "Average Ground Speed",
            data = ("${TWO_DP_FORMAT_STRING.format(state.freefallAverageGroundSpeed.value)} $M_UNIT_STRING"),
            icon = Concept.Altitude.icon
        )

        Text(
            DISTANCES_STRING,
            fontWeight = FontWeight.Bold,
            fontStyle = MaterialTheme.typography.body2.fontStyle
        )
        AppDataPoint(
            "Vertical Distance Travelled",
            data = ("${TWO_DP_FORMAT_STRING.format(state.freefallVerticalDistanceTravelled.value)} $M_UNIT_STRING")
        )
        AppDataPoint(
            "Ground Distance Travelled",
            data = ("${TWO_DP_FORMAT_STRING.format(state.freefallGroundDistanceTravelled.value)} $M_UNIT_STRING")
        )
    }
}

@Preview(showBackground = true)
@Composable
fun skydiveContent(
    skydive: Jump? = Jump(userID = UUID.randomUUID().toString()),
    clip: Boolean = false
) {
    Column(Modifier.padding(SMALL_PADDING)) {
        Text(
            text = "#" + skydive?.jumpNumber.toString() + " " + skydive?.title,
            fontWeight = FontWeight.Bold,
            fontStyle = MaterialTheme.typography.body2.fontStyle,
            fontSize = MaterialTheme.typography.body2.fontSize,
            )
        Text(
            skydive?.description ?: LOADING_STRING,
            fontStyle = MaterialTheme.typography.body2.fontStyle,
            fontSize = MaterialTheme.typography.body2.fontSize,
            maxLines = if (clip) 3 else Int.MAX_VALUE,
            overflow = TextOverflow.Ellipsis
        )
    }
}



