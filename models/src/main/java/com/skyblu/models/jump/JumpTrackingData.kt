package com.skyblu.models.jump

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import timber.log.Timber


data class JumpTrackingData(
    val jumpId : String,
    var walkingTrackingPoints : MutableList<JumpDatapoint> = mutableListOf<JumpDatapoint>(),
    var aircraftTrackingPoints : MutableList<JumpDatapoint> = mutableListOf<JumpDatapoint>(),
    var freefallTrackingPoints : MutableList<JumpDatapoint> = mutableListOf<JumpDatapoint>(),
    var canopyTrackingPoints : MutableList<JumpDatapoint> = mutableListOf<JumpDatapoint>(),
    var landedTrackingPoints : MutableList<JumpDatapoint> = mutableListOf<JumpDatapoint>(),
){
    fun allTrackingPoints(): List<JumpDatapoint> {
        return walkingTrackingPoints + aircraftTrackingPoints + freefallTrackingPoints + canopyTrackingPoints + landedTrackingPoints
    }
    fun importantTrackingPoints(): List<JumpDatapoint> {
        return walkingTrackingPoints + aircraftTrackingPoints + freefallTrackingPoints + canopyTrackingPoints + landedTrackingPoints
    }
    fun getMinPressure(list : List<JumpDatapoint>) : Float {
        return list.minOf { it.airPressure }
    }
    fun getMinPressure() : Float {
        return allTrackingPoints().minOf { it.airPressure }
    }
    fun getMaxPressure(list : List<JumpDatapoint>) : Float {
        return list.maxOf { it.airPressure }
    }
    fun getMaxPressure() : Float {
        return allTrackingPoints().maxOf { it.airPressure }
    }
    private fun getMinLatitude(list : List<JumpDatapoint>) : Double {
        return list.minOf { it.latitude }
    }
    private fun getMaxLatitude(list : List<JumpDatapoint>) : Double {
        return list.maxOf { it.latitude }
    }
    private fun getMinLongitude(list : List<JumpDatapoint>) : Double {
        return list.minOf { it.longitude }
    }
    private fun getMaxLongitude(list : List<JumpDatapoint>) : Double {
        return list.maxOf { it.longitude }
    }
    fun getCenterPoint(list: List<JumpDatapoint>) : LatLng{
        var latTotal = 0.0
        var lngTotal = 0.0
        for (i in list.indices){
            latTotal += list[i].latitude
            lngTotal += list[i].longitude
        }
        return LatLng(latTotal/list.size, lngTotal/list.size)
    }
    private fun getSouthwestCameraPoint() : LatLng{

        val minLat = getMinLatitude(importantTrackingPoints())
        val minLong = getMinLongitude(importantTrackingPoints())

        return LatLng(minLat, minLong)
    }
    private fun getNorthEastCameraPoint() : LatLng{
        val maxLat = getMaxLatitude(importantTrackingPoints())
        val maxLong = getMaxLongitude(importantTrackingPoints())

        return LatLng(maxLat, maxLong)
    }
    fun getCameraBounds() : LatLngBounds {
        if(importantTrackingPoints().isNotEmpty()){
            return LatLngBounds(getSouthwestCameraPoint(), getNorthEastCameraPoint())
        } else {
            return LatLngBounds(LatLng(0.0, 0.0), LatLng(0.0, 0.0))
        }

    }
    fun createLatLngList(list : List<JumpDatapoint>) : List<LatLng>{
        val latLngList : MutableList<LatLng> = mutableListOf()
        for(i in list.indices){

            latLngList.add(LatLng(list[i].latitude, list[i].longitude))
        }
        return latLngList
    }
    fun getLastTrackingPoint() : JumpDatapoint?{
        if(landedTrackingPoints.isNotEmpty()){
            return landedTrackingPoints.last()
        }
        if(canopyTrackingPoints.isNotEmpty()){
            return canopyTrackingPoints.last()
        }
        if(freefallTrackingPoints.isNotEmpty()){
            return freefallTrackingPoints.last()
        }
        if(aircraftTrackingPoints.isNotEmpty()){
            return aircraftTrackingPoints.last()
        }
        if(walkingTrackingPoints.isNotEmpty()){
            return walkingTrackingPoints.last()
        }
        return null
    }
    fun getFirstTrackingPoint() : JumpDatapoint? {
            return walkingTrackingPoints.firstOrNull()
    }
}

