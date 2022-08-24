package com.skyblu.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import javax.inject.Inject

class Datastore @Inject constructor(
    val dataStore : DataStore<Preferences>,
) : DatastoreInterface {

    override fun getDatastore(): DataStore<Preferences> {
        return dataStore
    }
}


