package com.skyblu.userinterface.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import javax.inject.Inject
import androidx.compose.runtime.*
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyblu.configuration.PreferenceKeys
import com.skyblu.data.Repository

import com.skyblu.models.jump.Jump
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch

/**
 * Holds the current state of the Edit Skydive Screen
 * @param jump The jump data being edited
 * @param showDeleteJumpDialog True if Delete dialog is displayed
 */
data class EditState(
    val showDeleteJumpDialog : MutableState<Boolean> = mutableStateOf(false),
    var jump : MutableState<Jump?> = mutableStateOf(null)
)

/**
 * Manages data for the Edit Screen
 * @param repository Provides an API to communicate with sources of data
 * @property authentication an interface to communicate with authentication system
 * @property dataStore an interface to communicate with app preferences datastore
 * @property savedSkydives an interface to communicate with skydives currently active in the app
 * @property state contains the current state of the Welcome Screen
 */
@HiltViewModel
class EditViewModel @Inject constructor(
    val repository: Repository
) : ViewModel(){

    // Edit Screen State
    val state by mutableStateOf(EditState())

    //Data Sources
    private val savedSkydives = repository.savedSkydivesInterface
    private val authentication = repository.authenticationInterface
    private val dataStore = repository.datastoreInterface

    //Sets up jump in state
    init{
        val jump = savedSkydives.skydive
        if (jump != null) {state.jump.value = jump}
    }

    //Call upon storage interface to update the jump in the remote backend
    fun updateSkydive(){
        val p = state.jump.value?.let {
            repository.storageInterface.updateJumpFile(it)
        }
        viewModelScope.launch {
            state.jump.value?.let {
                dataStore.getDatastore().edit { preferences ->
                    preferences[PreferenceKeys.LAST_JUMP_NUMBER] = it.jumpNumber
                    preferences[PreferenceKeys.LAST_AIRCRAFT] = it.aircraft
                    preferences[PreferenceKeys.LAST_EQUIPMENT] = it.equipment
                    preferences[PreferenceKeys.LAST_DROPZONE] = it.dropzone.name
                }
            }
        }
        viewModelScope.launch {
            dataStore.getDatastore().edit { preferences ->
                preferences[PreferenceKeys.UPDATE_JUMP_WORK] = p.toString()
            }
        }
    }

    //Call upon storage interface to delete the jump from the remote backend
    fun deleteJump(){
        val work = state.jump.value?.let { repository.storageInterface.deleteJumpFile(it.jumpID, authentication.thisUser!!) }
        viewModelScope.launch {
            dataStore.getDatastore().edit { preferences ->
                preferences[PreferenceKeys.DELETE_JUMP_WORK] = work.toString()
            }
        }
    }



}
