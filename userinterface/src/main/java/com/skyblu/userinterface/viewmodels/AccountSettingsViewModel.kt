package com.skyblu.userinterface.viewmodels

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyblu.configuration.Licence
import com.skyblu.configuration.UNKNOWN_USER_STRING
import com.skyblu.configuration.USERNAME_PATTERN
import com.skyblu.data.authentication.AuthenticationInterface
import com.skyblu.data.datastore.DatastoreInterface
import com.skyblu.data.datastore.PreferenceKeys
import com.skyblu.data.storage.StorageInterface
import com.skyblu.data.users.SavedUsersInterface
//import com.skyblu.models.jump.Licence
import com.skyblu.models.jump.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AccountSettingsViewModel @Inject constructor(
    val storage: StorageInterface,
    val authentication: AuthenticationInterface,
    val savedUsers : SavedUsersInterface,
    val datastore : DatastoreInterface,
    private val context: Context,

) : ViewModel() {

    var state by mutableStateOf(AccountSettingsState())


    init{
        viewModelScope.launch {
            authentication.loggedInFlow.collectLatest {
                state.thisUser.value = it
            }
        }
        val thisUser = savedUsers.thisUser()
        if(thisUser != null){
            state.username.value = thisUser.username ?: UNKNOWN_USER_STRING
            state.bio.value = thisUser.bio
            state.photoUrl = thisUser.photoUrl
            state.licence.value = thisUser.licence
            state.jumpNumber.value = thisUser.jumpNumber
        }
    }

    fun save() {
        if(USERNAME_PATTERN.matches(state.username.value)){
            state.isUsernameValid.value = true
            viewModelScope.launch {
                state.profilePicUUID.value = storage.updateUser(
                    applicationContext = context,
                    uri = state.photoUri,
                    userID = authentication.getThisUserID()!!,
                    user = User(
                        ID = state.thisUser.value!!,
                        jumpNumber = state.jumpNumber.value,
                        username = state.username.value,
                        bio = state.bio.value,
                        licence = state.licence.value
                    )
                )
                datastore.writeStringToDatastore(PreferenceKeys.UPDATE_USER_WORK, state.profilePicUUID.value.toString())
            }
        } else {
            state.isUsernameValid.value = false
        }
    }




    fun setPhotoUri(uri: Uri?) {
        state.photoUri = uri
    }

    fun deleteAccount() {
                   authentication.deleteAccount()
    }
}

data class AccountSettingsState(
    var photoUri: Uri? = null,
    var photoUrl : String? = null,
    var username: MutableState<String> = mutableStateOf(""),
    var bio: MutableState<String> = mutableStateOf(""),
    var jumpNumber : MutableState<Int> = mutableStateOf(0),
    var thisUser: MutableState<String?> = mutableStateOf("user"),
    var licence: MutableState<Licence> = mutableStateOf(Licence.A),
    var message : MutableState<String> = mutableStateOf(""),
    var profilePicUUID : MutableState<UUID?> = mutableStateOf(null),
    val showDeleteDialog: MutableState<Boolean> = mutableStateOf(false),
    val isUsernameValid: MutableState<Boolean> = mutableStateOf(true)
) {


}


