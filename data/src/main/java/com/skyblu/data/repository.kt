package com.skyblu.data

import com.skyblu.data.authentication.AuthenticationInterface
import com.skyblu.data.datastore.DatastoreInterface
import com.skyblu.data.firestore.ReadServerInterface
import com.skyblu.data.firestore.WriteServerInterface
import com.skyblu.data.storage.StorageInterface
import com.skyblu.data.users.SavedSkydivesInterface
import com.skyblu.data.users.SavedUsersInterface
import javax.inject.Inject

data class Repository @Inject constructor(
    //val roomInterface : TrackingPointsDao,
    val writeServerInterface: WriteServerInterface,
    val readServerInterface: ReadServerInterface,
    val datastoreInterface: DatastoreInterface,
    val authenticationInterface: AuthenticationInterface,
    val storageInterface: StorageInterface,
    val savedSkydivesInterface: SavedSkydivesInterface,
    val savedUsersInterface: SavedUsersInterface,
)