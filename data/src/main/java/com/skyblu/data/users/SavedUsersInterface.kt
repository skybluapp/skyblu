package com.skyblu.data.users

import com.skyblu.models.jump.Jump
import com.skyblu.models.jump.User

interface SavedUsersInterface {
    val userMap :  MutableMap<String, User>
    fun containsUser(user : String) : Boolean
    fun addUser(user : User)
    fun clear()
    fun thisUser() : User?
    suspend fun getThisUser() : User?
}

interface SavedSkydivesInterface {
    var skydive : Jump?
}