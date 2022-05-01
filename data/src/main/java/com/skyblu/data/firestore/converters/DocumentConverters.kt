package com.skyblu.data.firestore

import com.google.firebase.firestore.DocumentSnapshot
import com.skyblu.configuration.Dropzone
import com.skyblu.configuration.Licence
import com.skyblu.models.jump.*

/**
 * Converts a DocumentSnapshot to a Jump Datapoint
 * @return A jump datapoint
 */
fun DocumentSnapshot.toDatapoint() : JumpDatapoint {
    return JumpDatapoint(
        dataPointID = this[DatapointParams.DATAPOINT_ID].toString(),
        jumpID = this[DatapointParams.JUMP_ID].toString(),
        latitude = this[DatapointParams.LATITUDE].toString().toDouble(),
        longitude = this[DatapointParams.LONGITUDE].toString().toDouble(),
        airPressure = this[DatapointParams.AIR_PRESSURE].toString().toFloat(),
        altitude = this[DatapointParams.ALTITUDE].toString().toFloat(),
        timeStamp = this[DatapointParams.TIMESTAMP].toString().toLong(),
        verticalSpeed =this[DatapointParams.VERTICAL_SPEED].toString().toFloat() ,
        groundSpeed = this[DatapointParams.GROUND_SPEED].toString().toFloat(),
        phase = JumpPhase.valueOf(this[DatapointParams.PHASE].toString()) ,
    )
}

/**
 * Converts a DocumentSnapshot to a Jump
 * @return A jump datapoint
 */
fun DocumentSnapshot.toJump() : Jump {
    var jumpNumber = 0
    if(this[UserParameterNames.JUMP_NUMBER] != null){
        jumpNumber = this[JumpParams.JUMP_NUMBER].toString().toInt()
    }

    var dropzone : Dropzone
    try{

        dropzone = enumValueOf(this[JumpParams.DROPZONE].toString())
    }catch (e : IllegalArgumentException){
        dropzone = Dropzone.LANGAR
    }

    return Jump(
        jumpID = this[JumpParams.JUMP_ID].toString(),
        date = this[JumpParams.DATE].toString().toLong(),
        description = this[JumpParams.DESCRIPTION].toString(),
        jumpNumber = jumpNumber,
        userID = this[JumpParams.USER_ID].toString(),
        staticMapUrl = this[JumpParams.STATIC_MAP_URL].toString(),
        uploaded = this[JumpParams.UPLOADED].toString().toBoolean(),
        title = this[JumpParams.TITLE].toString(),
        equipment = this[JumpParams.EQUIPMENT].toString(),
        dropzone =  dropzone ,
        aircraft = this[JumpParams.AIRCRAFT].toString()
    )
}

/**
 * Converts a DocumentSnapshot to a User
 * @return A user datapoint
 */
fun DocumentSnapshot.toUser() : User{

    var licence = Licence.A
    var jumpNumber = 0
    lateinit var friends : List<String>

    if(this[UserParameterNames.LICENCE] != null){
        licence = enumValueOf(this[UserParameterNames.LICENCE].toString())
    }

    if(this[UserParameterNames.JUMP_NUMBER] != null){
        jumpNumber = this[UserParameterNames.JUMP_NUMBER].toString().toInt()
    }

    if(this[UserParameterNames.FRIENDS] != null){

        friends = this[UserParameterNames.FRIENDS] as List<String>
    } else {
        friends = emptyList()
    }



    return User(
        ID = this[UserParameterNames.ID].toString(),
        photoUrl = this[UserParameterNames.PHOTO_URL].toString(),
        username = this[UserParameterNames.USERNAME].toString(),
        bio = this[UserParameterNames.BIO].toString(),
        licence = licence,
        jumpNumber = jumpNumber,
        friends = friends
    )
}

