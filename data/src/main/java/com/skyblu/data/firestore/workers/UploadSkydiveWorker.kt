package com.skyblu.data.firestore.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.FirebaseFirestore
import com.skyblu.data.firebaseTasks.FirestoreWriteTasks
import com.skyblu.models.jump.Jump
import com.skyblu.models.jump.JumpParams
import com.skyblu.models.jump.SerializableJump
import com.skyblu.models.jump.asJump
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

/**
 * A worker to upload a jump to Firestore. Will return failure if task is not completed within timeout
 */
class UploadSkydiveWorker(
    appContext: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(
    appContext,
    workerParameters
) {

    override suspend fun doWork(): Result {
        val firestore = FirebaseFirestore.getInstance()
        val jumpString = inputData.getString(JumpParams.JUMP) ?: return Result.failure()
        val urlString = inputData.getString(JumpParams.STATIC_MAP_URL) ?: return Result.failure()
        val jump: SerializableJump = Json.decodeFromString<SerializableJump>(jumpString)
        jump.staticMapUrl = urlString

        val task = FirestoreWriteTasks.uploadJump(firestore = firestore, jump = jump.asJump())

        return timeoutTask(task)

    }
}