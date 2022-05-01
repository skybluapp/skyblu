package com.skyblu.data.storage

import android.content.Context
import android.net.Uri
import androidx.work.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.skyblu.data.firebaseTasks.StorageTasks
import com.skyblu.data.firestore.workers.UploadUserWorker
import com.skyblu.data.firestore.workers.timeoutTask
import com.skyblu.data.storage.workers.UploadProfilePictureWorker
import com.skyblu.models.jump.User
import com.skyblu.models.jump.UserParameterNames
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*

class FirebaseStorage : StorageInterface {

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

    override suspend fun updateUser(
        applicationContext: Context,
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
            WorkManager.getInstance(applicationContext).beginUniqueWork(
                "updateUserWork",
                ExistingWorkPolicy.REPLACE,
                uploadProfilePictureWorker
            ).then(
                uploadUserWorker
            ).enqueue()
            return uploadUserWorker.id

        } else {
            WorkManager.getInstance(applicationContext).enqueueUniqueWork(
                "updateUserWork",
                ExistingWorkPolicy.REPLACE,
                uploadUserWorker
            )
            return uploadUserWorker.id
        }
    }
}


