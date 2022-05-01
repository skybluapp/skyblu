package com.skyblu.models.jump

import com.google.android.gms.maps.model.LatLng
import com.skyblu.configuration.Dropzone
import com.skyblu.configuration.hpaToFeet
import java.util.*

val latList: List<Double> = listOf(
    52.89820,
    52.90296,
    52.91208,
    52.91974,
    52.91725,
    52.91456,
    52.91063,
    52.90504,
    52.89841,
    52.89095,
    52.88660,
    52.88805,
    52.89240,
    52.89696,
    52.90338,
    52.91083,
    52.91974,
    52.92657,
    52.92574,
    52.92408,
    52.92098,
    52.91663,
    52.91125,
    52.90359,
    52.89054,
    52.88474,
    52.88059,
    52.88080,
    52.88515,
    52.88785,
    52.89302,
    52.90338,
    52.91435,
    52.92574,
    52.93257,
    52.93360,
    52.93422,
    52.93153,
    52.92657,
    52.92222,
    52.91725,
    52.90566,
    52.89717,
    52.88785,
    52.88142,
    52.87749,
    52.87583,
    52.87666,
    52.87935,
    52.88039,
    52.88702,
    52.89696,
    52.90524,
    52.91394,
    52.92325,
    52.93008,
    52.93776,
    52.94024,
    52.94065,
    52.93900,
    52.93672,
    52.93217,
    52.92472,
    52.91230,
    52.90402,
    52.89180,
    52.88227,
    52.87584,
    52.87024,
    52.86941,
    52.86776,
    52.86651,
    52.86913,
    52.87266,
    52.87763,
    52.88592,
    52.90311,
    52.91740,
    52.93168,
    52.93830
)
val lngList: List<Double> = listOf(
    -0.90800,
    -0.90010,
    -0.89255,
    -0.89598,
    -0.90422,
    -0.91212,
    -0.92654,
    -0.92860,
    -0.93135,
    -0.92414,
    -0.91452,
    -0.90354,
    -0.89324,
    -0.88088,
    -0.88053,
    -0.88225,
    -0.88225,
    -0.88774,
    -0.90422,
    -0.91830,
    -0.92757,
    -0.93375,
    -0.94233,
    -0.94439,
    -0.94062,
    -0.93341,
    -0.92517,
    -0.91281,
    -0.89736,
    -0.88225,
    -0.87229,
    -0.87092,
    -0.86920,
    -0.87401,
    -0.88053,
    -0.89255,
    -0.91212,
    -0.92654,
    -0.93787,
    -0.94748,
    -0.95332,
    -0.95469,
    -0.95744,
    -0.95263,
    -0.94817,
    -0.93821,
    -0.92002,
    -0.90594,
    -0.89461,
    -0.88637,
    -0.87298,
    -0.86096,
    -0.86028,
    -0.86028,
    -0.85890,
    -0.86440,
    -0.87211,
    -0.88481,
    -0.90061,
    -0.91949,
    -0.93631,
    -0.94867,
    -0.96172,
    -0.96721,
    -0.96584,
    -0.96584,
    -0.96240,
    -0.95931,
    -0.94558,
    -0.93322,
    -0.92361,
    -0.90850,
    -0.89795,
    -0.88868,
    -0.87563,
    -0.85984,
    -0.84817,
    -0.84885,
    -0.85057,
    -0.85744
)
val pressureList: List<Float> = listOf<Float>(
    1014f,
    1001f,
    989f,
    977f,
    965f,
    953f,
    941f,
    929f,
    917f,
    905f,
    893f,
    881f,
    869f,
    857f,
    845f,
    833f,
    821f,
    809f,
    797f,
    785f,
    773f,
    761f,
    749f,
    737f,
    725f,
    713f,
    701f,
    689f,
    677f,
    665f,
    653f,
    641f,
    629f,
    617f,
    605f,
    593f,
    581f,
    631f,
    681f,
    731f,
    781f,
    831f,
    835f,
    840f,
    845f,
    850f,
    855f,
    860f,
    865f,
    870f,
    875f,
    880f,
    885f,
    890f,
    895f,
    890f,
    895f,
    900f,
    905f,
    910f,
    915f,
    920f,
    925f,
    930f,
    935f,
    940f,
    945f,
    950f,
    955f,
    960f,
    965f,
    970f,
    975f,
    980f,
    985f,
    990f,
    995f,
    1000f,
    1005f,
    1010f
)

fun generateSampleJump(): Jump {
    return Jump(
        title = "Generated Jump",
        aircraft = "Cessna 201B",
        equipment = "Sabre 2",
        dropzone = Dropzone.LANGAR,
        userID = "",
        jumpNumber = 100,
        description = "A nice peaceful skydive",
        jumpID = UUID.randomUUID().toString(),
        uploaded = true
    )
}

fun generateSampleTrackingData(): JumpTrackingData {
    val walkingTrackingPoints: MutableList<JumpDatapoint> = mutableListOf()
    val aircraftTrackingPoints: MutableList<JumpDatapoint> = mutableListOf()
    val freefallTrackingPoints: MutableList<JumpDatapoint> = mutableListOf()
    val canopyTrackingPoints: MutableList<JumpDatapoint> = mutableListOf()
    val landedTrackingPoints: MutableList<JumpDatapoint> = mutableListOf()

    for (i in lngList.indices) {
        val loca = LatLng(
            latList[i],
            lngList[i]
        )
        val trackingPoint = JumpDatapoint(
            dataPointID = "5",
            latitude = latList[i],
            longitude = lngList[i],
            airPressure = pressureList[i],
            altitude = pressureList[i].hpaToFeet(),
            timeStamp = System.currentTimeMillis(),
            verticalSpeed = 0f,
            groundSpeed = 0f,
            jumpID = "5"
        )
        when (i) {
            in 0 .. 36 -> {
                aircraftTrackingPoints.add(trackingPoint)
                if (i == 36) {
                    freefallTrackingPoints.add(trackingPoint)
                }
            }
            in 37 .. 60 -> {
                freefallTrackingPoints.add(trackingPoint)
                if (i == 60) {
                    canopyTrackingPoints.add(trackingPoint)
                }
            }
            in 61 .. 74 -> {
                canopyTrackingPoints.add(trackingPoint)
                if (i == 74) {
                    landedTrackingPoints.add(trackingPoint)
                }
            }
            in 75 .. 1000 -> {
                landedTrackingPoints.add(trackingPoint)
            }
        }
    }

    return JumpTrackingData(
        jumpId = UUID.randomUUID().toString(),
        walkingTrackingPoints = walkingTrackingPoints,
        aircraftTrackingPoints = aircraftTrackingPoints,
        freefallTrackingPoints = freefallTrackingPoints,
        canopyTrackingPoints = canopyTrackingPoints,
        landedTrackingPoints = landedTrackingPoints
    )
}

fun generateSampleJumpList(): List<Jump> {
    return listOf(
        generateSampleJump(),
        generateSampleJump(),
        generateSampleJump(),
        generateSampleJump(),
        generateSampleJump(),
        generateSampleJump()
    )
}


