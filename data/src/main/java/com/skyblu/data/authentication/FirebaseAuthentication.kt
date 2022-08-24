package com.skyblu.data.authentication

import com.google.firebase.auth.FirebaseAuth
import com.skyblu.data.firebaseTasks.AuthenticationTasks
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.IllegalArgumentException
import javax.inject.Inject

/**
 * Manages authentication for users on Firebase backend
 */
class FirebaseAuthentication @Inject constructor(
) : AuthenticationInterface {

    private val auth = FirebaseAuth.getInstance()
    override var thisUser : String? = null

    /**
     * @return A flow that emits the signed in users UID every second
     */
    override val signedInStatus : Flow<String?> = flow {
        while(true){
            val id = getThisUserID()
            thisUser = id
            emit(getThisUserID())
            delay(1000)
        }
    }

    init {

    }

    /**
     * @return The UID of the currently signed in user, or null if not signed in
     */
    private fun getThisUserID(): String? {
        return FirebaseAuth.getInstance().currentUser?.uid
    }

    /**
     * Calls upon Firebase Authentication to sign a user in with Email and Password
     * @param email The users email address
     * @param password The users password
     * @param onSuccess A function to run if the user signs in successfully
     * @param onFailure A function to run if the user cannot be signed in
     */
    override fun loginWithEmailAndPassword(
        email: String,
        password: String,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {

        try {
            val task = AuthenticationTasks.signInWithEmailAndPassword(auth, email = email, password = password)
            task(onSuccess, onFailure)
        } catch (exception : Exception){
            when(exception){
                is IllegalArgumentException -> {
                    onFailure("Email or Username is empty")
                }
                else -> {
                    onFailure(exception.toString())
                }
            }
        }

    }

    /**
     * Calls upon Firebase Authentication to create a new account
     * @param email The users email address
     * @param password The users password
     * @param confirm Must be identical to password to create an account
     * @param onSuccess A function to run if the account is created successfully
     * @param onFailure A function to run if the account cannot be created
     */
    override fun createAccount(
        email: String,
        password: String,
        confirm: String,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {

        if (password != confirm) {
            onFailure("Passwords do not match.")
            return
        }
        try {
            val task = AuthenticationTasks.createAccountWithEmailAndPassword(auth, email = email, password = password)
            task(onSuccess, onFailure)
        } catch (exception: Exception) {
            when (exception.message) {
                "Given String is empty or null" -> {
                    onFailure("Email or Username is empty")
                }
                else -> {
                    onFailure(exception.toString())
                }
            }
        }
    }

    /**
     * Calls upon Firebase Authentication to delete the users account
     */
    override fun deleteAccount() {
        AuthenticationTasks.deleteUser(auth)
    }

    /**
     * Calls upon Firebase Authentication sign out a user
     */
    override fun logout() {
        auth.signOut()
    }
}