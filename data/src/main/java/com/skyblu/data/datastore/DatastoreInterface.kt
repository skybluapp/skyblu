package com.skyblu.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

interface DatastoreInterface {
    fun getDatastore() : DataStore<Preferences>
}