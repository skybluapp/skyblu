package com.skyblu.userinterface.viewmodels

//import com.skyblu.models.jump.Licence
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyblu.configuration.Licence
import com.skyblu.configuration.PreferenceKeys
import com.skyblu.configuration.UNKNOWN_USER_STRING
import com.skyblu.configuration.USERNAME_PATTERN
import com.skyblu.data.Repository
import com.skyblu.models.jump.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Holds the current state of the Account Settings
 * @param photoUri The reference to the photo to update stored locally on the device
 * @param photoUrl The Url link to the users photo stored remotely
 * @param username The users username
 * @param bio The users bio
 * @param jumpNumber The users total jumps
 * @param thisUser The ID for this user
 * @param licence skydiving licence for this user
 * @param message A message that can be displayed
 * @param showDeleteDialog True if delete dialog box should be displaying
 * @param isUsernameValid True if the string in username box meets regex requirements
 */
data class AccountSettingsState(
    var photoUri: Uri? = null,
    var photoUrl: String? = null,
    var username: MutableState<String> = mutableStateOf(""),
    var bio: MutableState<String> = mutableStateOf(""),
    var jumpNumber: MutableState<Int> = mutableStateOf(0),
    var thisUser: MutableState<String?> = mutableStateOf("user"),
    var licence: MutableState<Licence> = mutableStateOf(Licence.A),
    var message: MutableState<String> = mutableStateOf(""),
    val showDeleteDialog: MutableState<Boolean> = mutableStateOf(false),
    val isUsernameValid: MutableState<Boolean> = mutableStateOf(true)
)

/**
 * Manages data for the Account Settings
 * @param repository Provides an API to communicate with sources of data
 * @property authentication an interface to communicate with authentication system
 * @property state contains the current state of the Complete Skydive Screen
 * @property datastore an interface to communicate with on-device key-value pairs saved for the application
 * @property storage an interface to read and write data to remote file storage
 */
@HiltViewModel
class AccountSettingsViewModel @Inject constructor(
    val repository: Repository
) : ViewModel() {

    //Data Sources
    val authentication = repository.authenticationInterface
    val savedUsers = repository.savedUsersInterface
    private val storage = repository.storageInterface
    private val datastore = repository.datastoreInterface

    //Screen State
    var state by mutableStateOf(AccountSettingsState())

    //Collect user and set data in state
    init {
        viewModelScope.launch {
            authentication.signedInStatus.collectLatest {
                state.thisUser.value = it
            }
        }
        val thisUser = savedUsers.thisUser()
        if (thisUser != null) {
            state.username.value = thisUser.username ?: UNKNOWN_USER_STRING
            state.bio.value = thisUser.bio
            state.photoUrl = thisUser.photoUrl
            state.licence.value = thisUser.licence
            state.jumpNumber.value = thisUser.jumpNumber
        }
    }

    /**
     * Update changes in the remote backend
     */
    fun save() {
        if (USERNAME_PATTERN.matches(state.username.value)) {
            state.isUsernameValid.value = true
            viewModelScope.launch {
                datastore.getDatastore().edit { preferences ->
                    preferences[PreferenceKeys.LAST_AIRCRAFT] = storage.updateUser(
                        uri = state.photoUri,
                        userID = authentication.thisUser!!,
                        user = User(
                            ID = state.thisUser.value!!,
                            jumpNumber = state.jumpNumber.value,
                            username = state.username.value,
                            bio = state.bio.value,
                            licence = state.licence.value
                        )).toString()


                }
//                datastore.writeStringToDatastore(
//                    PreferenceKeys.UPDATE_USER_WORK,
//
//                    ).toString()

            }
        } else {
            state.isUsernameValid.value = false
        }
    }

    /**
     * Set photo Uri to be uploaded
     * @param uri The Uri of the photo to be set
     */
    fun setPhotoUri(uri: Uri?) {
        state.photoUri = uri
    }

    /**
     * Delete the logged in users account
     */
    fun deleteAccount() {
        authentication.deleteAccount()
    }
}



