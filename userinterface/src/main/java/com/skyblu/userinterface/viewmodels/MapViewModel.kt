package com.skyblu.userinterface.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.QuerySnapshot
import com.skyblu.data.authentication.AuthenticationInterface
import com.skyblu.data.firestore.ReadServerInterface
import com.skyblu.data.firestore.WriteServerInterface
import com.skyblu.data.firestore.toDatapoint
import com.skyblu.data.firestore.toJump
import com.skyblu.data.users.SavedSkydivesInterface
import com.skyblu.models.jump.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

data class MapViewModelState(
    val skydive: MutableState<Jump?> = mutableStateOf(null),
    val isLoading: MutableState<Boolean> = mutableStateOf(true),
    val isDatapointsEmpty: MutableState<Boolean> = mutableStateOf(true),
    var datapoints: MutableList<JumpDatapoint> = mutableListOf(),
    val selectedTab: MutableState<JumpPhase> = mutableStateOf(JumpPhase.FREEFALL),
    val isMyJump: MutableState<Boolean> = mutableStateOf(false),
    //Freefall Tab
    //Speeds
    val freefallMaxVerticalSpeed: MutableState<Float?> = mutableStateOf(null),
    val freefallMaxGroundSpeed: MutableState<Float?> = mutableStateOf(null),
    val freefallAverageVerticalSpeed: MutableState<Double?> = mutableStateOf(null),
    val freefallAverageGroundSpeed: MutableState<Double?> = mutableStateOf(null),
    //Distances
    val freefallVerticalDistanceTravelled: MutableState<Float?> = mutableStateOf(null),
    val freefallGroundDistanceTravelled: MutableState<Float?> = mutableStateOf(null),
    //Canopy Tab
    //Speeds
    val canopyMaxVerticalSpeed: MutableState<Float?> = mutableStateOf(null),
    val canopyMaxGroundSpeed: MutableState<Float?> = mutableStateOf(null),
    val canopyAverageVerticalSpeed: MutableState<Double?> = mutableStateOf(null),
    val canopyAverageGroundSpeed: MutableState<Double?> = mutableStateOf(null),
    //Distances
    val canopyVerticalDistanceTravelled: MutableState<Float?> = mutableStateOf(null),
    val canopyGroundDistanceTravelled: MutableState<Float?> = mutableStateOf(null),
    //Altitudes
    val exitAltitude: MutableState<Int?> = mutableStateOf(null),
    val openingAltitude: MutableState<Int?> = mutableStateOf(null),
    val currentUser: MutableState<String?> = mutableStateOf("")
)

@OptIn(InternalCoroutinesApi::class)
@HiltViewModel
class MapViewModel @Inject constructor(
    val authentication: AuthenticationInterface,
    val readServer : ReadServerInterface,
    val writeServer : WriteServerInterface,
    val savedSkydive: SavedSkydivesInterface
) : ViewModel() {

    val state by mutableStateOf(MapViewModelState())
    fun setJump(jumpID: String) {
        state.isLoading.value = true
        viewModelScope.launch {
            val datapoints = mutableListOf<JumpDatapoint>()
            val querySnapshot: QuerySnapshot? =
                readServer.getDatapoints(jumpID = jumpID).getOrNull()
            querySnapshot?.documents?.forEach { document ->
                datapoints.add(document.toDatapoint())
            }
            state.datapoints = datapoints
            state.isLoading.value = false
            val freefallPoints =  datapoints.filterByPhase(phase = JumpPhase.FREEFALL)

            state.freefallMaxVerticalSpeed.value =
                freefallPoints.maxVerticalSpeed(VerticalDirection.DOWNWARD)
            state.freefallMaxGroundSpeed.value = freefallPoints.maxGroundSpeed()
            state.freefallAverageGroundSpeed.value = freefallPoints.averageGroundSpeed()
            state.freefallAverageVerticalSpeed.value = freefallPoints.averageVerticalSpeed()
            state.exitAltitude.value = freefallPoints.getOrNull(0)?.altitude?.toInt()
            state.openingAltitude.value = freefallPoints.lastOrNull()?.altitude?.roundToInt()
            state.freefallVerticalDistanceTravelled.value =
                if (freefallPoints.firstOrNull() != null && freefallPoints.lastOrNull() != null) {
                    freefallPoints.firstOrNull()!!.altitude - freefallPoints.lastOrNull()?.altitude!!
                } else null
            state.freefallGroundDistanceTravelled.value = freefallPoints.calculateDistanceOfList()
            val canopyPoints = datapoints.filterByPhase(phase = JumpPhase.CANOPY)
            state.canopyMaxVerticalSpeed.value =
                canopyPoints.maxVerticalSpeed(VerticalDirection.DOWNWARD)
            state.canopyMaxGroundSpeed.value = canopyPoints.maxGroundSpeed()
            state.canopyAverageVerticalSpeed.value = canopyPoints.averageVerticalSpeed()
            state.canopyAverageGroundSpeed.value = canopyPoints.averageGroundSpeed()
            state.canopyVerticalDistanceTravelled.value =
                if (canopyPoints.firstOrNull() != null && canopyPoints.lastOrNull() != null) {
                    canopyPoints.firstOrNull()!!.altitude - canopyPoints.lastOrNull()?.altitude!!
                } else null
            state.canopyGroundDistanceTravelled.value = canopyPoints.calculateDistanceOfList()
        }
        viewModelScope.launch {
            state.skydive.value = readServer.getJump(jumpID).getOrNull()?.toJump()
        }
        viewModelScope.launch {
            authentication.loggedInFlow.collectLatest {
                state.currentUser.value = it
            }
        }
    }
}

