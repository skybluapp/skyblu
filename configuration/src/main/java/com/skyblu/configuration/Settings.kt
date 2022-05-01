package com.skyblu.configuration

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey

/**
 * Values for settings used throughout the app
 */

const val PREFERENCES = "skyblu_preferences"
val AIRCRAFT_CERTAINTY_KEY = intPreferencesKey("aircraft_certainty_key")
val AIRCRAFT_MIN_ALTITUDE_KEY = intPreferencesKey("aircraft_min_altitude_key")
val AIRCRAFT_MIN_EXIT_HEIGHT = intPreferencesKey("aircraft_max_altitude_key")
val CANOPY_MIN_OPEN_HEIGHT = intPreferencesKey("canopy_min_open_height")
val FREEFALL_ALTITUDE_LOSS_KEY = intPreferencesKey("freefall_altitude_loss_key")
val FREEFALL_SPEED_LOSS_KEY = intPreferencesKey("freefall_speed_loss_key")
val FREEFALL_CERTAINTY_KEY = intPreferencesKey("freefall_certainty_key")



sealed class Settings<T>(
    val key: Preferences.Key<T>,
    val defaultValue: T,
    val range: IntRange? = null
) {

    //How many tracking points need to be collected to confirm the user is in an aircraft
    object AircraftCertainty : Settings<Int>(
        AIRCRAFT_CERTAINTY_KEY,
        defaultValue = 5,
        range = 1 .. 30
    )

    //The minimum altitude required to detect the user is in an aircraft
    object AircraftMinAltitude : Settings<Int>(
        AIRCRAFT_MIN_ALTITUDE_KEY,
        defaultValue = 25,
        range = 10 .. 500,
    )

    //The minimum height the skydiver will jump from
    object AircraftMinExitHeight : Settings<Int>(
        AIRCRAFT_MIN_EXIT_HEIGHT,
        defaultValue = 4000
    )

    //The minimum height the skydiver will be under canopy by
    object CanopyMinOpeningHeight : Settings<Int>(
        CANOPY_MIN_OPEN_HEIGHT,
        defaultValue = 5
    )

    object FreefallAltitudeLossPerSecondThreshold : Settings<Int>(
        FREEFALL_ALTITUDE_LOSS_KEY,
        defaultValue = 5
    )

    object FreefallSpeedLoss : Settings<Int>(
        FREEFALL_SPEED_LOSS_KEY,
        defaultValue = 5
    )

    object FreefallCertainty : Settings<Int>(
        FREEFALL_CERTAINTY_KEY,
        defaultValue = 5
    )
}
