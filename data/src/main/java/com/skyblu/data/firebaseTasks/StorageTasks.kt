package com.skyblu.data.firebaseTasks

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.StorageReference

/**
 * Provides functions to run actions on Firestore Storage
 * Note that functions may trigger additional cloud functions
 */
object StorageTasks {

    /**
     * @param location The firebase storage location to upload a file to
     * @param file The file to upload
     */
    fun uploadFile(
        location: StorageReference,
        file: Uri
    ): (onSuccess: () -> Unit, onFailure: () -> Unit) -> Task<Uri> {
        return { onSuccess, onFailure ->
            location.putFile(file).continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        onFailure()
                    }
                }
                location.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onFailure()
                }
            }
        }
    }
}