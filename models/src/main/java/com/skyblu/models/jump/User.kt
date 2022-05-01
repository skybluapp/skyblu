package com.skyblu.models.jump

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.skyblu.configuration.Licence
import com.skyblu.configuration.UNKNOWN_USER_STRING
import com.skyblu.configuration.emptyString
import kotlinx.serialization.Serializable

/**
 * @author Oliver Stocks
 *
 */
@Entity(tableName = "user_table")
@Serializable
data class User(
    @PrimaryKey
    var ID : String,
    var jumpNumber : Int = 0,
    var photoUrl : String? = null,
    var username : String = emptyString(),
    var bio : String = emptyString(),
    var licence : Licence = Licence.A,
    var friends : List<String> = emptyList()
)



/**
 * Parameter names for a skydive
 */
object UserParameterNames{
    const val USER = "user"
    const val ID = "id"
    const val PHOTO_URL = "photoUrl"
    const val USERNAME = "username"
    const val BIO = "bio"
    const val LICENCE = "licence"
    const val JUMP_NUMBER = "jumpNumber"
    const val FRIENDS = "friends"
}

