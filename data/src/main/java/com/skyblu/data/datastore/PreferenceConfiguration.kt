package com.skyblu.data.datastore

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferenceKeys{
    val FREEFALL_CERTAINTY_KEY = intPreferencesKey("freefall_certainty_key")
    val LAST_JUMP_NUMBER = intPreferencesKey("last_jump_number")
    val LAST_EQUIPMENT = stringPreferencesKey("last_equipment")
    val LAST_AIRCRAFT = stringPreferencesKey("last_aircraft")
    val LAST_DROPZONE = stringPreferencesKey("last_dropzone")

    val AIRCRAFT_ALTITUDE_THRESHOLD = intPreferencesKey("aircraft_altitude_threshold")
    val AIRCRAFT_GROUNDSPEED_THRESHOLD = intPreferencesKey("aircraft_groundspeed_threshold")

    val FREEFALL_VERTICALSPEED_THRESHOLD = intPreferencesKey("freefall_verticalspeed_threshold")
    val FREEFALL_GROUNDSPEED_THRESHOLD = intPreferencesKey("freefall_groundspeed_threshold")

    val CANOPY_VERTICALSPEED_THRESHOLD = intPreferencesKey("canopy_verticalspeed_threshold")

    val LANDED_ALTITUDE_THRESHOLD = intPreferencesKey("canopy_verticalspeed_threshold")

    val UPDATE_USER_WORK = stringPreferencesKey("update_user_work")
    val UPDATE_JUMP_WORK = stringPreferencesKey("update_jump_work")
    val DELETE_JUMP_WORK = stringPreferencesKey("delete_jump_work")
    val UPLOAD_JUMP_WORK = stringPreferencesKey("upload_jump_work")
}

