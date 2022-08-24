package com.skyblu.models.jump

import android.location.Location
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.skyblu.configuration.AIRCRAFT_STRING
import com.skyblu.models.R
import kotlinx.serialization.Serializable
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import timber.log.Timber
import java.io.FileWriter
import java.io.IOException
import java.util.*
import kotlin.math.absoluteValue

/**
 * Name of table for skydive data points for Room databases
 *@author Oliver Stocks
 */
const val JUMP_DATA_POINT_TABLE = "jump_data_point_table"

/**
 * A representation of a single point during a skydive
 * @property dataPointID A unique identifier for the data point
 * @property jumpID A unique identifier for the skydive
 * @property latitude The latitude of the point
 * @property longitude The Longitude of the point
 * @property airPressure The Air Pressure of the current Point in hPa
 * @property altitude The Altitude of the current point in meters
 * @property timeStamp The timestamp the moment the datapoint was recorded
 * @property verticalSpeed The current vertical speed the moment the datapoint was recorded in m/s
 * @property groundSpeed The speed across the ground the moment the datapoint was recorded
 * @property phase The current phase in the skydive
 */
@Serializable
@Entity(JUMP_DATA_POINT_TABLE)
data class JumpDatapoint(
    @PrimaryKey
    val dataPointID: String,
    val jumpID: String,
    var latitude: Double,
    var longitude: Double,
    var airPressure: Float,
    var altitude: Float,
    val timeStamp: Long,
    val verticalSpeed: Float,
    val groundSpeed: Float,
    var phase: JumpPhase = JumpPhase.UNKNOWN
)

/**
 * Parameter names for a skydive datapoint
 */
object DatapointParams {

    const val DATAPOINT = "datapoint"
    const val DATAPOINT_ID = "dataPointID"
    const val JUMP_ID = "jumpID"
    const val LATITUDE = "latitude"
    const val LONGITUDE = "longitude"
    const val AIR_PRESSURE = "airPressure"
    const val ALTITUDE = "altitude"
    const val TIMESTAMP = "timeStamp"
    const val VERTICAL_SPEED = "verticalSpeed"
    const val GROUND_SPEED = "groundSpeed"
    const val PHASE = "phase"
}

/**
 * The phase of a skydive
 */
enum class JumpPhase(
    val title: String,
    @DrawableRes val icon: Int
) {

    /**
     * The user has begun tracking, but is not yet in the aircraft
     */
    WALKING(
        "Walking",
        R.drawable.walk
    ),

    /**
     * The user is in the aircraft ascending to altitude
     */
    AIRCRAFT(
        AIRCRAFT_STRING,
        R.drawable.aircraft
    ),

    /**
    The user has exited the aircraft and is in freefall descending quickly
     **/
    FREEFALL(
        "Freefall",
        R.drawable.freefall
    ),

    /*
    The user has deployed their parachute and is descending slowly
     */
    CANOPY(
        "Canopy",
        R.drawable.parachute
    ),

    /*
    The user has landed and is now longer descending
     */
    LANDED(
        "Landed",
        R.drawable.walk
    ),

    /*
    It is not known what phase of the skydive the user is in
     */
    UNKNOWN(
        "Unknown",
        R.drawable.unknown
    )
}



/**
 * @return The maximum vertical speed of a list of Skydiving tracking points in a specified direction, or of either donation if direction is unspecified
 * @param direction The direction of maximum speed
 */
fun List<JumpDatapoint>.maxVerticalSpeed(direction: VerticalDirection?): Float? {
    return when (direction) {
        VerticalDirection.DOWNWARD -> {
            minOfOrNull { jumpDatapoint: JumpDatapoint -> jumpDatapoint.verticalSpeed }

        }

        VerticalDirection.UPWARD -> {
            maxOfOrNull { jumpDatapoint: JumpDatapoint -> jumpDatapoint.verticalSpeed }
        }

        null -> {
            maxOf { jumpDatapoint: JumpDatapoint -> jumpDatapoint.verticalSpeed.absoluteValue }
        }
    }
}

fun List<JumpDatapoint>.averageVerticalSpeed(): Double {
    return sumOf { jumpDatapoint: JumpDatapoint -> jumpDatapoint.verticalSpeed.toDouble() } / size.toDouble()
}

fun List<JumpDatapoint>.averageGroundSpeed(): Double {
    return sumOf { jumpDatapoint: JumpDatapoint -> jumpDatapoint.groundSpeed.toDouble() } / size.toDouble()
}

/**
 * STILL IN DEVELOPMENT:
 * Determines the phases of a jump
 */
fun List<JumpDatapoint>.calculatePhases() : List<JumpDatapoint>{
    val indexHighestAlt = indexOfFirst { jd -> jd.altitude == this.maxAltitude()}
    forEachIndexed{index, dataPoint ->
        if(index >= indexHighestAlt){
            dataPoint.phase = JumpPhase.FREEFALL
        } else {
            dataPoint.phase = JumpPhase.AIRCRAFT
        }
    }
    val canopyStartIndex = indexOfLast { jd -> jd.phase == JumpPhase.FREEFALL && jd.verticalSpeed < -20 }
    forEachIndexed{index, dataPoint ->
        if(index  > canopyStartIndex){
            dataPoint.phase = JumpPhase.CANOPY
        }
    }

    val aircraftStartIndex = indexOfFirst { jd -> jd.phase == JumpPhase.AIRCRAFT && jd.altitude > 40 && jd.groundSpeed > 10 }
    forEachIndexed{index, dataPoint ->
        if(index < aircraftStartIndex){
            dataPoint.phase = JumpPhase.WALKING
        }
    }

    return this

}

/**
 * Enum to specify vertical direction of travel
 */
enum class VerticalDirection {

    /**
     * Upward direction of travel
     */
    UPWARD,

    /**
     * Downward direction of travel
     */
    DOWNWARD
}

/**
 * @return The maximum ground speed from a list of skydiving tracking points
 */
fun List<JumpDatapoint>.maxGroundSpeed(): Float? {

    return maxOfOrNull { skydiveDataPoint -> skydiveDataPoint.groundSpeed }
}

/**
 * @return The minimum ground speed from a list of skydiving tracking points
 */
fun List<JumpDatapoint>.minGroundSpeed(): Float {
    return minOf { skydiveDataPoint -> skydiveDataPoint.groundSpeed }
}

/**
 * @return The maximum latitude from a list of skydiving tracking points
 */
fun List<JumpDatapoint>.maxLatitude(): Double? {
    return maxOfOrNull { skydiveDataPoint -> skydiveDataPoint.latitude }
}

/**
 * @return The minimum latitude  from a list of skydiving tracking points
 */
fun List<JumpDatapoint>.minLatitude(): Double? {
    return minOfOrNull { skydiveDataPoint -> skydiveDataPoint.latitude }
}

/**
 * @return The maximum longitude from a list of skydiving tracking points
 */
fun List<JumpDatapoint>.maxLongitude(): Double? {
    return maxOfOrNull { skydiveDataPoint -> skydiveDataPoint.longitude }
}

/**
 * @return The minimum longitude  from a list of skydiving tracking points
 */
fun List<JumpDatapoint>.minLongitude(): Double? {
    return minOfOrNull { skydiveDataPoint -> skydiveDataPoint.longitude }
}

/**
 * Returns the maximum altitude from a list of skydiving tracking points
 */
fun List<JumpDatapoint>.maxAltitude(): Float {
    return maxOf { skydiveDataPoint -> skydiveDataPoint.altitude }
}

/**
 * @return The minimum altitude  from a list of skydiving tracking points
 */
fun List<JumpDatapoint>.minAltitude(): Float {
    return minOf { skydiveDataPoint -> skydiveDataPoint.altitude }
}

/**
 * @return Centre point of skydiving tracking points
 */
fun List<JumpDatapoint>.centerPoint(): LatLng {
    return LatLng(sumOf { point -> point.latitude } / size,
        sumOf { point -> point.longitude } / size)
}

/**
 * @return Get the most southeasterly point of a list of skydiving tracking points
 */
fun List<JumpDatapoint>.southwest(): LatLng? {
    return minLatitude()?.let {
        minLongitude()?.let { it1 ->
            LatLng(
                it,
                it1
            )
        }
    }
}

/**
 * @return Get the most northwesterly point of a list of skydiving tracking points
 */
fun List<JumpDatapoint>.northeast(): LatLng? {
    return maxLatitude()?.let {
        maxLongitude()?.let { it1 ->
            LatLng(
                it,
                it1
            )
        }
    }
}

/** @return Get bounds from a list of skydiving tracking points
 *
 */
fun List<JumpDatapoint>.bounds(): LatLngBounds? {
    return northeast()?.let {
        southwest()?.let { it1 ->
            LatLngBounds(
                it1,
                it
            )
        }
    }
}

/**
 * @return Return a list of LatLng points from a list of skydiving tracking points
 */
fun List<JumpDatapoint>.latLngList(): List<LatLng> {
    val latLngList: MutableList<LatLng> = mutableListOf()
    for (i in indices) {
        latLngList.add(
            LatLng(
                elementAt(i).latitude,
                elementAt(i).longitude
            )
        )
    }
    return latLngList
}

/**
 * @return The most recent datapoint from a list
 */
fun List<JumpDatapoint>.newest(): JumpDatapoint? {
    return maxByOrNull { it.timeStamp }
}

/**
 * @return The least recent datapoint from a list
 */
fun List<JumpDatapoint>.oldest(): JumpDatapoint? {
    return minByOrNull { it.timeStamp }
}

/**
 * @return All datapoints of a given phase, ordered by time
 */
fun List<JumpDatapoint>.filterByPhase(phase: JumpPhase): List<JumpDatapoint> {
    return filter { point -> point.phase == phase }.sortedBy { it.timeStamp }
}

/**
 * @return returns the total distance of a list of datapoints
 */
fun List<JumpDatapoint>.calculateDistanceOfList(): Float {
    var oneStartIndex = 0
    var twoStartIndex = 1
    var totalDistance = 0f

    while (twoStartIndex <= indices.last) {
        val one = Location("LocationOne")
        one.latitude = this[oneStartIndex].latitude
        one.longitude = this[oneStartIndex].longitude
        val two = Location("LocationTwo")
        two.latitude = this[twoStartIndex].latitude
        two.longitude = this[twoStartIndex].longitude
        totalDistance += one.distanceTo(two)
        oneStartIndex++
        twoStartIndex++
    }
    return totalDistance
}

/**
 * Saves a list of datapoints as a CSV file
 */
fun List<JumpDatapoint>.toCsv(filename : String) {
    try {
        CSVPrinter(
            FileWriter("/data/data/com.skyblu.skyblu/files/${filename}.csv"),
            CSVFormat.DEFAULT.withHeader(
                DatapointParams.DATAPOINT_ID,
                DatapointParams.JUMP_ID,
                DatapointParams.LATITUDE,
                DatapointParams.LONGITUDE,
                DatapointParams.AIR_PRESSURE,
                DatapointParams.ALTITUDE,
                DatapointParams.TIMESTAMP,
                DatapointParams.VERTICAL_SPEED,
                DatapointParams.GROUND_SPEED,
                DatapointParams.PHASE
            )
        ).use { printer ->
            for (jumpDatapoint in this) {
                printer.printRecord(
                    jumpDatapoint.dataPointID.toString(),
                    jumpDatapoint.jumpID.toString(),
                    jumpDatapoint.latitude.toString(),
                    jumpDatapoint.longitude.toString(),
                    jumpDatapoint.airPressure.toString(),
                    jumpDatapoint.altitude.toString(),
                    jumpDatapoint.timeStamp.toString(),
                    jumpDatapoint.verticalSpeed.toString(),
                    jumpDatapoint.groundSpeed.toString(),
                    jumpDatapoint.phase.toString()
                )
            }
        }
    } catch (ex: IOException) {
        ex.printStackTrace()
    }

}


