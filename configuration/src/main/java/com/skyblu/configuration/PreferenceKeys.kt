package com.skyblu.configuration

import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferenceKeys{
    val LAST_JUMP_NUMBER = intPreferencesKey("last_jump_number")
    val LAST_EQUIPMENT = stringPreferencesKey("last_equipment")
    val LAST_AIRCRAFT = stringPreferencesKey("last_aircraft")
    val LAST_DROPZONE = stringPreferencesKey("last_dropzone")
    val CURRENT_JUMP_ID = stringPreferencesKey("current_jump_id")
    val CURRENT_JUMP_MAP_URL = stringPreferencesKey("current_jump_map_url")
    val UPDATE_USER_WORK = stringPreferencesKey("update_user_work")
    val UPDATE_JUMP_WORK = stringPreferencesKey("update_jump_work")
    val DELETE_JUMP_WORK = stringPreferencesKey("delete_jump_work")
    val UPLOAD_JUMP_WORK = stringPreferencesKey("upload_jump_work")
}