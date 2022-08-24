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
import java.io.File

class UploadJumpDataWorker(
    val appContext: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(
    appContext,
    workerParameters
) {

    override suspend fun doWork(): Result {

        val firebaseStorage = Firebase.storage.reference
        val fileUri =   appContext.getFileStreamPath("JumpFile.csv").toUri() ?: return Result.failure()
        val userID = inputData.getString(UserParameterNames.ID) ?: return Result.failure()


        val jumpString = inputData.getString(JumpParams.JUMP) ?: return Result.failure()
        val urlString = inputData.getString(JumpParams.STATIC_MAP_URL) ?: return Result.failure()
        val jump: SerializableJump = Json.decodeFromString<SerializableJump>(jumpString)
        jump.staticMapUrl = urlString

        val location = firebaseStorage.child("jumpData/$userID/${jump.jumpID}")

        val metadata = storageMetadata {
            contentType = "csv"
            setCustomMetadata(
                JumpParams.JUMP_ID , jump.jumpID
            )
            setCustomMetadata(
                JumpParams.DESCRIPTION , jump.description,
            )
            setCustomMetadata(
                JumpParams.AIRCRAFT , jump.aircraft,
            )
            setCustomMetadata(
                JumpParams.DROPZONE , jump.dropzone,
            )
            setCustomMetadata(
                JumpParams.JUMP_NUMBER , jump.jumpNumber.toString(),
            )
            setCustomMetadata(
                JumpParams.DATE , jump.date.toString(),
            )
            setCustomMetadata(
                JumpParams.STATIC_MAP_URL , jump.staticMapUrl,
            )
            setCustomMetadata(
                JumpParams.USER_ID , jump.userID,
            )
            setCustomMetadata(
                JumpParams.TITLE , jump.title,
            )
            setCustomMetadata(
                JumpParams.EQUIPMENT , jump.equipment,
            )
        }


        return timeoutTask(
            StorageTasks.uploadFile(
                location = location,
                file = fileUri,
                metadata = metadata
            )
        )
    }
}

class UpdateJumpFileWorker(
    val appContext: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(
    appContext,
    workerParameters
) {

    override suspend fun doWork(): Result {

        val firebaseStorage = Firebase.storage.reference
        val userID = inputData.getString(UserParameterNames.ID) ?: return Result.failure()

        val jumpString = inputData.getString(JumpParams.JUMP) ?: return Result.failure()
        val urlString = inputData.getString(JumpParams.STATIC_MAP_URL) ?: return Result.failure()
        val jump: SerializableJump = Json.decodeFromString<SerializableJump>(jumpString)
        val location = firebaseStorage.child("jumpData/$userID/${jump.jumpID}")
        jump.staticMapUrl = urlString

        val metadata = storageMetadata {
            contentType = "csv"
            setCustomMetadata(
                JumpParams.JUMP_ID , jump.jumpID
            )
            setCustomMetadata(
                JumpParams.DESCRIPTION , jump.description,
            )
            setCustomMetadata(
                JumpParams.AIRCRAFT , jump.aircraft,
            )
            setCustomMetadata(
                JumpParams.DROPZONE , jump.dropzone,
            )
            setCustomMetadata(
                JumpParams.JUMP_NUMBER , jump.jumpNumber.toString(),
            )
            setCustomMetadata(
                JumpParams.DATE , jump.date.toString(),
            )
            setCustomMetadata(
                JumpParams.STATIC_MAP_URL , jump.staticMapUrl,
            )
            setCustomMetadata(
                JumpParams.USER_ID , jump.userID,
            )
            setCustomMetadata(
                JumpParams.TITLE , jump.title,
            )
            setCustomMetadata(
                JumpParams.EQUIPMENT , jump.equipment,
            )
        }


        return timeoutTask(
            StorageTasks.updateMetadata(
                location = location,
                metadata = metadata
            )
        )
    }
}

