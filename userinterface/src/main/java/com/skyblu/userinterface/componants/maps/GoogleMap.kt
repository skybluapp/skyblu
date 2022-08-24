package com.skyblu.userinterface.componants.maps

import android.content.Context
import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import com.skyblu.configuration.*
import com.skyblu.models.jump.*
import com.skyblu.userinterface.R

/**
 * A composable map showing jump datapoints as coloured polylines on a map for each phase of a skydive
 * @param isLoading True if the map is loading data
 * @param points A list of jump datapoints to display
 */
@Composable
fun JumpMap(
    isLoading : Boolean,
    points : List<JumpDatapoint>
){
    val cameraPadding = 150
    val cameraPositionState : CameraPositionState = rememberCameraPositionState()
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        properties = MapProperties(
            isMyLocationEnabled = false,
            latLngBoundsForCameraTarget = points.bounds(),
            mapType = MapType.SATELLITE

        ),
        cameraPositionState = cameraPositionState,
        uiSettings = MapUiSettings(
            compassEnabled = true,
            rotationGesturesEnabled = true,
            scrollGesturesEnabled = true,
            zoomGesturesEnabled = true,
            tiltGesturesEnabled = true,
            zoomControlsEnabled = true,
            mapToolbarEnabled = false
        )
    ) {

        if(!isLoading){
            val walkingLine: MutableList<LatLng> = points.filterByPhase(JumpPhase.WALKING).sortedBy { it.timeStamp }. latLngList().toMutableList()
            val aircraftLine: MutableList<LatLng> = points.filterByPhase(JumpPhase.AIRCRAFT).latLngList().toMutableList()
            val freefallLine: MutableList<LatLng> = points.filterByPhase(JumpPhase.FREEFALL).latLngList().toMutableList()
            val canopyLine: MutableList<LatLng> = points.filterByPhase(JumpPhase.CANOPY).latLngList().toMutableList()
            val landedLine: MutableList<LatLng> = points.filterByPhase(JumpPhase.FREEFALL).latLngList().toMutableList()
            val unknownLine: MutableList<LatLng> = points.filterByPhase(JumpPhase.UNKNOWN).latLngList().toMutableList()


            if(landedLine.isNotEmpty() && aircraftLine.isNotEmpty() && freefallLine.isNotEmpty()){
                aircraftLine.firstOrNull()?.let { walkingLine.add(it) }
                freefallLine.firstOrNull()?.let { aircraftLine.add(it) }
                canopyLine.firstOrNull()?.let { freefallLine.add(it) }
                landedLine.firstOrNull()?.let { canopyLine.add(it) }
            }

            points.bounds()?.let { CameraUpdateFactory.newLatLngBounds(it,  cameraPadding) }
                ?.let { cameraPositionState.move(it) }


            CreatePolyLine(
                settings = PolylineSettings.LandedPolyLineSettings,
                points = landedLine
            )
            CreatePolyLine(
                settings = PolylineSettings.WalkingPolyLineSettings,
                points = walkingLine
            )
            CreatePolyLine(
                settings = PolylineSettings.FreefallPolyLineSettings,
                points = freefallLine
            )
            CreatePolyLine(
                settings = PolylineSettings.AircraftPolyLineSettings,
                points = aircraftLine
            )
            CreatePolyLine(
                settings = PolylineSettings.CanopyPolyLineSettings,
                points = canopyLine
            )
            CreatePolyLine(
                settings = PolylineSettings.CanopyPolyLineSettings,
                points = unknownLine
            )

        }


    }
}



/**
 * Creates a polyline that can be overlaid over a Google Map
 * @param points The LatLng points that make up the polyline
 * @param settings Additional polyline settings
 */
@Composable
private fun CreatePolyLine(settings : PolylineSettings, points : List<LatLng>) : Unit{
    var pCap : Cap = ButtCap()
    if(settings.cap != null){

        pCap = CustomCap(bitmapDescriptorFromVector(context = LocalContext.current, icon = settings.cap)!!,
            10.0F
        )
    }
    return Polyline(
        points = points,
        color = settings.color,
        width = settings.width,
        pattern = settings.pattern,
        endCap = pCap,
        jointType = JointType.ROUND
    )
}

/**
 * Settings for Polylines
 * @param color The color of the polyline
 * @param hex The hex code colour (for static maps)
 * @param width The width of the line
 * @param pattern The pattern of the line
 * @param cap A drawable resource to display at the end of the line
 */
sealed class PolylineSettings(
    val color: Color,
    val hex : String = "0xFF0000",
    val width : Float = 8f,
    val pattern : List<PatternItem>? = null,
    @DrawableRes val cap : Int? = null
) {
    object WalkingPolyLineSettings : PolylineSettings(WALKING_COLOR, hex = "0x808080FF" )
    object AircraftPolyLineSettings : PolylineSettings(AIRCRAFT_COLOR, cap = R.drawable.blue_plane, hex = "0xFF53E2F1")
    object FreefallPolyLineSettings : PolylineSettings(FREEFALL_COLOR, hex = "0xFF2644FF")
    object CanopyPolyLineSettings : PolylineSettings(CANOPY_COLOR, hex = "0xFFE64A19",  cap = ((R.drawable.parachute_cap)))
    object LandedPolyLineSettings : PolylineSettings(LANDED_COLOR, hex =  "0xFF000000")
}

@Composable
private fun bitmapDescriptorFromVector(context: Context, icon: Int): BitmapDescriptor? {
    return ContextCompat.getDrawable(context, icon)?.run {
        setBounds(0, 0, intrinsicWidth, intrinsicHeight)
        val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
        draw(android.graphics.Canvas(bitmap))
        BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}


