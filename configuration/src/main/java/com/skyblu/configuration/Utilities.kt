package com.skyblu.configuration

import android.media.AudioManager
import android.media.ToneGenerator
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.pow

/**
 * Miscellaneous functions that perform additional functions
 */

fun Float.hpaToMeters() : Float{
    return  (44330 * (1 - (this/1013.25).pow(1/5.255))).toFloat()
}
fun Float.hpaToFeet() : Float{
    return (3.28084 * this.hpaToMeters()).toFloat()
}

fun Float.feetToMeters() : Float{
    return (this / 3.28084).toFloat()
}

fun Float.metersToFeet() : Float{
    return (this * 3.28084).toFloat()
}

fun Long.millisToDateString() : String {
    val date = Date(this)
    val dateFormat: DateFormat = SimpleDateFormat("dd-MM-yy")
    val p = 3
    return dateFormat.format(date)
}

fun Int.suffix() : String {
    return when{
        this == 1 -> {
            "st"
        }
        this == 2 -> {
            "nd"
        }
        this == 3 -> {
            "rd"
        }
        else -> {
            "th"
        }
    }
}

fun emptyString() : String{
    return ""
}


fun playTone() {
    val tone = ToneGenerator(
        AudioManager.STREAM_MUSIC,
        100
    )
    tone.startTone(
        ToneGenerator.TONE_CDMA_ABBR_ALERT,
        150
    )
}