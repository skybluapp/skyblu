package com.skyblu.data.firestore

import java.util.*

/**
 * An interface to write data to a remote backend
 * Note: This project uses Firebase Cloud Functions to perform writes to the Firestore database when files are written to storage.
 * This means the client does not need to call functions to upload a jump, hence why not many functions are required in this interface!
 */
interface WriteServerInterface {
    /**
     * Deletes a jump from the firestore database
     * @return UUID of queued work
     */
    fun addFriend(
        userID: String,
        friendID: String
    ): UUID

    /**
     * Starts a worker to remove a friend in the database
     * @param userID the ID of the user
     * @param friendID the ID of the users friend
     * @return UUID of queued work
     */
    fun unfriend(
        userID: String,
        friendID: String
    ): UUID
}