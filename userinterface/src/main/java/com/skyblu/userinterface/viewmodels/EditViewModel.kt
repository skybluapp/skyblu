package com.skyblu.userinterface.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.skyblu.data.users.SavedSkydivesInterface
import javax.inject.Inject
import android.content.Context
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyblu.configuration.Dropzone
import com.skyblu.data.authentication.AuthenticationInterface
import com.skyblu.data.datastore.DataStoreRepository
import com.skyblu.data.datastore.PreferenceKeys
import com.skyblu.data.firestore.ReadServerInterface
import com.skyblu.data.firestore.WriteServerInterface
import com.skyblu.models.jump.Jump
import com.skyblu.models.jump.asSerializable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Named

data class EditState(
    val title : MutableState<String> = mutableStateOf(""),
    val equipment : MutableState<String> = mutableStateOf(""),
    val dropzone : MutableState<Dropzone> = mutableStateOf(Dropzone.LANGAR),
    var aircraft : MutableState<String> = mutableStateOf(""),
    val description : MutableState<String> = mutableStateOf(""),
    val jumpNumber : MutableState<Int> = mutableStateOf(0),
    var jumpID : String = "",
    val showDeleteJumpDialog : MutableState<Boolean> = mutableStateOf(false)
)

@HiltViewModel
class EditViewModel @Inject constructor(
    val applicationContext : Context,
    private val savedSkydives : SavedSkydivesInterface,
    val readServer : ReadServerInterface,
    val writeServer : WriteServerInterface,
    val authentication : AuthenticationInterface,
    val dataStore : DataStoreRepository
) : ViewModel(){

    val state by mutableStateOf(EditState())

    fun updateSkydive(){

        val g = writeServer.updateJump(
            Jump(
                jumpID = state.jumpID,
                equipment = state.equipment.value,
                dropzone = state.dropzone.value,
                aircraft = state.aircraft.value,
                description = state.description.value,
                jumpNumber = state.jumpNumber.value,
                userID = authentication.getThisUserID()!!
            ),
            applicationContext = applicationContext
        )

//        val p = writeServer.updateJump(
//            jumpID = state.jumpID,
//            equipment = state.equipment.value,
//            dropzone = state.dropzone.value.name,
//            aircraft = state.aircraft.value,
//            description = state.description.value,
//            title = state.title.value,
//            applicationContext = applicationContext,
//            jumpNumber = state.jumpNumber.value,
//        )

        viewModelScope.launch {
            dataStore.writeIntToDataStore(data = state.jumpNumber.value, key = PreferenceKeys.LAST_JUMP_NUMBER)
            dataStore.writeStringToDatastore(data = state.aircraft.value, key = PreferenceKeys.LAST_AIRCRAFT)
            dataStore.writeStringToDatastore(data = state.equipment.value, key = PreferenceKeys.LAST_EQUIPMENT)
            dataStore.writeStringToDatastore(data = state.dropzone.value.name, key = PreferenceKeys.LAST_DROPZONE)
        }
        viewModelScope.launch {
            dataStore.writeStringToDatastore(PreferenceKeys.UPDATE_JUMP_WORK, data = g.toString())

        }
    }

    fun deleteJump(){
        val work = writeServer.deleteJump(state.jumpID, applicationContext)
        viewModelScope.launch {
            dataStore.writeStringToDatastore(PreferenceKeys.DELETE_JUMP_WORK, work.toString())
        }

    }

    init{
        val jump = savedSkydives.skydive
        if (jump != null) {
            state.title.value = jump.title
            state.equipment.value = jump.equipment
            state.dropzone.value = jump.dropzone
            state.description.value = jump.description
            state.jumpID = jump.jumpID
            state.aircraft.value = jump.aircraft
            state.jumpNumber.value = jump.jumpNumber
        }
    }

}
