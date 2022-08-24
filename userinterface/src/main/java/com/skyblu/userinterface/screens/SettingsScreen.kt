package com.skyblu.userinterface.screens

import android.app.DownloadManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.getSystemService
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.functions.FirebaseFunctions
import com.skyblu.configuration.Concept
import com.skyblu.userinterface.componants.ActionConcept
import com.skyblu.userinterface.componants.alerts.AppDialog
import com.skyblu.userinterface.componants.input.ActionConceptList
import com.skyblu.userinterface.componants.input.AppSettingsCategory
import com.skyblu.userinterface.componants.scaffold.AppTopAppBar
import com.skyblu.userinterface.viewmodels.AppViewModel
import com.skyblu.userinterface.viewmodels.SettingsViewModel
import timber.log.Timber



/**
 * A screen that allows the user to access different settings
 * @param navController Controls navigation between screens
 * @param viewModel Manages the state for the screen
 */
@Composable()
fun SettingsScreen(
    navController: NavController = rememberNavController(),
    viewModel: SettingsViewModel = hiltViewModel(),
    appViewModel : AppViewModel
) {

    val state = viewModel.state

    val settingsConcept = Concept.Settings
    val settingsList = settingsList(navController = navController)
    val trackingSettingsList = settingsList(navController = navController)

    val appState = appViewModel.state

    LaunchedEffect(
        key1 = appViewModel.state.thisUser.value,
        block = {
            if (appState.thisUser.value.isNullOrBlank()) {
                navController.navigate(Concept.LoggedOut.route)
            }
        }
    )

    AppDialog(
        show = state.showLogoutDialog.value,
        dismiss = {state.showLogoutDialog.value = false},
        title = "Logout",
        text = "Are you sure you want to logout?",
        confirm = {viewModel.logout(); viewModel.state.showLogoutDialog.value = false },
        buttonText = "Logout"
    )


    @Composable
    fun root() {
        val context = LocalContext.current
        Column(Modifier.fillMaxSize()) {
            AppSettingsCategory(
                ActionConcept(
                    Concept.Account
                ) { navController.navigate(Concept.Account.route + Concept.Settings.route) }
            )
            AppSettingsCategory(
                ActionConcept(
                    Concept.Download
                ) {
                    var functions: FirebaseFunctions = FirebaseFunctions.getInstance()

                    functions.getHttpsCallable("generateLogbookX").call()
                        .addOnSuccessListener {
                        val data = it.data.toString().replace("[", "").replace("]", "")


                        val clip = ClipData.newPlainText(
                            "url",
                            data
                        )
                        (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?)?.setPrimaryClip(clip)


                        val uri: Uri = Uri.parse(data)
                        val request = DownloadManager.Request(uri)
                        request.setTitle("Download.xlsx").setDescription("Download In Progress")
                            
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

                        val downloadmanager =
                            context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

                        val reference: Long = downloadmanager.enqueue(request)


                    }

                }
            )

        }
    }


    @Composable
    fun tracking() {
        Column(Modifier.fillMaxSize()) {
            AppSettingsCategory(
                menuAction = ActionConcept(Concept.AircraftDetection,
                    action = {navController.navigate(Concept.AircraftDetection.route + Concept.Settings.route) ;} )
            )
            AppSettingsCategory(
                menuAction = ActionConcept(Concept.FreefallDetection,
                    action = {navController.navigate(Concept.FreefallDetection.route + Concept.Settings.route);})
            )
            AppSettingsCategory(
                menuAction = ActionConcept(Concept.CanopyDetection,
                    action = {navController.navigate(Concept.CanopyDetection.route + Concept.Settings.route); })
            )
            AppSettingsCategory(
                menuAction = ActionConcept(Concept.LandingDetection,
                    action = {navController.navigate(Concept.LandingDetection.route + Concept.Settings.route)})
            )
        }
    }




    Scaffold(
        content = {
            when (state.screen.value) {
                SettingsPage.TRACKING -> {
                    tracking()
                }
                SettingsPage.ROOT -> {
                    root()
                }
            }

        },
        topBar = {
            AppTopAppBar(
                title =
                when (state.screen.value) {
                    SettingsPage.ROOT -> {
                        Concept.Settings.title
                    }
                    SettingsPage.TRACKING -> {
                        "${Concept.LocationTracking.title} ${Concept.Settings.title}"
                    }


                },
                navigationIcon = {
                    ActionConceptList(
                        menuActions = listOf(
                            ActionConcept(
                                action = {
                                    if (state.screen.value != SettingsPage.ROOT) {
                                        state.screen.value = SettingsPage.ROOT
                                    } else {
                                        navController.popBackStack()
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
                                action = {state.showLogoutDialog.value = true},
                                concept = Concept.Logout
                            )
                        )
                    )
                },
            )
        }
    )
}

fun settingsList(navController: NavController): List<ActionConcept> {
    val settingsList: MutableList<ActionConcept> = mutableListOf()
    for (icon in SETTINGS_LIST) {
        settingsList.add(
            ActionConcept(
                action = { navController.navigate(icon.route + Concept.Settings.route) },
                concept = icon
            )
        )
    }
    return settingsList
}

@Preview(showBackground = true)
@Composable
fun HelloWorld() {
    Text(text = "Hello")
}

enum class SettingsPage {
    ROOT,
    TRACKING
}

val SETTINGS_LIST = listOf<Concept>(
    Concept.Account,
    //Concept.LocationTracking,
    //Concept.Mapping,
    Concept.Download
)
