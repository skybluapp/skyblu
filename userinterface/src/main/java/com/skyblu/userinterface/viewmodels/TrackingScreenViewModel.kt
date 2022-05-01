package com.skyblu.userinterface.viewmodels

import android.content.Context
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyblu.data.authentication.AuthenticationInterface
import com.skyblu.data.datastore.DatastoreInterface
import com.skyblu.data.datastore.PreferenceKeys
import com.skyblu.data.firestore.ReadServerInterface
import com.skyblu.data.firestore.WriteServerInterface
import com.skyblu.jumptracker.service.ClientToService
import com.skyblu.models.jump.JumpDatapoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.absoluteValue

enum class TrackingStatus {
    NOT_TRACKING,
    TRACKING,
}

data class TrackingState(
    val staticMap: MutableState<String> = mutableStateOf(""),
    val trackingPoints: MutableState<MutableList<JumpDatapoint>> = mutableStateOf(mutableStateListOf()),
    val trackingStatus: MutableState<TrackingStatus> = mutableStateOf(TrackingStatus.NOT_TRACKING),
    val showStopEarlyDialog : MutableState<Boolean> = mutableStateOf(false)
)

@HiltViewModel
class TrackingScreenViewModel @Inject constructor(
    private val context: Context,
    private val clientToService: ClientToService,
) : ViewModel() {

    var state by mutableStateOf(TrackingState())

    init {
        clientToService.setOnRecieveSkydiveDataPoint { receiveTrackingPoint(it) }
    }

    private fun receiveTrackingPoint(trackingPoint: JumpDatapoint) {
        if (trackingPoint.verticalSpeed.absoluteValue < 100) {
            state.trackingPoints.value.add(trackingPoint)
        }
    }

    fun startTracking() {
        state.trackingStatus.value = TrackingStatus.TRACKING
        clientToService.startTrackingService()
    }

    fun reset(){
        stopTracking()
        state = TrackingState()
    }


    fun stopTracking() {
        state.trackingStatus.value = TrackingStatus.NOT_TRACKING
        clientToService.stopTrackingService()
    }

}





