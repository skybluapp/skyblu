package com.skyblu.userinterface.screens.settingsScreens

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.skyblu.configuration.*
import com.skyblu.userinterface.R
import com.skyblu.userinterface.componants.ActionConcept
import com.skyblu.userinterface.componants.alerts.AppDialog

import com.skyblu.userinterface.componants.alerts.AppBanner
import com.skyblu.userinterface.componants.input.*
import com.skyblu.userinterface.componants.photos.AppDisplayPhoto
import com.skyblu.userinterface.componants.scaffold.AppTopAppBar
import com.skyblu.userinterface.viewmodels.AccountSettingsViewModel
import com.skyblu.userinterface.viewmodels.AppViewModel
import timber.log.Timber

/**
 * A screen that allows the user to change their account settings
 * @param navController Controls navigation between screens
 * @param viewModel Manages the state for the screen
 */
@Preview(showBackground = true)
@Composable()
fun AccountSettingsScreen(
    navController: NavController = rememberNavController(),
    viewModel: AccountSettingsViewModel = hiltViewModel(),
    appViewModel: AppViewModel = hiltViewModel()

) {

    val state = viewModel.state
    val appState = appViewModel.state

    val navIcon = Concept.Previous
    fun save() {
        viewModel.save()
    }

    LaunchedEffect(
        key1 = appViewModel.state.thisUser.value,
        block = {

            Timber.d("User: ${appViewModel.state.thisUser.value}")
            if (appState.thisUser.value.isNullOrBlank()) {
                navController.navigate(Concept.LoggedOut.route)
            }
        }
    )


    val context = LocalContext.current
    val bitmap = remember {
        mutableStateOf<Bitmap?>(null)
    }
    val imageBitmap = remember {
        mutableStateOf<ImageBitmap?>(null)
    }

    val launcher = rememberLauncherForActivityResult(
        contract =
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.setPhotoUri(uri)
    }

    state.photoUri?.let {
        if (Build.VERSION.SDK_INT < 28) {
            bitmap.value = MediaStore.Images
                .Media.getBitmap(context.contentResolver, it)

        } else {
            val source = ImageDecoder
                .createSource(context.contentResolver, it)
            bitmap.value = ImageDecoder.decodeBitmap(source)
        }


    }

    bitmap.value?.let { btm ->
        Image(
            bitmap = btm.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier.size(400.dp)
        )
        imageBitmap.value = btm.asImageBitmap()
    }


    AppDialog(
        show = state.showDeleteDialog.value,
        dismiss = {state.showDeleteDialog.value = false },
        title = "Delete Account",
        text = "Are you sure you want to delete your Account?",
        confirm = { viewModel.deleteAccount() },
        buttonText = "Delete Account"
    )

    val data = if(state.photoUrl.isNullOrBlank() || state.photoUrl.isNullOrEmpty() || state.photoUrl == "null"){
        "https://firebasestorage.googleapis.com/v0/b/skyblu-skydiving-tracker.appspot.com/o/profilePictures%2FEmptyProfilePic.png?alt=media&token=b3360aec-bfde-4dd3-b519-039a8f0e8dca"
    } else {
        state.photoUrl

    }

    Scaffold(
        content = {

            Column(Modifier.fillMaxSize()) {


                AppBanner(
                    text = appState.message.value,
                    actionConcept = ActionConcept(
                        action = { appState.message.value = "" }, concept = Concept.Close

                    ), importance = appState.messageImportance.value
                )



                Box(Modifier.fillMaxSize()) {


                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(LARGE_PADDING)
                            .align(Alignment.TopCenter),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(LARGE_PADDING)

                    ) {


                        if (viewModel.state.photoUri != null) {
                            AppDisplayPhoto(
                                size = 150.dp,
                                image = imageBitmap.value,
                                onClick = {
                                    launcher.launch("image/*")
                                },
                            )
                        } else {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(data)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(150.dp)
                                    .clip(CircleShape)
                                    .clickable { launcher.launch("image/*") },
                            )
                        }

                        AppTextField(
                            value = state.username.value,
                            onValueChanged = { state.username.value = it },
                            placeholder = USERNAME_STRING,
                            leadingIcon = R.drawable.person,
                            error = !state.isUsernameValid.value
                        )
                        AppTextField(
                            value = state.bio.value,
                            onValueChanged = { state.bio.value = it },
                            placeholder = "Bio",
                            leadingIcon = R.drawable.edit
                        )
                        EnumTextPicker(
                            heading = LICENCE_STRING,
                            value = state.licence.value,
                            onValueChanged = { state.licence.value = it },
                            list = Licence,
                            leadingIcon = com.skyblu.configuration.R.drawable.licence
                        )
                        AppNumberPicker(
                            value = state.jumpNumber.value,
                            onValueChanged = { state.jumpNumber.value = it },
                            range = 0..50000,
                            transformation = { "$it Jumps" },
                            leadingIcon = R.drawable.number
                        )

                    }

                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(
                                bottom = XLARGE_PADDING,
                                end = LARGE_PADDING
                            ),
                        horizontalArrangement = Arrangement.End
                    ) {
                        AppTextButton(
                            onClick = {state.showDeleteDialog.value = true},
                            text = "Delete Account",
                            colors = errorButtonColors()
                        )
                    }




                }
            }


        },
        topBar = {
            AppTopAppBar(
                title = ACCOUNT_SETTINGS_STRING,
                navigationIcon = {
                    ActionConceptList(
                        menuActions = listOf(
                            ActionConcept(
                                action = {
                                    navController.navigate(Concept.Settings.route) {
                                        popUpTo(Concept.Settings.route) {
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
                }
            )
        }
    )
}

