package com.skyblu.userinterface.settingsviewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyblu.data.datastore.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named
import com.skyblu.configuration.*


@HiltViewModel
class TrackingSettingsVm @Inject() constructor(
    private val dataStoreRepository: ReadWriteDatastore,
    private val dataStoreInterface : DatastoreInterface,

) : ViewModel() {



    val aircraftAltitudeThresholdStatus = MutableStateFlow<RequestState<Float>>(RequestState.Idle)
    val aircraftAltitudeThreshold = mutableStateOf(5f)

    val aircraftCertaintyStatus = MutableStateFlow<RequestState<Float>>(RequestState.Idle)
    val aircraftCertainty = mutableStateOf(5f)

    val freefallAltitudeLossThresholdStatus = MutableStateFlow<RequestState<Float>>(RequestState.Idle)
    val freefallAltitudeLossThreshold = mutableStateOf(50f)

    val freefallSpeedLossThresholdStatus = MutableStateFlow<RequestState<Float>>(RequestState.Idle)
    val freefallSpeedLoss = mutableStateOf(10f)

    val freefallCertaintyStatus = MutableStateFlow<RequestState<Float>>(RequestState.Idle)
    val freefallCertainty = mutableStateOf(5f)

    val freefallDetectionAltitudeStatus = MutableStateFlow<RequestState<Float>>(RequestState.Idle)
    val freefallDetectionAltitude = mutableStateOf(4000f)

    val canopyDetectionAltitudeStatus = MutableStateFlow<RequestState<Float>>(RequestState.Idle)
    val canopyDetectionAltitude = mutableStateOf(900f)

    @Inject
    lateinit var readWriteDatastore: ReadWriteDatastore

    init {
        readFromDatastore(
            key = AIRCRAFT_MIN_ALTITUDE_KEY,
            mutable = aircraftAltitudeThresholdStatus,
            mapping = { it: Int -> it.toFloat() },
            dataStoreRepository = dataStoreRepository,
            default = 30,
            onLoad = { aircraftAltitudeThreshold.value = it })

        viewModelScope.launch {
            aircraftCertainty.value = 5F
        }


        readFromDatastore(
            key = AIRCRAFT_CERTAINTY_KEY,
            mutable = aircraftCertaintyStatus,
            mapping = { it.toFloat() },
            dataStoreRepository = dataStoreRepository,
            default = 30,
            onLoad = { aircraftCertainty.value = it })

        readFromDatastore(
            key = AIRCRAFT_MIN_EXIT_HEIGHT,
            mutable = freefallDetectionAltitudeStatus,
            mapping = { it.toFloat() },
            dataStoreRepository = dataStoreRepository,
            default = 4000,
            onLoad = { freefallDetectionAltitude.value = it })

        readFromDatastore(
            key = CANOPY_MIN_OPEN_HEIGHT,
            mutable = canopyDetectionAltitudeStatus,
            mapping = { it.toFloat() },
            dataStoreRepository = dataStoreRepository,
            default = 900,
            onLoad = { canopyDetectionAltitude.value = it })

    }

    fun save() {
        writeToDatastore(
            AIRCRAFT_MIN_ALTITUDE_KEY,
            dataStoreRepository = dataStoreRepository,
            int = aircraftAltitudeThreshold.value.toInt()
        )
        writeToDatastore(
            AIRCRAFT_CERTAINTY_KEY,
            dataStoreRepository = dataStoreRepository,
            int = aircraftCertainty.value.toInt()
        )
    }

}



