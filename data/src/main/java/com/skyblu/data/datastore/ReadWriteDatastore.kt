package com.skyblu.data.datastore

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject
import javax.inject.Named

class ReadWriteDatastore @Inject constructor(
    val dataStore : DataStore<Preferences>,
) {
    fun read(key: Preferences.Key<String>, default: String = "DefaultString"): Flow<String> {
        return dataStore.data.catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            val result = preferences[key] ?: default
            result
        }
    }

    fun read(key: Preferences.Key<Int>, default: Int = 0): Flow<Int> {
        return dataStore.data.catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            val result = preferences[key] ?: default
            result
        }
    }

    suspend fun write(data: String , key : Preferences.Key<String>)  {
        dataStore.edit { preferences ->
            preferences[key] = data
        }
    }

    suspend fun write(data: Int , key : Preferences.Key<Int>)  {
        dataStore.edit { preferences ->
            preferences[key] = data
        }
    }
}


sealed class RequestState<out T>{
    object Idle : RequestState<Nothing>()
    object Loading : RequestState<Nothing>()
    data class Success<T>(val data : T) : RequestState<T>()
    data class Error(val error : Throwable) : RequestState<Nothing>()
}