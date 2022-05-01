package com.skyblu.userinterface.viewmodels

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyblu.configuration.Dropzone
import com.skyblu.data.Repository
import com.skyblu.data.datastore.PreferenceKeys
import com.skyblu.jumptracker.service.ClientToService
import com.skyblu.models.jump.Jump
import com.skyblu.models.jump.JumpDatapoint
import com.skyblu.models.jump.JumpWithDatapoints
import com.skyblu.userinterface.componants.generateStaticMapsUrl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class CompleteSkydiveState(
    val dropzone: MutableState<Dropzone> = mutableStateOf(Dropzone.LANGAR),
    val title: MutableState<String> = mutableStateOf(""),
    var jumpNumber: MutableState<Int> = mutableStateOf(0),
    val aircraft: MutableState<String> = mutableStateOf(""),
    val equipment: MutableState<String> = mutableStateOf(""),
    val description: MutableState<String> = mutableStateOf(""),
    val isSigning: MutableState<Boolean> = mutableStateOf(false),
    val signatureBitmap : MutableState<Bitmap?> = mutableStateOf(null)

)

@HiltViewModel
class CompleteSkydiveViewModel @Inject constructor(
    private val repository: Repository,
    private val context: Context,
    val clientToService: ClientToService
) : ViewModel() {

    val state by mutableStateOf(CompleteSkydiveState())
    val datastore = repository.datastoreInterface
    val authentication = repository.authenticationInterface
    val writeServer = repository.writeServerInterface

    init {
        readRecentValues()
    }

    private fun createJump(points: MutableList<JumpDatapoint>): Jump {
        return Jump(
            title = state.title.value.toString(),
            jumpID = points[0].jumpID,
            aircraft = state.aircraft.value,
            equipment = state.equipment.value,
            description = state.description.value,
            userID = authentication.getThisUserID()!!,
            staticMapUrl = generateStaticMapsUrl(
                context = context,
                points = points
            ),
            dropzone = state.dropzone.value,
            date = System.currentTimeMillis()
        )
    }

    private fun readRecentValues() {
        viewModelScope.launch {
            launch {
                datastore.readIntFromDatastore(
                    key = PreferenceKeys.LAST_JUMP_NUMBER,
                    defaultValue = 1
                ) { it ->
                    state.jumpNumber.value = it
                }
            }
            launch {
                datastore.readStringFromDatastore(
                    key = PreferenceKeys.LAST_AIRCRAFT,
                    defaultValue = "C208B"
                ) {
                    state.aircraft.value = it
                }
            }
            launch {
                datastore.readStringFromDatastore(
                    key = PreferenceKeys.LAST_EQUIPMENT,
                    defaultValue = "Sabre 2"
                ) { it ->
                    state.equipment.value = it
                }
            }
            launch {
                datastore.readStringFromDatastore(
                    key = PreferenceKeys.LAST_DROPZONE,
                    defaultValue = "LANGAR"
                ) { it ->
                    Timber.d("Dropzone" + it)
                    try {
                        state.dropzone.value = enumValueOf(it)
                    } catch (e : Exception){
                        state.dropzone.value = Dropzone.LANGAR
                    }

                }
            }
        }
    }

    private fun saveRecentValues() {
        viewModelScope.launch {
            launch {
                datastore.writeIntToDataStore(
                    data = state.jumpNumber.value,
                    key = PreferenceKeys.LAST_JUMP_NUMBER
                )
            }
            launch {
                datastore.writeStringToDatastore(
                    data = state.aircraft.value,
                    key = PreferenceKeys.LAST_AIRCRAFT,
                )
            }
            launch {
                datastore.writeStringToDatastore(
                    data = state.equipment.value,
                    key = PreferenceKeys.LAST_EQUIPMENT,
                )
            }
            launch {
                datastore.writeStringToDatastore(
                    data = state.dropzone.value.name,
                    key = PreferenceKeys.LAST_DROPZONE,
                )
            }
        }
    }


    fun queueJump(points: MutableList<JumpDatapoint>) {
        saveRecentValues()
        clientToService.stopTrackingService()

        viewModelScope.launch {
            Timber.d("Uploading")
            val uploadJumpWork = writeServer.uploadJumpWithDatapoints(
                jumpWithDatapoints = JumpWithDatapoints(
                    createJump(points = points),
                    points
                ),
                context,
                signature = state.signatureBitmap.value
            )
            datastore.writeStringToDatastore(
                PreferenceKeys.UPLOAD_JUMP_WORK,
                uploadJumpWork.toString()
            )
        }

    }
}
