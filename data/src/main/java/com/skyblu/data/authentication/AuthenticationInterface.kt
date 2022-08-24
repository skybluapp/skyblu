package com.skyblu.data.authentication

import android.content.Context
import com.google.firebase.auth.AuthResult
import com.skyblu.models.jump.User
import kotlinx.coroutines.flow.Flow


interface AuthenticationInterface {
    fun loginWithEmailAndPassword(email : String, password : String, onSuccess : (String) -> Unit, onFailure : (String) -> Unit)
    fun createAccount(email : String, password: String, confirm : String, onSuccess : (String) -> Unit, onFailure : (String) -> Unit)
    fun deleteAccount()
    fun logout()
    val signedInStatus : Flow<String?>
    var thisUser : String?
}

class EmptyAuthentication(

) : AuthenticationInterface{

    override fun loginWithEmailAndPassword(
        email: String,
        password: String,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun createAccount(
        email: String,
        password: String,
        confirm: String,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun deleteAccount() {
        TODO("Not yet implemented")
    }

    override fun logout() {
        TODO("Not yet implemented")
    }

    override val signedInStatus: Flow<String?>
        get() = TODO("Not yet implemented")
    override var thisUser: String?
        get() = TODO("Not yet implemented")
        set(value) {}

}