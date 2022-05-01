package com.skyblu.data.firebaseTasks

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.storage.StorageReference
import java.lang.Exception

/**
 * Provides functions to run actions on Firestore Authentication
 * Note that functions may trigger additional cloud functions
 */
object AuthenticationTasks {

    /**
     * @param firebaseAuth The firebase authentication service to act upon
     * @param email The new email to sign in
     * @param password The users password
     * @return a task that can be executed to sign in a user
     */
    fun signInWithEmailAndPassword(
        firebaseAuth : FirebaseAuth,
        email: String,
        password: String,
    ): (onSuccess: (uid : String) -> Unit, onFailure: (error : String) -> Unit) -> Task<AuthResult> {
        return { onSuccess, onFailure ->
            firebaseAuth.signInWithEmailAndPassword(
                email,
                password
            )
                .addOnSuccessListener {
                    onSuccess(it.user!!.uid)
                }
                .addOnFailureListener { exception : Exception ->
                    when(exception){
                        is FirebaseAuthInvalidCredentialsException -> {
                            onFailure("Incorrect Email or password")
                        }
                        else -> {
                            onFailure(exception.toString())
                        }
                    }
                }
        }
    }

    /**
     * @param firebaseAuth The firebase authentication service to act upon
     * @param email The new email to add
     * @param password The password the user wants to add
     * @return a task that can be executed to create a users account
     */
    fun createAccountWithEmailAndPassword(
        firebaseAuth : FirebaseAuth,
        email: String,
        password: String,
    ): (onSuccess: (uid : String) -> Unit, onFailure: (error : String) -> Unit) -> Task<AuthResult> {
        return {onSuccess, onFailure ->
            firebaseAuth.createUserWithEmailAndPassword(
                email.trim(),
                password
            ).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess(firebaseAuth.currentUser!!.uid)
                } else {
                    onFailure(task.exception?.message.toString())
                }
            }
        }
    }

    /**
     * Returns a runnable task to delete the currently signed in user
     * @param firebaseAuth The firebase authentication service to act upon
     * @return a task that can be executed to delete the currently signed in user
     */
    fun deleteUser(
        firebaseAuth : FirebaseAuth,
    ): (onSuccess: () -> Unit, onFailure: (error : String) -> Unit) -> Task<Void> {
        return {onSuccess, onFailure ->
            firebaseAuth.currentUser!!.delete().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onFailure(task.exception?.message.toString())
                }
            }
        }
    }

}