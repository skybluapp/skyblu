package com.skyblu.data.memory

import com.skyblu.models.jump.Jump
import com.skyblu.models.jump.User

/**
 * An interface that allows access to a map of users saved in memory
 */
interface SavedUsersInterface {
    val userMap :  MutableMap<String, User>
    fun containsUser(user : String) : Boolean
    fun addUser(user : User)
    fun clear()
    fun thisUser() : User?
    suspend fun getThisUser() : User?
}

