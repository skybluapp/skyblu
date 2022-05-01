package com.skyblu.data.storage

import android.content.Context
import android.net.Uri
import com.skyblu.models.jump.User
import java.util.*

interface StorageInterface {
    suspend fun updateUser(applicationContext : Context, userID : String, user : User, uri : Uri?) : UUID
}