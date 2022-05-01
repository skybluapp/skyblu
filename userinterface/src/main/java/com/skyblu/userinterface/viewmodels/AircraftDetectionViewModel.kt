package com.skyblu.userinterface.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.dataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.skyblu.configuration.feetToMeters
import com.skyblu.configuration.metersToFeet
import com.skyblu.data.datastore.DataStoreRepository
import com.skyblu.data.datastore.DatastoreInterface
import com.skyblu.data.datastore.PreferenceKeys
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AircraftDetectionState(
    val groundspeed : MutableState<Int> = mutableStateOf(0),
    val altitudeFt : MutableState<Int> = mutableStateOf(1000)
)

@HiltViewModel
class AircraftDetectionViewModel @Inject constructor(
    val datastore : DatastoreInterface
) : ViewModel() {

    val state by mutableStateOf(AircraftDetectionState())

    init{
        viewModelScope.launch {
            datastore.readIntFromDatastore(key = PreferenceKeys.AIRCRAFT_ALTITUDE_THRESHOLD,  defaultValue = 300){
                state.altitudeFt.value = it.toFloat().metersToFeet().toInt()
            }
            datastore.readIntFromDatastore(key = PreferenceKeys.AIRCRAFT_GROUNDSPEED_THRESHOLD, defaultValue = 300){
                state.groundspeed.value = it
            }
        }

    }

    fun save(){
        viewModelScope.launch {
            datastore.writeIntToDataStore(data = state.groundspeed.value, key = PreferenceKeys.AIRCRAFT_GROUNDSPEED_THRESHOLD)
            datastore.writeIntToDataStore(data = state.altitudeFt.value.toFloat().feetToMeters().toInt(), key = PreferenceKeys.AIRCRAFT_ALTITUDE_THRESHOLD)
        }

    }

}

data class FreefallDetectionState(
    val groundspeed : MutableState<Int> = mutableStateOf(0),
    val verticalspeed : MutableState<Int> = mutableStateOf(0)
)

@HiltViewModel
class FreefallDetectionViewModel @Inject constructor(
    val datastore : DatastoreInterface
) : ViewModel() {

    val state by mutableStateOf(FreefallDetectionState())

    init{
        viewModelScope.launch {
            datastore.readIntFromDatastore(key = PreferenceKeys.FREEFALL_VERTICALSPEED_THRESHOLD,defaultValue = 300){
                state.verticalspeed.value = it
            }
            datastore.readIntFromDatastore(key = PreferenceKeys.FREEFALL_GROUNDSPEED_THRESHOLD,  defaultValue = 300){
                state.groundspeed.value = it
            }
        }

    }

    fun save(){
        viewModelScope.launch {
            datastore.writeIntToDataStore(data = state.groundspeed.value, key = PreferenceKeys.FREEFALL_GROUNDSPEED_THRESHOLD,  )
            datastore.writeIntToDataStore(data = state.verticalspeed.value, key = PreferenceKeys.FREEFALL_VERTICALSPEED_THRESHOLD,  )
        }

    }

}

data class CanopyDetectionState(
    val verticalspeed : MutableState<Int> = mutableStateOf(0)
)

@HiltViewModel
class CanopyDetectionViewModel @Inject constructor(
    val datastore : DatastoreInterface
) : ViewModel() {

    val state by mutableStateOf(CanopyDetectionState())

    init{
        viewModelScope.launch {
            datastore.readIntFromDatastore(key = PreferenceKeys.CANOPY_VERTICALSPEED_THRESHOLD, defaultValue = 300){
                state.verticalspeed.value = it
            }
        }

    }

    fun save(){
        viewModelScope.launch {
            datastore.writeIntToDataStore(data = state.verticalspeed.value, key = PreferenceKeys.CANOPY_VERTICALSPEED_THRESHOLD,  )
        }

    }

}

data class LandingDetectionState(
    val altitudeFt : MutableState<Int> = mutableStateOf(0)
)

@HiltViewModel
class LandingDetectionViewModel @Inject constructor(
    val datastore : DatastoreInterface
) : ViewModel() {

    val state by mutableStateOf(LandingDetectionState())

    init{
        viewModelScope.launch {
            datastore.readIntFromDatastore(key = PreferenceKeys.LANDED_ALTITUDE_THRESHOLD,  defaultValue = 300){
                state.altitudeFt.value = it.toFloat().metersToFeet().toInt()
            }
        }

    }

    fun save(){
        viewModelScope.launch {
            datastore.writeIntToDataStore(data = state.altitudeFt.value.toFloat().feetToMeters().toInt(), key = PreferenceKeys.LANDED_ALTITUDE_THRESHOLD,  )
        }

    }

}


