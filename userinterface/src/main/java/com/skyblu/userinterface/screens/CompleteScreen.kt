package com.skyblu.userinterface.screens

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.skyblu.configuration.*
import com.skyblu.userinterface.R
import com.skyblu.userinterface.componants.ActionConcept
import com.skyblu.userinterface.componants.icons.BasicIcon
import com.skyblu.userinterface.componants.input.ActionConceptList
import com.skyblu.userinterface.componants.input.AppNumberPicker
import com.skyblu.userinterface.componants.input.AppTextField
import com.skyblu.userinterface.componants.input.EnumTextPicker
import com.skyblu.userinterface.componants.scaffold.AppTopAppBar
import com.skyblu.userinterface.viewmodels.CompleteSkydiveViewModel
import com.skyblu.userinterface.viewmodels.TrackingScreenViewModel
import io.ak1.drawbox.DrawBox
import io.ak1.drawbox.rememberDrawController
import java.io.FileOutputStream

/**
 * A screen that allows the user to confirm the details of a skydive prior to uploading
 * @param navController Controls navigation between screens
 * @param viewModel Manages the state for the screen
 */
@Composable
fun CompleteSkydiveScreen(
    completeSkydiveViewModel : CompleteSkydiveViewModel = hiltViewModel(),
    trackingScreenViewModel: TrackingScreenViewModel,
    navController: NavController
    ){

    val screenState = completeSkydiveViewModel.state
    val controller = rememberDrawController()
    val context = LocalContext.current
    controller.setStrokeColor(Color.Gray)

    Box(Modifier.fillMaxSize()){
        Scaffold(
            topBar = {
                AppTopAppBar(
                    title = "Complete Skydive",
                    navigationIcon = {
                        ActionConceptList(
                            menuActions = listOf(
                                ActionConcept(
                                    action = {
                                        navController.navigate(Concept.TrackSkydive.route)
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
                                    action = {
                                        completeSkydiveViewModel.completeJump();
                                        trackingScreenViewModel.state.trackingPoints.value = mutableStateListOf()
                                        navController.navigate(Concept.Home.route)
                                    },
                                    concept = Concept.Save
                                )
                            )
                        )
                    },
                )
            },

            floatingActionButton = {
                FloatingActionButton(
                    onClick = { screenState.isSigning.value = true },
                    content = {
                        Icon(
                            painterResource(id = com.skyblu.configuration.R.drawable.sign),
                            contentDescription = "Sign"
                        )
                    }
                )
            }


        ) {
            val focusManager = LocalFocusManager.current
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(SMALL_PADDING),
                verticalArrangement = Arrangement.spacedBy(SMALL_PADDING),
            ) {


                AppNumberPicker(
                    value = screenState.jumpNumber.value,
                    onValueChanged = {screenState.jumpNumber.value = it},
                    range = 1..10000,
                    transformation = {it -> it.toString() + it.suffix() + " Jump"},
                    leadingIcon = R.drawable.number
                )


                EnumTextPicker(
                    value = screenState.dropzone.value,
                    onValueChanged = { screenState.dropzone.value = it },
                    heading = DROPZONE_STRING,
                    leadingIcon = R.drawable.location,
                    list = Dropzone
                )
                AppTextField(
                    value = screenState.aircraft.value,
                    onValueChanged = { screenState.aircraft.value = it },
                    imeAction = ImeAction.Next,
                    onIme = { focusManager.moveFocus(FocusDirection.Down) },
                    placeholder = AIRCRAFT_STRING,
                    leadingIcon = R.drawable.aircraft
                )
                AppTextField(
                    value = screenState.equipment.value,
                    onValueChanged = { screenState.equipment.value = it },
                    imeAction = ImeAction.Next,
                    onIme = { focusManager.moveFocus(FocusDirection.Down) },
                    placeholder = EQUIPMENT_STRING,
                    leadingIcon = R.drawable.parachute
                )
                AppTextField(
                    value = screenState.title.value,
                    onValueChanged = { screenState.title.value = it },
                    imeAction = ImeAction.Next,
                    onIme = { focusManager.moveFocus(FocusDirection.Down) },
                    placeholder = TITLE_STRING,
                    leadingIcon = R.drawable.edit
                )
                AppTextField(
                    value = screenState.description.value,
                    onValueChanged = { screenState.description.value = it },
                    imeAction = ImeAction.Next,
                    onIme = { focusManager.moveFocus(FocusDirection.Down) },
                    placeholder = DESCRIPTION_STRING,
                    leadingIcon = R.drawable.edit
                )
            }
        }


        if(screenState.isSigning.value){
            Scaffold(
                topBar = {
                    AppTopAppBar(
                        title = "Provide Signature",
                        navigationIcon = {
                            ActionConceptList(
                                menuActions = listOf(
                                    ActionConcept(
                                        action = {
                                            controller.reset()
                                            screenState.isSigning.value = false
                                        },
                                        concept = Concept.Close
                                    )
                                )
                            )
                        },
                        actionIcons = {
                            ActionConceptList(
                                menuActions = listOf(
                                    ActionConcept(
                                        action = {
                                            screenState.isSigning.value = false
                                            val bitmap = controller.getDrawBoxBitmap()
                                                val fileOutputStream: FileOutputStream =
                                                    context.openFileOutput(
                                                        "RecentSignature",
                                                        Context.MODE_PRIVATE
                                                    )
                                                bitmap!!.compress(
                                                    Bitmap.CompressFormat.PNG,
                                                    100,
                                                    fileOutputStream
                                                )
                                                fileOutputStream.close()
                                            screenState.signatureBitmap.value = bitmap
                                        },
                                        concept = Concept.Tick
                                    )
                                )
                            )
                        },
                    )
                },
                floatingActionButton = {
                    FloatingActionButton(onClick = {controller.unDo() }) {
                        BasicIcon(icon = com.skyblu.configuration.R.drawable.undo)
                    }
                }
            ) {
                
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter){
                    Column() {
                        Spacer(Modifier.height(100.dp))
                        DrawBox(drawController = controller, modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.5F)
                            .background(Color.White)
                        )
                    }

                }
            }
        }
    }

}

