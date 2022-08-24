package com.skyblu.data.firestore

import android.content.Context
import androidx.work.*
import com.skyblu.data.firestore.workers.AddFriendWorker
import com.skyblu.data.firestore.workers.UnfriendWorker
import com.skyblu.models.jump.UserParameterNames
import java.util.*

/**
 * Calls upon WorkManager to start work to write data to the firebase firestore database
 * @property constraints Constraints that must be satisfied before WorkManager starts work
 * @property outOfQuotaPolicy out of quota policy for work manager
 */
class FirestoreWrite(
    private val context: Context
    ) : WriteServerInterface {

    private val constraints =
        Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
    private val outOfQuotaPolicy = OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST

    /**
     * Creates background work for WorkManager
     * @param work The work to start
     * @param inputData data that can be provided for the work
     */
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
     * Deletes a jump from the firestore database
     * @param userID The ID of the user to add a friend to
     * @param friendID The ID of the friend to add
     * @return UUID of queued work
     */
    override fun addFriend(
        userID: String,
        friendID: String,
    ): UUID {
        val addFriendWork = makeWork<AddFriendWorker>(
            inputData = workDataOf(
                UserParameterNames.ID to userID,
                UserParameterNames.FRIENDS to friendID,
            ),
            work = OneTimeWorkRequestBuilder<AddFriendWorker>()
        )
        WorkManager.getInstance(context)
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
     * @return UUID of queued work
     */
    override fun unfriend(
        userID: String,
        friendID: String
    ): UUID {
        val unfriendWork = makeWork<UnfriendWorker>(
            inputData = workDataOf(
                UserParameterNames.ID to userID,
                UserParameterNames.FRIENDS to friendID,
            ),
            work = OneTimeWorkRequestBuilder<UnfriendWorker>()
        )
        WorkManager.getInstance(context).enqueueUniqueWork(
            "unfriendWork",
            ExistingWorkPolicy.APPEND,
            unfriendWork
        )
        return unfriendWork.id
    }


}


