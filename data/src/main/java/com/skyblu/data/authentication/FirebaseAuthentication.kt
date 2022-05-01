package com.skyblu.data.authentication

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.skyblu.data.Repository
import com.skyblu.data.firebaseTasks.AuthenticationTasks
import com.skyblu.data.firestore.FireStoreRead
import com.skyblu.data.firestore.toUser
import com.skyblu.data.users.SavedUsersInterface
import com.skyblu.models.jump.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import java.lang.IllegalArgumentException
import javax.inject.Inject

class FirebaseAuthentication @Inject constructor(
) : AuthenticationInterface {

    override val loggedInFlow : Flow<String?> = flow<String?> {
        while(true){
            emit(getThisUserID())
            delay(1000)
        }
    }


    val auth = FirebaseAuth.getInstance()
    override fun getThisUserID(): String? {
        return FirebaseAuth.getInstance().currentUser?.uid
    }



    override fun login(
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
            val task = AuthenticationTasks.signInWithEmailAndPassword(auth, email = email, password = password)
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

    override fun deleteAccount() {
        val task = AuthenticationTasks.deleteUser(auth)
    }

    override fun logout() {
        auth.signOut()
    }

    override fun initialise(context: Context) {
        FirebaseApp.initializeApp(context)
    }
}