package com.skyblu.data.datastore

import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

interface DatastoreInterface {


    suspend fun readStringFromDatastore(
        key: Preferences.Key<String>,
        defaultValue: String = "",
        onRead: (String) -> Unit
    )

    suspend fun writeStringToDatastore(
        key: Preferences.Key<String>,
        data: String,
    )

    suspend fun readIntFromDatastore(
        key: Preferences.Key<Int>,
        defaultValue: Int,
        onRead: (Int) -> Unit
    )

    suspend fun writeIntToDataStore(
        key: Preferences.Key<Int>,
        data: Int,

        )
}

class DataStoreRepository @Inject constructor(
    private val readWriteDatastore: ReadWriteDatastore
) : DatastoreInterface {

    override suspend fun readIntFromDatastore(
        key: Preferences.Key<Int>,
        defaultValue: Int,
        onRead: (Int) -> Unit
    ) {
        readWriteDatastore.read(
            key,
            0
        ).collect {
            onRead(it)
        }
    }

    override suspend fun writeIntToDataStore(
        key: Preferences.Key<Int>,
        data: Int
    ) {
        readWriteDatastore.write(data, key)
    }

    override suspend fun readStringFromDatastore(
        key: Preferences.Key<String>,

        defaultValue: String,
        onRead: (String) -> Unit
    ) {

        readWriteDatastore.read(
            key,
            ""
        ).collect {
            onRead(it)
        }

    }

    override suspend fun writeStringToDatastore(
        key: Preferences.Key<String>,
        data: String,

        ) {
        readWriteDatastore.write(data, key)
    }


}


fun ViewModel.writeToDatastore(
    key: Preferences.Key<Int>,
    int: Int,
    dataStoreRepository: ReadWriteDatastore
) {
    viewModelScope.launch(Dispatchers.IO) {
        dataStoreRepository.write(
            int,
            key
        )
    }
}

fun <T> ViewModel.readFromDatastore(
    mutable: MutableStateFlow<RequestState<T>>,
    key: Preferences.Key<Int>,
    default: Int,
    mapping: (Int) -> T,
    dataStoreRepository: ReadWriteDatastore,
    onLoad: (T) -> Unit
) {
    mutable.value = RequestState.Loading
    try {
        viewModelScope.launch {

            dataStoreRepository.read(
                key,
                default
            )
                .map { mapping(it) }
                .collect {
                    mutable.value = RequestState.Success(it)
                    onLoad(it)
                }
        }
    } catch (e: Exception) {
        mutable.value = RequestState.Error(e)
    }
}



