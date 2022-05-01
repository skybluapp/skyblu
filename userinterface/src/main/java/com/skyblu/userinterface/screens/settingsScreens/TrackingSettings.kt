package com.skyblu.userinterface.screens.settingsScreens

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.skyblu.configuration.Concept
import com.skyblu.configuration.FT_UNIT_STRING
import com.skyblu.configuration.M_UNIT_STRING
import com.skyblu.userinterface.componants.*
import com.skyblu.userinterface.componants.input.ActionConceptList
import com.skyblu.userinterface.componants.input.AppSettingsRangeSlider
import com.skyblu.userinterface.componants.input.AppSettingsSlider
import com.skyblu.userinterface.componants.scaffold.AppTopAppBar
import com.skyblu.userinterface.settingsviewmodels.TrackingSettingsVm

@Composable()
fun TrackingSettingsScreen(
    navController: NavController,
    viewModel : TrackingSettingsVm = hiltViewModel()
) {

    val navIcon = Concept.Previous


    fun save() {
        viewModel.save()
    }

    Scaffold(
        content = {

            Column {
                AppSettingsSlider(
                    appConcepts = Concept.BlueAircraft,
                    title = "Aircraft Altitude Threshold",
                    prepend = FT_UNIT_STRING,
                    value = viewModel.aircraftAltitudeThreshold.value.toFloat(),
                    onValueChanged = { viewModel.aircraftAltitudeThreshold.value = it },
                    range = 30f .. 1000f
                )

                AppSettingsSlider(
                    appConcepts = Concept.BlueAircraft,
                    title = "Aircraft Certainty",
                    value = viewModel.aircraftCertainty.value.toFloat(),
                    onValueChanged = { viewModel.aircraftCertainty.value = it },
                    range = 5f .. 15f
                )
                AppSettingsSlider(
                    appConcepts = Concept.Parachute,
                    title = "Freefall Altitude Loss Threshold",
                    value = viewModel.aircraftCertainty.value.toFloat(),
                    onValueChanged = { viewModel.aircraftCertainty.value = it },
                    range = 50f .. 1000f
                )
                AppSettingsSlider(
                    appConcepts = Concept.Parachute,
                    title = "Freefall Speed Loss Threshold",
                    value = viewModel.aircraftCertainty.value.toFloat(),
                    onValueChanged = { viewModel.aircraftCertainty.value = it },
                    range = 10f .. 80f
                )
                AppSettingsSlider(
                    appConcepts = Concept.Parachute,
                    title = "Freefall Certainty",
                    value = viewModel.aircraftCertainty.value.toFloat(),
                    onValueChanged = { viewModel.aircraftCertainty.value = it },
                    range = 5f .. 15f
                )
                AppSettingsRangeSlider(
                    values = viewModel.canopyDetectionAltitude.value .. viewModel.freefallDetectionAltitude.value,
                    range = 800f .. 5000f,
                    title = "Freefall Range",
                    prepend = M_UNIT_STRING,
                    onValueChanged = { viewModel.canopyDetectionAltitude.value = it.start ; viewModel.freefallDetectionAltitude.value = it.endInclusive;}
                )

            }


        },
        topBar = {
            AppTopAppBar(
                title = "Tracking Settings",
                navigationIcon = {
                    ActionConceptList(
                        menuActions = listOf(
                            ActionConcept(
                                action = {
                                    navController.navigate(Concept.Settings.route) {
                                        popUpTo(Concept.Settings.route){
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
                    ActionConceptList(
                        menuActions = listOf(
                            ActionConcept(
                                action = { save() },
                                concept = Concept.Save
                            )
                        )
                    )
                },
            )
        },
        bottomBar = {

        }
    )
}

