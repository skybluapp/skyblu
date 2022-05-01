package com.skyblu.userinterface.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
import com.skyblu.userinterface.componants.alerts.AppDialog
import com.skyblu.userinterface.componants.alerts.StyledBanner
import com.skyblu.userinterface.componants.input.*
import com.skyblu.userinterface.componants.scaffold.AppTopAppBar
import com.skyblu.userinterface.viewmodels.AppViewModel
import com.skyblu.userinterface.viewmodels.EditViewModel
import timber.log.Timber

@Composable
fun EditScreen(
    viewModel: EditViewModel = hiltViewModel(),
    navController: NavController,
    appViewModel : AppViewModel,
) {
    val state = viewModel.state
    val appState = appViewModel.state
    val p = state.aircraft
    val focusManager = LocalFocusManager.current

    AppDialog(
        show = state.showDeleteJumpDialog.value,
        dismiss = {state.showDeleteJumpDialog.value = false},
        title = "Delete Jump",
        text = "Are you sure you want to delete this jump?",
        buttonText = "Delete Jump",
        confirm = { viewModel.deleteJump() }
    )
    Scaffold(
        content = {
            
            Column(Modifier.fillMaxSize()) {
                StyledBanner(
                    text = appState.message.value,
                    actionConcept = ActionConcept(
                        action = { appState.message.value = "" }, concept = Concept.Close

                    ), importance = appState.messageImportance.value
                )

                Box(Modifier.fillMaxSize()){
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(SMALL_PADDING)
                            .align(Alignment.TopCenter),
                        verticalArrangement = Arrangement.spacedBy(SMALL_PADDING),

                        ) {
                        AppNumberPicker(
                            value = state.jumpNumber.value,
                            heading = JUMP_NUMBER_STRING,
                            onValueChanged = { state.jumpNumber.value = it },
                            range = JUMP_NUMBER_RANGE,
                            leadingIcon = R.drawable.number,
                            transformation = { "$it${it.suffix()} Jump" }
                        )
                        EnumTextPicker(
                            heading = DROPZONE_STRING,
                            value = state.dropzone.value,
                            onValueChanged = {state.dropzone.value = it; },
                            list = Dropzone,
                            leadingIcon = R.drawable.location
                        )
                        AppTextField(
                            value = state.aircraft.value,
                            onValueChanged = { state.aircraft.value = it },
                            imeAction = ImeAction.Next,
                            onIme = { focusManager.moveFocus(FocusDirection.Down) },
                            placeholder = AIRCRAFT_STRING,
                            leadingIcon = R.drawable.aircraft
                        )
                        AppTextField(
                            value = state.equipment.value,
                            onValueChanged = { state.equipment.value = it },
                            imeAction = ImeAction.Next,
                            onIme = { focusManager.moveFocus(FocusDirection.Down) },
                            placeholder = EQUIPMENT_STRING,
                            leadingIcon = R.drawable.parachute
                        )
                        AppTextField(
                            value = state.title.value,
                            onValueChanged = { state.title.value = it },
                            imeAction = ImeAction.Next,
                            onIme = { focusManager.moveFocus(FocusDirection.Down) },
                            placeholder = TITLE_STRING,
                            leadingIcon = R.drawable.edit
                        )


                        AppTextField(
                            value = state.description.value,
                            onValueChanged = { state.description.value = it },
                            imeAction = ImeAction.Next,
                            onIme = { focusManager.moveFocus(FocusDirection.Down) },
                            placeholder = DESCRIPTION_STRING,
                            leadingIcon = R.drawable.edit
                        )

                    }
                }
            }




        },
        topBar = {
            AppTopAppBar(
                title = state.dropzone.value.title,
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
                                action = { viewModel.updateSkydive() },
                                concept = Concept.Save
                            ),
                        )
                    )
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                          state.showDeleteJumpDialog.value = true
                },
                content = {
                    Icon(
                        painter = painterResource(id = R.drawable.delete),
                        contentDescription = Concept.Delete.title
                    )
                },
                backgroundColor = MaterialTheme.colors.error
            )
        }
    )
}