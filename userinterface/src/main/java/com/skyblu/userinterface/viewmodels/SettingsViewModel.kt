package com.skyblu.userinterface.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.skyblu.data.authentication.AuthenticationInterface
import com.skyblu.data.users.SavedUsersInterface
import com.skyblu.userinterface.screens.SettingsPage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class SettingsScreenState(
    val screen : MutableState<SettingsPage> = mutableStateOf(SettingsPage.ROOT),
    val showLogoutDialog: MutableState<Boolean> = mutableStateOf(false)
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
  private val authentication : AuthenticationInterface,
  private val savedUsersInterface: SavedUsersInterface
) : ViewModel(){

    val state by mutableStateOf(SettingsScreenState())
    private val loggedIn : MutableState<Boolean> = mutableStateOf(false)

    init{
        loggedIn.value = authentication.getThisUserID() != null
    }


    fun logout(){

        authentication.logout()
        savedUsersInterface.clear()
        loggedIn.value = false

    }

  fun currentUser() : String?{
    return authentication.getThisUserID()
  }

}
