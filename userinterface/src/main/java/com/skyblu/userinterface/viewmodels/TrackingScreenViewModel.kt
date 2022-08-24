package com.skyblu.userinterface.viewmodels

import androidx.compose.runtime.*
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyblu.configuration.PreferenceKeys
import com.skyblu.configuration.playTone
import com.skyblu.data.Repository

import com.skyblu.jumptracker.service.ClientToService
import com.skyblu.models.jump.JumpDatapoint
import com.skyblu.models.jump.calculatePhases
import com.skyblu.models.jump.maxAltitude
import com.skyblu.models.jump.toCsv
import com.skyblu.userinterface.componants.maps.generateStaticMapsUrl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.absoluteValue

/**
 * Holds the current state of the tracking screen
 * @param trackingPoints A list of tracking points recorded from device sensors
 * @param trackingStatus The current status of the tracking
 * @param showStopEarlyDialog If true, stop early dialog is shown (if user has not landed yet)
 * @param staticMap Holds a generated URL for static Google Map representation of the jump
 */
data class TrackingState(
    val staticMap: MutableState<String> = mutableStateOf(""),
    val trackingPoints: MutableState<MutableList<JumpDatapoint>> = mutableStateOf(mutableStateListOf()),
    val trackingStatus: MutableState<TrackingStatus> = mutableStateOf(TrackingStatus.NOT_TRACKING),
    val showStopEarlyDialog: MutableState<Boolean> = mutableStateOf(false)
)

/**
 * Contains different tracking statuses
 */
enum class TrackingStatus {
    NOT_TRACKING,
    TRACKING,
}

/**
 * Manages data for the Tracking Screen
 * @param repository Provides an API to communicate with sources of data
 * @param clientToService Provides an API to communicate with the tracking service
 */
@HiltViewModel
class TrackingScreenViewModel @Inject constructor(
    private val clientToService: ClientToService,
    private val repository: Repository
) : ViewModel() {

    //Screen State
    var state by mutableStateOf(TrackingState())
    var datastore = repository.datastoreInterface

    init {
        clientToService.setOnRecieveSkydiveDataPoint { receiveTrackingPoint(it) }
    }

    /**
     * Starts device location tracking
     */
    fun startTracking() {
        state.trackingStatus.value = TrackingStatus.TRACKING
        clientToService.startTrackingService()
    }

    /**
     * Adds a tracking pint to list
     */
    private fun receiveTrackingPoint(trackingPoint: JumpDatapoint) {
        //playTone()
        if (trackingPoint.verticalSpeed.absoluteValue < 100) {
            state.trackingPoints.value.add(trackingPoint)

        }
    }

    /**
     * Stops tracking and save data locally
     */
    fun stopTracking() {
        state.trackingStatus.value = TrackingStatus.NOT_TRACKING
        clientToService.stopTrackingService()
        saveData()

    }

    /**
     * Saves jump file locally as a CSV and Jump ID and Maps URL to datastore
     */
    private fun saveData(){
        state.trackingPoints.value.calculatePhases()
        state.trackingPoints.value.maxAltitude()
        state.trackingPoints.value.toCsv("JumpFile")
        if (state.trackingPoints.value.size > 0) {
            viewModelScope.launch {
                datastore.getDatastore().edit { preferences ->
                    preferences[PreferenceKeys.CURRENT_JUMP_ID] =
                        state.trackingPoints.value[0].jumpID
                    preferences[PreferenceKeys.CURRENT_JUMP_MAP_URL] =
                        generateStaticMapsUrl(state.trackingPoints.value)
                }
            }
        }
    }

    /**
     * Resets the tracking screen and stops tracking
     */
    fun reset() {
        stopTracking()
        state = TrackingState()
    }

}





