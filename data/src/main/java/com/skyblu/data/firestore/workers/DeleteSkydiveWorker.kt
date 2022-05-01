package com.skyblu.data.firestore.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.FirebaseFirestore
import com.skyblu.data.firebaseTasks.FirestoreWriteTasks
import com.skyblu.models.jump.JumpParams

/**
 * A worker to delete a jump from Firestore. Will return failure if task is not completed within timeout
 */
class DeleteSkydiveWorker(
    appContext: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(
    appContext,
    workerParameters
) {
    override suspend fun doWork(): Result {
        val firestore = FirebaseFirestore.getInstance()
        val jumpID = inputData.getString(JumpParams.JUMP_ID) ?: return Result.failure()
        return timeoutTask(FirestoreWriteTasks.deleteJump(firestore = firestore, jumpID = jumpID))
    }
}