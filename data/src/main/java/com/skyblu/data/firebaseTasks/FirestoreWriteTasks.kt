package com.skyblu.data.firebaseTasks

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.skyblu.configuration.DATAPOINTS_COLLECTION
import com.skyblu.configuration.JUMPS_COLLECTION
import com.skyblu.configuration.USERS_COLLECTION
import com.skyblu.models.jump.Jump
import com.skyblu.models.jump.JumpDatapoint
import com.skyblu.models.jump.UserParameterNames
import timber.log.Timber

/**
 * Provides functions to run actions on the Firestore Database
 * Note that functions may trigger additional cloud functions
 */
object FirestoreWriteTasks {

    /**
     * @param firestore The firestore database to act upon
     * @param userID The ID of the user that is adding a friend
     * @param friendID The ID of the friend to be added to the user
     * @return Returns a task to add a user as a friend
     */
    fun addFriend(
        firestore: FirebaseFirestore,
        userID: String,
        friendID: String
    ): (onSuccess : () -> Unit, onFailure : () -> Unit) -> Task<Void>{
        return { onSuccess , onFailure ->
            firestore.collection(USERS_COLLECTION).document(userID)
                .update(
                    UserParameterNames.FRIENDS,
                    FieldValue.arrayUnion(friendID)
                )
                .addOnFailureListener {
                    Timber.d("Add Friend Failed" + it.message)
                    onFailure()
                }
                .addOnSuccessListener {
                    onSuccess()
                }
        }
    }

    /**
     * @param firestore The firestore database to act upon
     * @param userID The ID of the user that is removing a friend
     * @param friendID The ID of the friend to be removed from the user
     * @return Returns a task to add a user as a friend
     */
    fun unfriend(
        firestore: FirebaseFirestore,
        userID: String,
        friendID: String
    ): (onSuccess : () -> Unit, onFailure : () -> Unit) -> Task<Void>{
        return { onSuccess , onFailure ->
            firestore.collection(USERS_COLLECTION).document(userID)
                .update(
                    UserParameterNames.FRIENDS,
                    FieldValue.arrayRemove(friendID)
                )
                .addOnFailureListener {
                    onFailure()
                }
                .addOnSuccessListener {
                    onSuccess()
                }
        }
    }


    /**
     * @param firestore The firestore database to act upon
     * @param map The mapping of values to update
     * @param userID The user to update
     * @return A task to attempt to update a user
     */
    fun updateUser(
        firestore: FirebaseFirestore,
        map : Map<String, Any>,
        userID : String
    ): (onSuccess : () -> Unit, onFailure : () -> Unit) -> Task<Void>{
        return { onSuccess, onFailure ->
            firestore.collection(USERS_COLLECTION).document(userID).set(map,
                SetOptions.merge()
            )
                .addOnFailureListener {
                    onFailure()
                }
                .addOnSuccessListener {
                    onSuccess()
                }
        }
    }


}




