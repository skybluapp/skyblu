package com.skyblu.data.storage.workers

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.ktx.storageMetadata
import com.skyblu.data.firebaseTasks.StorageTasks
import com.skyblu.data.firestore.workers.timeoutTask
import com.skyblu.models.jump.JumpParams
import com.skyblu.models.jump.SerializableJump
import com.skyblu.models.jump.UserParameterNames
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.io.File

class DeleteJumpFileWorker(
    val appContext: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(
    appContext,
    workerParameters
) {

    override suspend fun doWork(): Result {

        val firebaseStorage = Firebase.storage.reference

        val userID = inputData.getString(UserParameterNames.ID) ?: return Result.failure()
        val jumpID = inputData.getString(JumpParams.JUMP_ID) ?: return Result.failure()

        Timber.d("WORKER: $userID  $jumpID")
        val location = firebaseStorage.child("jumpData/$userID/${jumpID}")

        return timeoutTask(
            StorageTasks.deleteFile(
                location = location,
            )
        )
    }
}

