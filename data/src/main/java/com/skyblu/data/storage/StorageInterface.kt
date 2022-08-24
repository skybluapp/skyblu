package com.skyblu.data.storage

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.google.firebase.storage.StorageMetadata
import com.skyblu.models.jump.Jump
import com.skyblu.models.jump.JumpDatapoint
import com.skyblu.models.jump.User
import java.util.*

interface StorageInterface {
    suspend fun updateUser(userID : String, user : User, uri : Uri?) : UUID


    /**
     * Starts a worker to add a jump file to storage
     * @param jump jump and datapoints to add to the database
     * @param signature a signature to add to the jump
     * @return UUID of queued work
     */
    fun uploadJumpFile(
        jump: Jump,
        signature: Bitmap?
    ): UUID



    /**
     * Starts a worker to update a jump file in storage
     * @param jump the ID of the jump to update
     * @return UUID of queued work
     */
    fun updateJumpFile(
        jump: Jump,
    ): UUID

    /**
     * Starts a worker to update a jump file in storage
     * @param jumpID the ID of the jump to update
     * @return UUID of queued work
     */
    fun deleteJumpFile(
        jumpID: String,
        userID: String,
    ): UUID

    /**
     * Starts a worker to update a jump file in storage
     * @param jumpID the ID of the jump to retrieve
    *  @param userID the ID of the user to retrieve
     * @return UUID of queued work
     */
    suspend fun getJumpFile(
        jumpID: String,
        userID: String,
    ) : Result<List<JumpDatapoint>>

    /**
     * Starts a worker to update a jump file in storage
     * @param jumpID the ID of the metadata to retrieve
     *  @param userID the ID of the metadata to retrieve
     * @return UUID of queued work
     */
    suspend fun getJumpFileMetadata(
        jumpID: String,
        userID: String,
    ) : Result<StorageMetadata>
}