package com.skyblu.data.storage

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.work.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.skyblu.configuration.ONE_MEGABYTE
import com.skyblu.data.firestore.timeout
import com.skyblu.data.firestore.workers.UploadUserWorker
import com.skyblu.data.storage.workers.DeleteJumpFileWorker
import com.skyblu.data.storage.workers.UpdateJumpFileWorker
import com.skyblu.data.storage.workers.UploadJumpDataWorker
import com.skyblu.data.storage.workers.UploadProfilePictureWorker
import com.skyblu.models.jump.*
import kotlinx.coroutines.delay
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.util.*

class FirebaseStorage(
    private val context: Context,
) : StorageInterface {

    private val constraints =
        Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
    private val outOfQuotaPolicy = OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST

    private val storage = FirebaseStorage.getInstance()

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

    override suspend fun updateUser(
        userID: String,
        user: User,
        uri: Uri?,
    ): UUID {

        val uploadProfilePictureWorker: OneTimeWorkRequest = makeWork<UploadProfilePictureWorker>(
            work = OneTimeWorkRequestBuilder<UploadProfilePictureWorker>(),
            inputData = workDataOf(
                UserParameterNames.ID to userID,
                UserParameterNames.PHOTO_URL to uri.toString()
            )
        )

        val uploadUserWorker: OneTimeWorkRequest = makeWork<UploadUserWorker>(
            work = OneTimeWorkRequestBuilder<UploadUserWorker>(),
            inputData = workDataOf(
                UserParameterNames.USER to Json.encodeToString(user),
            )
        )


        if (uri != null) {
            WorkManager.getInstance(context).beginUniqueWork(
                "updateUserWork",
                ExistingWorkPolicy.REPLACE,
                uploadProfilePictureWorker
            ).then(
                uploadUserWorker
            ).enqueue()
            return uploadUserWorker.id

        } else {
            WorkManager.getInstance(context).enqueueUniqueWork(
                "updateUserWork",
                ExistingWorkPolicy.REPLACE,
                uploadUserWorker
            )
            return uploadUserWorker.id
        }
    }

    /**
     * Starts a worker to add a jump to the database
     * @param jump jump and datapoints to add to the database
     * @param signature a signature to add to the jump
     * @return UUID of queued work
     */
    override fun uploadJumpFile(
        jump: Jump,
        signature: Bitmap?
    ): UUID {
        val url = jump.staticMapUrl
        jump.staticMapUrl = ""

        val jumpData = makeWork<OneTimeWorkRequest>(
            inputData = workDataOf(
                JumpParams.JUMP to Json.encodeToString(jump.asSerializable()),
                JumpParams.STATIC_MAP_URL to url,
                UserParameterNames.ID to jump.userID
            ),
            work = OneTimeWorkRequestBuilder<UploadJumpDataWorker>(),
        )

        WorkManager.getInstance(context).enqueueUniqueWork(
            "uploadJumpWork",
            ExistingWorkPolicy.APPEND,
            jumpData
        )
        return jumpData.id
    }

    override fun updateJumpFile(
        jump: Jump,
    ): UUID {
        val url = jump.staticMapUrl
        jump.staticMapUrl = ""

        val jumpData = makeWork<OneTimeWorkRequest>(
            inputData = workDataOf(
                JumpParams.JUMP to Json.encodeToString(jump.asSerializable()),
                JumpParams.STATIC_MAP_URL to url,
                UserParameterNames.ID to jump.userID
            ),
            work = OneTimeWorkRequestBuilder<UpdateJumpFileWorker>()
        )

        WorkManager.getInstance(context).enqueueUniqueWork(
            "updateJumpWork",
            ExistingWorkPolicy.APPEND,
            jumpData
        )

        return jumpData.id
    }

    override fun deleteJumpFile(
        jumpID: String,
        userID: String,
    ): UUID {
        val deleteFileWorker = makeWork<OneTimeWorkRequest>(
            inputData = workDataOf(
                JumpParams.JUMP_ID to jumpID,
                UserParameterNames.ID to userID
            ),
            work = OneTimeWorkRequestBuilder<DeleteJumpFileWorker>()
        )

        WorkManager.getInstance(context).enqueueUniqueWork(
            "deleteJumpWork",
            ExistingWorkPolicy.APPEND,
            deleteFileWorker
        )

        return deleteFileWorker.id
    }

    override suspend fun getJumpFile(
        jumpID: String,
        userID: String,
    ): Result<List<JumpDatapoint>> {

        var result: Result<List<JumpDatapoint>>? = null
        val startTime = System.currentTimeMillis()
        val reference = storage.reference.child("jumpData/$userID/$jumpID")
        val list = mutableListOf<JumpDatapoint>()
        reference.getBytes(ONE_MEGABYTE).addOnSuccessListener { bytes ->
            var string = String(
                bytes = bytes,
                Charsets.UTF_8
            )
            Timber.d("File Retrieved!\n$string")
            val lines = string.lines()
            lines.forEachIndexed { index, line ->
                if (index == 0 || line.isBlank()) {

                } else {
                    var array = line.split(",")

                    val datapoint: JumpDatapoint = JumpDatapoint(
                        array[0],
                        array[1],
                        array[2].toDouble(),
                        array[3].toDouble(),
                        array[4].toFloat(),
                        array[5].toFloat(),
                        array[6].toLong(),
                        array[7].toFloat(),
                        array[8].toFloat(),
                        JumpPhase.valueOf(array[9].toString())
                    )
                    list.add(datapoint)
                    Timber.d("$index $datapoint")
                }

            }

            result = Result.success(list)
        }.addOnFailureListener {
            result = Result.failure(it)

        }

        while (result == null) {
            if (timeout(startTime)) {
                return Result.failure(Exception())
            }
            delay(1000)
        }

        return result as Result<List<JumpDatapoint>>

    }

    override suspend fun getJumpFileMetadata(
        jumpID: String,
        userID: String
    ): Result<StorageMetadata> {
        var result: Result<StorageMetadata>? = null
        val startTime = System.currentTimeMillis()

        val reference = storage.reference.child("jumpData/$userID/$jumpID")
        reference.metadata.addOnSuccessListener { metadata ->

            Timber.d("MetaData Retrieved!\n$metadata")
            Timber.d("${metadata.getCustomMetadata("aircraft")}")
            result = Result.success(metadata)

            // Data for "images/island.jpg" is returned, use this as needed
        }.addOnFailureListener {
            result = Result.failure(it)

        }

        while (result == null) {
            if (timeout(startTime)) {
                return Result.failure(Exception())
            }
            delay(1000)
        }

        return result as Result<StorageMetadata>

    }

}


