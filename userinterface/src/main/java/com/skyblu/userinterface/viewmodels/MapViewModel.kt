 package com.skyblu.userinterface.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.StorageMetadata
import com.skyblu.configuration.Dropzone
import com.skyblu.data.Repository
import com.skyblu.models.jump.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

/**
 * Holds the current state of the map screen
 * @param jump The jump being viewed
 * @param isLoading  true if the screen is in a loading state
 * @param isDatapointsEmpty true if the list of data points is empty
 * @param datapoints The list of location datapoints to be plotted on the map
 * @param selectedTab The tab selected from the menu
 * @param isMyJump True if this jump belongs to the logged in user
 * @param currentUser
 *
 * Freefall Data
 * @param freefallMaxVerticalSpeed contains the max vertical speed of the skydiver during freefall
 * @param freefallMaxGroundSpeed contains the max ground speed of the skydiver during freefall
 * @param freefallAverageGroundSpeed contains the average groundspeed of the skydiver during freefall
 * @param freefallAverageVerticalSpeed contains the average groundspeed of the skydiver during freefall
 * @param freefallVerticalDistanceTravelled contains the distance travelled vertically of the skydiver during freefall
 * @param freefallGroundDistanceTravelled contains the distance travelled across the ground of the skydiver during freefall
 *
 * Canopy Data
 * @param canopyMaxVerticalSpeed contains the max vertical speed of the skydiver under canopy
 * @param canopyMaxGroundSpeed contains the max ground speed of the skydiver under canopy
 * @param canopyAverageGroundSpeed contains the average groundspeed of the skydiver under canopy
 * @param canopyAverageVerticalSpeed contains the average groundspeed of the skydiver under canopy
 * @param canopyVerticalDistanceTravelled contains the distance travelled vertically of the skydiver under canopy
 * @param canopyGroundDistanceTravelled contains the distance travelled across the ground of the skydiver under canopy
 *
 * Altitudes
 * @param exitAltitude contains the exit altitude of the skydiver
 * @param exitAltitude contains the opening altitude of the skydiver
 *
 */
data class MapViewModelState(
    val jump: MutableState<Jump?> = mutableStateOf(null),
    val isLoading: MutableState<Boolean> = mutableStateOf(true),
    val isDatapointsEmpty: MutableState<Boolean> = mutableStateOf(true),
    var datapoints: List<JumpDatapoint> = mutableListOf(),
    val selectedTab: MutableState<JumpPhase> = mutableStateOf(JumpPhase.FREEFALL),
    val isMyJump: MutableState<Boolean> = mutableStateOf(false),
    val freefallMaxVerticalSpeed: MutableState<Float?> = mutableStateOf(null),
    val freefallMaxGroundSpeed: MutableState<Float?> = mutableStateOf(null),
    val freefallAverageVerticalSpeed: MutableState<Double?> = mutableStateOf(null),
    val freefallAverageGroundSpeed: MutableState<Double?> = mutableStateOf(null),
    val freefallVerticalDistanceTravelled: MutableState<Float?> = mutableStateOf(null),
    val freefallGroundDistanceTravelled: MutableState<Float?> = mutableStateOf(null),
    val canopyMaxVerticalSpeed: MutableState<Float?> = mutableStateOf(null),
    val canopyMaxGroundSpeed: MutableState<Float?> = mutableStateOf(null),
    val canopyAverageVerticalSpeed: MutableState<Double?> = mutableStateOf(null),
    val canopyAverageGroundSpeed: MutableState<Double?> = mutableStateOf(null),
    val canopyVerticalDistanceTravelled: MutableState<Float?> = mutableStateOf(null),
    val canopyGroundDistanceTravelled: MutableState<Float?> = mutableStateOf(null),
    val exitAltitude: MutableState<Int?> = mutableStateOf(null),
    val openingAltitude: MutableState<Int?> = mutableStateOf(null),
    val currentUser: MutableState<String?> = mutableStateOf("")
)

/**
 * Manages data for the Map Screen
 * @param repository Provides an API to communicate with sources of data
 * @property state contains the current state of the Map Screen
 * @property storage an interface to read and write data to remote file storage
 */
@HiltViewModel
class MapViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    // Screen State
    val state by mutableStateOf(MapViewModelState())

    // Data Sources
    private val storage = repository.storageInterface
    val savedSkydives = repository.savedSkydivesInterface

    /**
     * Downloads a jump and metadata
     * @param jumpID The ID of the jump to download
     * @param userID The User ID of the user who owns the jump
     */
    fun downloadJump(
        jumpID: String,
        userID: String
    ) {
        state.isLoading.value = true
        viewModelScope.launch {

            storage.getJumpFileMetadata(
                jumpID,
                userID = userID
            ).onSuccess { metadata ->
                state.jump.value = createJump(metadata)
            }
            storage.getJumpFile(
                jumpID,
                userID
            ).onSuccess { dataPoints ->
                state.datapoints = dataPoints
                setFreefallData(dataPoints)
                setCanopyData(dataPoints)
            }

            state.isLoading.value = false
        }
    }

    /**
     * Sets state freefall data
     */
    private fun setFreefallData(datapoints: List<JumpDatapoint>) {
        val freefallPoints = datapoints.filterByPhase(phase = JumpPhase.FREEFALL)
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
    }

    /**
     * Sets state canopy data
     */
    private fun setCanopyData(datapoints: List<JumpDatapoint>) {
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

    /**
     * Creates a jump object from metadata
     * @param metadata the metadata to convert in to a Jump
     */
    private fun createJump(metadata: StorageMetadata): Jump {
        val aircraft = metadata.getCustomMetadata(JumpParams.AIRCRAFT) ?: ""
        val id = metadata.getCustomMetadata(JumpParams.JUMP_ID) ?: ""
        val equipment = metadata.getCustomMetadata(JumpParams.EQUIPMENT) ?: ""
        val date = metadata.getCustomMetadata(JumpParams.DATE) ?: "1661204286301"
        val description = metadata.getCustomMetadata(JumpParams.DESCRIPTION) ?: ""
        val dropzone = metadata.getCustomMetadata(JumpParams.DROPZONE) ?: "LANGAR"
        val staticMap = metadata.getCustomMetadata(JumpParams.STATIC_MAP_URL) ?: ""
        val title = metadata.getCustomMetadata(JumpParams.TITLE) ?: ""
        val user = metadata.getCustomMetadata(JumpParams.USER_ID) ?: ""
        val number = metadata.getCustomMetadata(JumpParams.JUMP_NUMBER) ?: "0"
        return Jump(
            jumpID = id,
            userID = user,
            jumpNumber = number.toInt(),
            date = date.toLong(),
            title = title,
            aircraft = aircraft,
            equipment = equipment,
            dropzone = Dropzone.valueOf(dropzone),
            description = description,
            staticMapUrl = staticMap
        )
    }

}

