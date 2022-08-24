package com.skyblu.userinterface.viewmodels

import android.graphics.Bitmap
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyblu.configuration.Dropzone
import com.skyblu.configuration.PreferenceKeys
import com.skyblu.data.Repository

import com.skyblu.jumptracker.service.ClientToService
import com.skyblu.models.jump.Jump
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Holds the current state of the Create Account Screen
 * @param dropzone the DZ chosen in to the dropzone field
 * @param title the text entered in the title field
 * @param jumpNumber the integer entered in the jump number field
 * @param aircraft the text entered in the aircraft field
 * @param equipment the text entered in the equipment field
 * @param description the text entered in the description field
 * @param isSigning is true when signature canvas is displayed
 * @param signatureBitmap a bitmap containing the inputted signature
 * @param currentJumpID ID of the jump being completed
 * @param currentJumpMapUrl  Google Maps Url of the jump being completed
 * @param signatureBitmap a bitmap containing the inputted signature
 */
data class CompleteSkydiveState(
    val dropzone: MutableState<Dropzone> = mutableStateOf(Dropzone.LANGAR),
    val title: MutableState<String> = mutableStateOf(""),
    var jumpNumber: MutableState<Int> = mutableStateOf(0),
    val aircraft: MutableState<String> = mutableStateOf(""),
    val equipment: MutableState<String> = mutableStateOf(""),
    val description: MutableState<String> = mutableStateOf(""),
    val isSigning: MutableState<Boolean> = mutableStateOf(false),
    val signatureBitmap: MutableState<Bitmap?> = mutableStateOf(null),
    val currentJumpID: MutableState<String> = mutableStateOf(""),
    val currentJumpMapUrl : MutableState<String> = mutableStateOf(""),
)

/**
 * Manages data for the Complete Skydive Screen
 * @param repository Provides an API to communicate with sources of data
 * @param clientToService Provides an API to send input to the tracking service
 * @property authentication an interface to communicate with authentication system
 * @property state contains the current state of the Complete Skydive Screen
 * @property datastore an interface to communicate with on-device key-value pairs saved for the application
 * @property writeServer an interface to write data to the remote server
 * @property storage an interface to read and write data to remote file storage
 */
@HiltViewModel
class CompleteSkydiveViewModel @Inject constructor(
    private val repository: Repository,
    private val clientToService: ClientToService
) : ViewModel() {

    //Screen State
    val state by mutableStateOf(CompleteSkydiveState())

    //Data Sources
    private val datastore = repository.datastoreInterface
    private val authentication = repository.authenticationInterface
    private val writeServer = repository.writeServerInterface
    private val storage = repository.storageInterface

    init {
        readRecentValues()
    }

    /**
     * Creates a jump object using fields contained in state
     * @return a Jump object created from fields contained in state
     */
    private fun createJump(): Jump {
        return Jump(
            title = state.title.value.toString(),
            jumpID = state.currentJumpID.value,
            aircraft = state.aircraft.value,
            equipment = state.equipment.value,
            description = state.description.value,
            userID = authentication.thisUser!!,
            staticMapUrl = state.currentJumpMapUrl.value,
            dropzone = state.dropzone.value,
            date = System.currentTimeMillis()
        )
    }

    /**
     * Reads the most recent values for jump fields from the applications datastore and stores values in state
     */
    private fun readRecentValues() {
        viewModelScope.launch {
            datastore.getDatastore().data.map { preferences ->
                state.dropzone.value = enumValueOf<Dropzone>(
                    preferences[PreferenceKeys.LAST_DROPZONE]
                        ?: "LANGAR"
                )
                state.aircraft.value = preferences[PreferenceKeys.LAST_AIRCRAFT]
                    ?: ""
                state.equipment.value = preferences[PreferenceKeys.LAST_EQUIPMENT]
                    ?: ""
                state.jumpNumber.value = preferences[PreferenceKeys.LAST_JUMP_NUMBER] ?: 0
                state.currentJumpID.value = preferences[PreferenceKeys.CURRENT_JUMP_ID] ?: ""
                state.currentJumpMapUrl.value = preferences[PreferenceKeys.CURRENT_JUMP_MAP_URL] ?: ""
            }.collect()
        }
    }

    /**
     * Saves variables in state to datastore (so that they may be recalled automatically next time)
     */
    private fun saveRecentValues() {
        viewModelScope.launch {
            datastore.getDatastore().edit { preferences ->
                preferences[PreferenceKeys.LAST_AIRCRAFT] = state.aircraft.value
                preferences[PreferenceKeys.LAST_EQUIPMENT] = state.equipment.value
                preferences[PreferenceKeys.LAST_DROPZONE] = state.dropzone.value.name
                preferences[PreferenceKeys.LAST_JUMP_NUMBER] = state.jumpNumber.value
                preferences[PreferenceKeys.CURRENT_JUMP_ID] = ""
                preferences[PreferenceKeys.CURRENT_JUMP_MAP_URL] = ""
            }
        }
    }

    /**
     * Upon completion, the service is stopped and the jump is uploaded. The ID of the work is written to Datastore.
     */
    fun completeJump() {
        saveRecentValues()
        clientToService.stopTrackingService()
        viewModelScope.launch {
            datastore.getDatastore().edit { preferences ->
                preferences[PreferenceKeys.UPLOAD_JUMP_WORK] = uploadJump()

            }
//            datastore.writeStringToDatastore(
//                PreferenceKeys.UPLOAD_JUMP_WORK,
//                uploadJump()
//            )
        }
    }

    /**
     * Calls upon the storage interface to upload the jump file and metadata
     */
    private fun uploadJump() : String{
        return storage.uploadJumpFile(createJump(), state.signatureBitmap.value).toString()
    }

}
