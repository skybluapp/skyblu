package com.skyblu.data.firestore

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot

/**
 * An interface to read data from a remote backend
 */
interface ReadServerInterface {

    /**
     * Requests multiple jumps of a specified page size
     * @param page The page to start at
     * @param pageSize The number of jumps to return
     * @param fromUsers A list of users whom jumps should be returned from
     */
    suspend fun getJumps(
        page: DocumentSnapshot?,
        pageSize: Int,
        fromUsers: List<String>? = null
    ): Result<QuerySnapshot>

    /**
     * Requests multiple users of a specified page size
     * @param page The page to start at
     * @param pageSize The number of users to return
     * @param search The username of users to return
     */
    suspend fun getUsers(
        page: DocumentSnapshot?,
        pageSize: Int,
        search : String
    ): Result<QuerySnapshot>

    /**
     * Requests a single jump
     * @param jumpID The ID of the jump to reques
     */
    suspend fun getJump(jumpID: String): Result<DocumentSnapshot?>

    /**
     * Requests a single user
     * @param userID The ID of the jump to reques
     */
    suspend fun getUser(userID: String): Result<DocumentSnapshot?>
}