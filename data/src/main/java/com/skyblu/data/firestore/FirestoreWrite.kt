package com.skyblu.data.firestore

import android.content.Context
import android.graphics.Bitmap
import androidx.work.*
import com.skyblu.data.firestore.workers.*
import com.skyblu.models.jump.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.util.*

interface WriteServerInterface {

    /**
     * Starts a worker to delete a jump in the database
     * @param jumpID the ID of the user to delete
     * @param applicationContext The application context
     * @return UUID of queued work
     */
    fun deleteJump(
        jumpID: String,
        applicationContext: Context
    ): UUID

    /**
     * Starts a worker to add a jump to the database
     * @param jumpWithDatapoints jump and datapoints to add to the database
     * @param applicationContext The application context
     * @param signature a signature to add to the jump
     * @return UUID of queued work
     */
    fun uploadJumpWithDatapoints(
        jumpWithDatapoints: JumpWithDatapoints,
        applicationContext: Context,
        signature: Bitmap?
    ): UUID

    /**
     * Starts a worker to update a jump in the database
     * @param jump the ID of the jump to update
     * @param applicationContext The application context
     * @return UUID of queued work
     */
    fun updateJump(
        jump: Jump,
        applicationContext: Context
    ): UUID

    /**
     * Deletes a jump from the firestore database
     * @param jump the ID of the user to delete
     * @param applicationContext The application context
     * @return UUID of queued work
     */
    fun addFriend(
        userID: String,
        friendID: String,
        applicationContext: Context
    ): UUID

    /**
     * Starts a worker to remove a friend in the database
     * @param userID the ID of the user
     * @param friendID the ID of the users friend
     * @param applicationContext The application context
     * @return UUID of queued work
     */
    fun unfriend(
        userID: String,
        friendID: String,
        applicationContext: Context
    ): UUID
}

class FirestoreWrite : WriteServerInterface {

    private val constraints =
        Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
    private val outOfQuotaPolicy = OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST

    private fun <T> makeWork(
        work: OneTimeWorkRequest.Builder,
        inputData: Data
    ): OneTimeWorkRequest {
        return work
            .setExpedited(outOfQuotaPolicy)
            .setInputData(
                inputData
            )
            .setConstraints(
                constraints
            )
            .build()
    }

    /**
     * Starts a worker to delete a jump in the database
     * @param jumpID the ID of the user to delete
     * @param applicationContext The application context
     * @return UUID of queued work
     */
    override fun deleteJump(
        jumpID: String,
        applicationContext: Context,

        ): UUID {
        val deleteWork = makeWork<DeleteSkydiveWorker>(
            inputData = workDataOf(
                JumpParams.JUMP_ID to jumpID
            ),
            work = OneTimeWorkRequestBuilder<DeleteSkydiveWorker>()
        )
        WorkManager.getInstance(applicationContext).enqueueUniqueWork(
            "deleteJumpWork",
            ExistingWorkPolicy.APPEND,
            deleteWork
        )
        return deleteWork.id
    }

    /**
     * Starts a worker to update a jump in the database
     * @param jump the ID of the jump to update
     * @param applicationContext The application context
     * @return UUID of queued work
     */
    override fun updateJump(
        jump: Jump,
        applicationContext: Context
    ): UUID {
        val updateJumpWork: OneTimeWorkRequest = makeWork<UpdateJumpWorker>(
            inputData = workDataOf(
                JumpParams.JUMP to Json.encodeToString(jump.asSerializable())
            ),
            work = OneTimeWorkRequestBuilder<UpdateJumpWorker>()
        )
        WorkManager.getInstance(applicationContext)
            .enqueueUniqueWork(
                "updateJumpWork",
                ExistingWorkPolicy.APPEND,
                updateJumpWork
            )
        return updateJumpWork.id
    }

    /**
     * Deletes a jump from the firestore database
     * @param jump the ID of the user to delete
     * @param applicationContext The application context
     * @return UUID of queued work
     */
    override fun addFriend(
        userID: String,
        friendID: String,
        applicationContext: Context
    ): UUID {
        val addFriendWork = makeWork<AddFriendWorker>(
            inputData = workDataOf(
                UserParameterNames.ID to userID,
                UserParameterNames.FRIENDS to friendID,
            ),
            work = OneTimeWorkRequestBuilder<AddFriendWorker>()
        )
        WorkManager.getInstance(applicationContext)
            .enqueueUniqueWork(
                "addFriendWork",
                ExistingWorkPolicy.APPEND,
                addFriendWork
            )
        return addFriendWork.id
    }

    /**
     * Starts a worker to remove a friend in the database
     * @param userID the ID of the user
     * @param friendID the ID of the users friend
     * @param applicationContext The application context
     * @return UUID of queued work
     */
    override fun unfriend(
        userID: String,
        friendID: String,
        applicationContext: Context
    ): UUID {
        val unfriendWork = makeWork<UnfriendWorker>(
            inputData = workDataOf(
                UserParameterNames.ID to userID,
                UserParameterNames.FRIENDS to friendID,
            ),
            work = OneTimeWorkRequestBuilder<UnfriendWorker>()
        )
        WorkManager.getInstance(applicationContext).enqueueUniqueWork(
            "unfriendWork",
            ExistingWorkPolicy.APPEND,
            unfriendWork
        )
        return unfriendWork.id
    }

    /**
     * Starts a worker to add a jump to the database
     * @param jumpWithDatapoints jump and datapoints to add to the database
     * @param applicationContext The application context
     * @param signature a signature to add to the jump
     * @return UUID of queued work
     */
    override fun uploadJumpWithDatapoints(
        jumpWithDatapoints: JumpWithDatapoints,
        applicationContext: Context,
        signature: Bitmap?
    ): UUID {
        val url = jumpWithDatapoints.jump.staticMapUrl
        jumpWithDatapoints.jump.staticMapUrl = ""

        val skydiveWork = makeWork<UploadSkydiveWorker>(
            inputData = workDataOf(
                JumpParams.JUMP to Json.encodeToString(jumpWithDatapoints.jump.asSerializable()),
                JumpParams.STATIC_MAP_URL to url
            ),
            work = OneTimeWorkRequestBuilder<UploadSkydiveWorker>()
        )

        val dataPointsWorkList = mutableListOf<OneTimeWorkRequest>()
        jumpWithDatapoints.datapoints.forEach { datapoint ->
            val uploadDatapointWork = makeWork<UploadDatapointWorker>(
                inputData = workDataOf(
                    DatapointParams.DATAPOINT to Json.encodeToString(datapoint)
                ),
                work = OneTimeWorkRequestBuilder<UploadDatapointWorker>()
            )
            dataPointsWorkList.add(uploadDatapointWork)
        }
        WorkManager.getInstance(applicationContext).beginWith(skydiveWork).enqueue()
        WorkManager.getInstance(applicationContext).beginUniqueWork(
            "uploadJumpWork",
            ExistingWorkPolicy.REPLACE,
            dataPointsWorkList
        ).enqueue()
        return dataPointsWorkList.last().id
    }
}


