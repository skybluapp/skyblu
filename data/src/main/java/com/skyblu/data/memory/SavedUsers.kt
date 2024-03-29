package com.skyblu.data.memory

import androidx.compose.runtime.mutableStateMapOf
import com.google.firebase.auth.FirebaseAuth
import com.skyblu.data.authentication.AuthenticationInterface
import com.skyblu.data.firestore.FireStoreRead
import com.skyblu.data.firestore.toUser
import com.skyblu.models.jump.Jump
import com.skyblu.models.jump.User
import javax.inject.Inject

class SavedUsers @Inject constructor(
    val authentication : AuthenticationInterface,
    private val fireStoreRead: FireStoreRead,
) : SavedUsersInterface {

    override val userMap : MutableMap<String, User> = mutableStateMapOf()

    override fun containsUser(user: String): Boolean {
        return userMap.containsKey(user)
    }

    override fun addUser(user: User) {
        userMap[user.ID] = user
    }

    override fun thisUser() : User?{
        return userMap[authentication.thisUser]
    }

    override suspend fun getThisUser(): User? {
        val user = fireStoreRead.getUser(FirebaseAuth.getInstance().uid!!)
        val document = user.getOrNull()
        return if(document != null){
            addUser(document.toUser())
            document.toUser()
        } else {
            null
        }

    }

    override fun clear() {
        userMap.clear()
    }
}

