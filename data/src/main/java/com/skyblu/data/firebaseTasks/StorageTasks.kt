package com.skyblu.data.firebaseTasks

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storageMetadata
import java.io.FileInputStream

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
        file: Uri,
        metadata : StorageMetadata = storageMetadata { contentType  }
    ): (onSuccess: () -> Unit, onFailure: () -> Unit) -> Task<Uri> {
        return { onSuccess, onFailure ->
            location.putFile(file, metadata).continueWithTask { task ->
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

    /**
     * @param location The firebase storage location to deltee a file from
     */
    fun deleteFile(
        location: StorageReference,
    ): (onSuccess: () -> Unit, onFailure: () -> Unit) -> Task<Void> {
        return { onSuccess, onFailure ->
            location.delete().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onFailure()
                }
            }
        }
    }


    /**
     * @param location The firebase storage location to upload a file to
     * @param metadata The file metadata to update
     */
    fun updateMetadata(
        location: StorageReference,
        metadata : StorageMetadata = storageMetadata { contentType  }
    ): (onSuccess: () -> Unit, onFailure: () -> Unit) -> Task<Uri> {
        return { onSuccess, onFailure ->
            location.updateMetadata(metadata).continueWithTask { task ->
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