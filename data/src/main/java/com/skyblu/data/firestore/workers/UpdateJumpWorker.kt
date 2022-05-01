package com.skyblu.data.firestore.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.FirebaseFirestore
import com.skyblu.data.firebaseTasks.FirestoreWriteTasks
import com.skyblu.models.jump.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import timber.log.Timber

/**
 * A worker to update a jump in firestore. Will return failure if task is not completed within timeout
 */
class UpdateJumpWorker(
    appContext: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(
    appContext,
    workerParameters
) {

    override suspend fun doWork(): Result {
        val firestore = FirebaseFirestore.getInstance()

        val jumpDataPointString = inputData.getString(JumpParams.JUMP) ?: return Result.failure()
        val jump: SerializableJump = Json.decodeFromString<SerializableJump>(jumpDataPointString)
        return timeoutTask(FirestoreWriteTasks.updateJump(firestore = firestore, map = jumpToMap(jump = jump), jumpID = jump.jumpID))
    }

    private fun jumpToMap(jump : SerializableJump) : Map<String, Any>{
        val map = mutableMapOf<String, Any>()
        map[JumpParams.JUMP_NUMBER] = jump.jumpNumber
        map[JumpParams.DESCRIPTION] = jump.description
        map[JumpParams.DROPZONE] = jump.dropzone
        if (jump.title.isNotBlank()) {
            map[JumpParams.TITLE] = jump.title
        }
        if (jump.equipment.isNotBlank()) {
            map[JumpParams.EQUIPMENT] = jump.equipment
        }
        if (jump.aircraft.isNotBlank()) {
            map[JumpParams.AIRCRAFT] = jump.aircraft
        }
        return map

    }
}