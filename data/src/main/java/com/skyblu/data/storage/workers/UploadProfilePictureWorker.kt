package com.skyblu.data.storage.workers

import android.content.Context
import android.net.Uri
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.skyblu.data.firebaseTasks.StorageTasks
import com.skyblu.data.firestore.workers.timeoutTask
import com.skyblu.models.jump.UserParameterNames

class UploadProfilePictureWorker(
    appContext: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(
    appContext,
    workerParameters
) {

    override suspend fun doWork(): Result {
        val firebaseStorage = Firebase.storage.reference
        val photoUriString =
            inputData.getString(UserParameterNames.PHOTO_URL) ?: return Result.failure()
        val userID = inputData.getString(UserParameterNames.ID) ?: return Result.failure()
        val location = firebaseStorage.child("profilePictures/$userID")
        val file = Uri.parse(photoUriString)

        return timeoutTask(
            StorageTasks.uploadFile(
                location = location,
                file = file
            )
        )
    }
}

