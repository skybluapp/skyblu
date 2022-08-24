package com.skyblu.userinterface.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.skyblu.data.Repository
import com.skyblu.userinterface.screens.SettingsPage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Holds the current state of the Settings Screen
 * @param screen the settings screen being displayed
 * @param showLogoutDialog If true, logout dialog should be displayed
 */
data class SettingsScreenState(
    val screen : MutableState<SettingsPage> = mutableStateOf(SettingsPage.ROOT),
    val showLogoutDialog: MutableState<Boolean> = mutableStateOf(false),
    val loggedIn : MutableState<Boolean> = mutableStateOf(false)
)

/**
 * Manages Data for the Settings Screen
 * @param repository Provides an API to communicate with sources of data
 * @property authentication an interface to communicate with authentication system
 * @property savedUsersInterface an interface to communicate with users currently in memory
 * @property state contains the current state of the Settings Screen
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    repository: Repository
) : ViewModel(){

    //Data Sources
    private val authentication = repository.authenticationInterface
    private val savedUsersInterface = repository.savedUsersInterface

    //Screen State
    val state by mutableStateOf(SettingsScreenState())

    //Collects logged in status at initialisation
    init{
        state.loggedIn.value = authentication.thisUser != null
    }

    /**
     * Logs the user out of the application and clears data in memory
     */
    fun logout(){
        authentication.logout()
        savedUsersInterface.clear()
        state.loggedIn.value = false
    }
}
