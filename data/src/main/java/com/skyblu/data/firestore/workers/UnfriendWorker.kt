package com.skyblu.data.firestore.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.FirebaseFirestore
import com.skyblu.data.firebaseTasks.FirestoreWriteTasks
import com.skyblu.models.jump.UserParameterNames
import timber.log.Timber

/**
 * A worker to remove a friend from Firestore. Will return failure if task is not completed within timeout
 * @param appContext The application context
 * @param workerParameters The parameters for the work
 */
class UnfriendWorker(
    appContext: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(
    appContext,
    workerParameters
) {

    /**
     * Work to unfriend a user
     */
    override suspend fun doWork(): Result {
        val firestore = FirebaseFirestore.getInstance()
        val friendID = inputData.getString(UserParameterNames.FRIENDS) ?: return Result.failure()
        val userID = inputData.getString(UserParameterNames.ID) ?: return Result.failure()
        return timeoutTask(task = FirestoreWriteTasks.unfriend(firestore = firestore, userID = userID, friendID = friendID))
    }
}