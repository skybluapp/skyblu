package com.skyblu.data.firestore.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.FirebaseFirestore
import com.skyblu.data.firebaseTasks.FirestoreWriteTasks
import com.skyblu.models.jump.DatapointParams
import com.skyblu.models.jump.JumpDatapoint
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

/**
 * A worker to upload a datapoint to Firestore. Will return failure if task is not completed within timeout
 */
class UploadDatapointWorker(
    appContext: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(
    appContext,
    workerParameters
) {

    override suspend fun doWork(): Result {
        val firestore = FirebaseFirestore.getInstance()
        val jumpDataPointString = inputData.getString(DatapointParams.DATAPOINT) ?: return Result.failure()
        val datapoint: JumpDatapoint = Json.decodeFromString<JumpDatapoint>(jumpDataPointString)
        val task = FirestoreWriteTasks.uploadDatapoint(firestore = firestore, datapoint = datapoint)
        return timeoutTask(task)

    }
}