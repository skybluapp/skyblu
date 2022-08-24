package com.skyblu.userinterface.componants.maps

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.skyblu.models.jump.*
import com.skyblu.userinterface.BuildConfig


/**
 * A composable static Google Map
 * @param skydive The jump to display
 * @param trackingData The datapoints to display
 * @param onClick An action to take when the map is clicked
 */
@Composable
fun StaticGoogleMap(
    skydive: Jump,
    //trackingData: JumpTrackingData,
    onClick: () -> Unit = {}
) {


    Box(modifier = Modifier
        .background(Color.DarkGray)
        .height(350.dp)
        .fillMaxWidth()) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(skydive.staticMapUrl.replace("terrain", "satellite"))
                .crossfade(true)
                .build(),
            contentDescription = "barcode image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clickable { onClick() },
        )
    }
}

/**
 * Generates a static google map url from a list of datapoints
 * @param points The datapoints to generate a map for
 * @param baseUrl The base URL for the Google Maps Static Map API
 * @param key A Google Maps API Key
 */
fun generateStaticMapsUrl(
    points : List<JumpDatapoint>,
    baseUrl : String = "https://maps.googleapis.com/maps/api/staticmap?",
    key : String = BuildConfig.GOOGLE_MAPS_API_KEY
) : String{

    val sizeUrl = "&size=1000x1000"
    val mapTypeUrl = "&maptype=terrain"
    val keyUrl =  "&key=${BuildConfig.GOOGLE_MAPS_API_KEY}"
    return  baseUrl  + sizeUrl  + mapTypeUrl +
            pathToString(trackingPoints = points.filterByPhase(JumpPhase.UNKNOWN), PolylineSettings.AircraftPolyLineSettings) +
            pathToString(trackingPoints = points.filterByPhase(JumpPhase.WALKING), PolylineSettings.WalkingPolyLineSettings) +
            pathToString(trackingPoints = points.filterByPhase(JumpPhase.AIRCRAFT), PolylineSettings.AircraftPolyLineSettings) +
            pathToString(trackingPoints = points.filterByPhase(JumpPhase.FREEFALL), PolylineSettings.FreefallPolyLineSettings) +
            pathToString(trackingPoints = points.filterByPhase(JumpPhase.CANOPY), PolylineSettings.CanopyPolyLineSettings) +
            pathToString(trackingPoints = points.filterByPhase(JumpPhase.LANDED), PolylineSettings.LandedPolyLineSettings) +
            keyUrl
}

fun pathToString(trackingPoints : List<JumpDatapoint>, settings : PolylineSettings) : String {
    val stringPrepend = "&path=color:${settings.hex}|weight:5"
    var stringAppend = ""
    for (trackingPoint in trackingPoints) {
        val lat = trackingPoint.latitude
        val long = trackingPoint.longitude
        stringAppend += "|${lat},${long}"
    }
    return  stringPrepend + stringAppend
}