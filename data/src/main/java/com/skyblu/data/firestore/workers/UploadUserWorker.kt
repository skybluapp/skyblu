package com.skyblu.data.firestore.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.FirebaseFirestore
import com.skyblu.data.firebaseTasks.FirestoreWriteTasks
import com.skyblu.models.jump.JumpParams
import com.skyblu.models.jump.User
import com.skyblu.models.jump.UserParameterNames
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

/**
 * A worker to Upload user data to Firestore. Will return failure if task is not completed within timeout
 */
class UploadUserWorker(
    appContext: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(
    appContext,
    workerParameters
) {

    override suspend fun doWork(): Result {

        val firestore = FirebaseFirestore.getInstance()
        val userString = inputData.getString(UserParameterNames.USER) ?: return Result.failure()
        val user: User = Json.decodeFromString<User>(userString)
        val url: String? = inputData.getString(UserParameterNames.PHOTO_URL)
        user.photoUrl = url

        return timeoutTask(FirestoreWriteTasks.updateUser(map = userToMap(user = user), firestore = firestore, userID = user.ID))
    }

    private fun userToMap(user : User) : Map<String, Any>{
        val map = mutableMapOf<String, Any>()
        map[JumpParams.JUMP_NUMBER] = user.jumpNumber!!
        map[UserParameterNames.LICENCE] = user.licence!!
        if (user.photoUrl != null) {
            map[UserParameterNames.PHOTO_URL] = user.photoUrl!!
        }
        map[UserParameterNames.ID] = user.ID
        map[UserParameterNames.USERNAME] = user.username
        map[UserParameterNames.BIO] = user.bio
        return map
    }

}

