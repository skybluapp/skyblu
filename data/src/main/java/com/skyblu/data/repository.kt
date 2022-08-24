package com.skyblu.data

import com.skyblu.data.authentication.AuthenticationInterface
import com.skyblu.data.datastore.DatastoreInterface
import com.skyblu.data.firestore.ReadServerInterface
import com.skyblu.data.firestore.WriteServerInterface
import com.skyblu.data.memory.SavedSkydive
import com.skyblu.data.memory.SavedSkydiveInterface
import com.skyblu.data.permissions.PermissionsInterface
import com.skyblu.data.storage.StorageInterface

import com.skyblu.data.memory.SavedUsersInterface
import com.skyblu.data.workManager.WorkManagerInterface
import javax.inject.Inject

/**
 * Provides interfaces to the application to access data from a variety of sources
 * @param writeServerInterface interface to write data to the server
 * @param readServerInterface interface to read data from the server
 * @param datastoreInterface interface to read and write key value pairs from the device
 * @param authenticationInterface interface to manage users on the server
 * @param storageInterface interface to upload and download files stored remotely
 * @param savedUsersInterface interface to access user data in memory
 * @param savedSkydivesInterface interface to access skydive data in memory
 * @param permissionsInterface interface to request and read app permissions
 * @param workManager interface to provide an interface for accessing the devices background work capabilities
 */
data class Repository @Inject constructor(
    val writeServerInterface: WriteServerInterface,
    val readServerInterface: ReadServerInterface,
    val datastoreInterface: DatastoreInterface,
    val authenticationInterface: AuthenticationInterface,
    val storageInterface: StorageInterface,
    val savedSkydivesInterface: SavedSkydiveInterface,
    val savedUsersInterface: SavedUsersInterface,
    val permissionsInterface: PermissionsInterface,
    val workManager: WorkManagerInterface
)