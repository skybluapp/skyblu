package com.skyblu.jumptracker.service

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationManager.GPS_PROVIDER
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.skyblu.configuration.hpaToMeters
import com.skyblu.configuration.playTone
import com.skyblu.data.datastore.DatastoreInterface
import com.skyblu.jumptracker.EmptyTrackingServiceCallbacks
import com.skyblu.jumptracker.MainApplication
import com.skyblu.jumptracker.TrackingServiceCallbacks
import com.skyblu.models.jump.JumpDatapoint
import com.skyblu.models.jump.JumpPhase
import com.skyblu.trackingservice.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.math.absoluteValue

/**
 * This servive for tracking jumps is quite complex and error prone. Use simple tracking service instead for now
 */

///**
// * A service that dispatches instances of SkydiveDataPoint whenever either altitude or GPS thresholds are met
// */
//@AndroidEntryPoint
//class JumpTrackingService : Service(), SensorEventListener {
//
//    private val binder = TrackingServiceBinder()
//
//    @Inject
//    lateinit var datastore: DatastoreInterface
//
//    /**
//     * By default noting occurs when service actions occur
//     */
//    private var trackingServiceCallbacks: TrackingServiceCallbacks = EmptyTrackingServiceCallbacks()
//
//    /**
//     * Binder is passed to client. Client calls provideCallbacks to inform the service what should be done when service actions occur.
//     */
//    inner class TrackingServiceBinder : Binder() {
//        /**
//         * Client calls provideCallbacks to set what should be done when service actions are triggered
//         * @param callbacks A class that implements TrackingServiceCallbacks functions, called when service actions are triggered
//         * @see TrackingServiceCallbacks
//         */
//        fun provideCallbacks(callbacks: TrackingServiceCallbacks) {
//            trackingServiceCallbacks = callbacks
//        }
//    }
//
//    /**
//     * Access sensor and location data
//     */
//    private lateinit var sensorManager: SensorManager
//    private lateinit var locationClient: LocationManager
//
//    //Holds values of last requested location & pressure
//    private var locationsReceived: Int = 0
//    private var pressuresRecieved: Int = 0
//    private var mostRecentLocation: Location? = null
//    private var mostRelevantLocation: Location? = null
//    private var mostRecentAltitude: Float? = null
//    private var mostRecentPressure: Float? = null
//    private var mostRelevantAltitude: Float? = null
//    private var altitudeUpdateTime: Long = System.currentTimeMillis()
//    var mostRecentVerticalSpeed: Float? = null
//    private var currentPhase = JumpPhase.WALKING
//    var currentPoint: JumpDatapoint? = null
//
//    //Holds starting values of altitude and pressure
//    var startAltitude: Float? = null
//    private var jumpID = UUID.randomUUID().toString()
//
//    //Holds the value of how frequently a new location is outputted
//    private var refreshRate: Long = 1000
//    fun setRefreshRate(refreshRate: Long) {
//        this.refreshRate = refreshRate
//    }
//
//    private var aircraftGroundspeedThreshold = 100
//
//    private var aircraftAltitudeThreshold = 100
//
//    private var freefallGroundspeedThreshold = 100
//
//    private var freefallVerticalSpeedThreshold = 100
//
//    private var canopyVerticalSpeedThreshold = 100
//
//    private var landedAltitudeThreshold = 100
//
//    private val job = SupervisorJob()
//    private val scope = CoroutineScope(Dispatchers.IO + job)
//
//
//    // Holds weather the output loop has been stopped
//    var isPaused: Boolean = false
//
//    //Runs when service is created (Instantiates sensorManager and location client)
//    override fun onCreate() {
//        super.onCreate()
//        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
//        locationClient = getSystemService(LOCATION_SERVICE) as LocationManager
//    }
//
//    override fun onStartCommand(
//        intent: Intent?,
//        flags: Int,
//        startId: Int
//    ): Int {
//        startForeground(
//            1,
//            createNotification()
//        )
//        return super.onStartCommand(
//            intent,
//            flags,
//            startId
//        )
//    }
//
//    //Runs once client is bound to service
//    override fun onBind(intent: Intent?): IBinder {
//        scope.launch {
//            datastore.readIntFromDatastore(
//                PreferenceKeys.FREEFALL_GROUNDSPEED_THRESHOLD,
//                defaultValue = 50
//            ) {
//                freefallGroundspeedThreshold = it
//            }
//        }
//
//        scope.launch {
//            datastore.readIntFromDatastore(
//                PreferenceKeys.AIRCRAFT_GROUNDSPEED_THRESHOLD,
//                defaultValue = 70
//            ) {
//                aircraftGroundspeedThreshold = it
//            }
//        }
//
//        scope.launch {
//            datastore.readIntFromDatastore(
//                PreferenceKeys.AIRCRAFT_ALTITUDE_THRESHOLD,
//                defaultValue = 300
//            ) {
//
//                aircraftAltitudeThreshold = it
//            }
//        }
//
//        scope.launch {
//            datastore.readIntFromDatastore(
//                PreferenceKeys.FREEFALL_VERTICALSPEED_THRESHOLD,
//                defaultValue = 50
//            ) {
//
//                freefallVerticalSpeedThreshold = it
//            }
//        }
//
//        scope.launch {
//            datastore.readIntFromDatastore(
//                PreferenceKeys.CANOPY_VERTICALSPEED_THRESHOLD,
//                defaultValue = 20
//            ) {
//                canopyVerticalSpeedThreshold = it
//            }
//        }
//
//        scope.launch {
//            datastore.readIntFromDatastore(
//                PreferenceKeys.LANDED_ALTITUDE_THRESHOLD,
//                defaultValue = 50
//            ) {
//
//                landedAltitudeThreshold = it
//            }
//        }
//
//        jumpID = UUID.randomUUID().toString()
//        requestLocationUpdates()
//        requestPressureUpdates()
//        isPaused = false
//        return binder
//    }
//
//    //Runs when a client unbinds
//    override fun onUnbind(intent: Intent?): Boolean {
//        isPaused = true
//        sensorManager.unregisterListener(this)
//       // locationClient.removeLocationUpdates(locationCallback)
//        stopSelf()
//        jumpID = UUID.randomUUID().toString()
//        return super.onUnbind(intent)
//    }
//
//    //Request to receive location updates
//    private fun requestLocationUpdates() {
//        if (ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED
//        ) {
//            val mLocationManager = getSystemService(LOCATION_SERVICE) as LocationManager
//            mLocationManager.requestLocationUpdates(GPS_PROVIDER, 1, 10f, p)
////            locationClient.requestLocationUpdates(
////                LocationRequest.create().setInterval(0)
////                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY),
////                locationCallback,
////                Looper.getMainLooper()
////            )
//        } else {
//            stopSelf()
//        }
//    }
//
//    //Request updates from the pressure sensor
//    private fun requestPressureUpdates() {
//        sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)?.also { pressure ->
//            sensorManager.registerListener(
//                this,
//                pressure,
//                SensorManager.SENSOR_DELAY_FASTEST
//            )
//        }
//    }
//
//    //Updates last location whenever a new location is recieved
//    private val locationCallback = object : LocationCallback() {
//        override fun onLocationResult(locationResult: LocationResult) {
//            super.onLocationResult(locationResult)
//
//            locationsReceived++
//            if (mostRecentLocation == null || mostRelevantLocation == null) {
//                mostRecentLocation = locationResult.lastLocation
//                mostRelevantLocation = locationResult.lastLocation
//            }
//            val newLocation = locationResult.lastLocation
//            mostRecentLocation = newLocation
//            if (mostRecentLocation!!.distanceTo(mostRelevantLocation) > LOCATION_THRESHOLD_INT || locationsReceived == 1) {
//                mostRelevantLocation = newLocation
//
//                dispatch()
//            }
//        }
//    }
//
//    val p : LocationListener = LocationListener{
//        Timber.d("Location!")
//    }
//
//    //Updates last pressure whenever a new pressure is received
//    override fun onSensorChanged(event: SensorEvent?) {
//        if (event?.sensor == sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)) {
//            val newPressure = event?.values?.get(0)!!
//            val newAltitude = newPressure.hpaToMeters()
//            val currentTime = System.currentTimeMillis()
//
//            pressuresRecieved++
//            calculateVerticalSpeed(newAltitude)
//
//
//            if (startAltitude == null || mostRecentAltitude == null || mostRelevantAltitude == null) {
//                startAltitude = newAltitude
//                mostRelevantAltitude = newAltitude
//                mostRecentAltitude = newAltitude
//                altitudeUpdateTime = currentTime
//                mostRecentVerticalSpeed = 0f
//                dispatch()
//                return
//            }
//
//            if ((mostRelevantAltitude!! - newAltitude).absoluteValue > ALTITUDE_THRESHOLD_INT && currentTime > altitudeUpdateTime + 2000) {
//
//
//                altitudeUpdateTime = currentTime
//                mostRelevantAltitude = newAltitude
//
//
//                dispatch()
//            }
//
//            mostRecentAltitude = newAltitude
//            mostRecentPressure = newPressure
//        }
//    }
//
//    fun dispatch() {
//        if (mostRecentLocation == null) {
//
//            return
//        }
//        if (mostRecentAltitude == null) {
//
//            return
//        }
//        if (mostRecentPressure == null) {
//
//            return
//        }
//
//        if (currentPoint == null) {
//            currentPoint = createMostRecentDataPoint(phase = JumpPhase.WALKING)
//            trackingServiceCallbacks.postSkydiveDataPoint(currentPoint!!)
//            return
//        }
//        val newPoint = createMostRecentDataPoint()
//        when (currentPoint!!.phase) {
//            JumpPhase.WALKING -> {
//
//                if (newPoint.groundSpeed > aircraftGroundspeedThreshold && newPoint.altitude > aircraftAltitudeThreshold) {
//                    playTone()
//                    currentPoint!!.phase = JumpPhase.AIRCRAFT
//                    trackingServiceCallbacks.postSkydiveDataPoint(currentPoint!!)
//                    newPoint.phase = JumpPhase.AIRCRAFT
//                    currentPoint = newPoint
//                    trackingServiceCallbacks.postSkydiveDataPoint(newPoint)
//                } else {
//                    newPoint.phase = JumpPhase.WALKING
//                    currentPoint = newPoint
//                    trackingServiceCallbacks.postSkydiveDataPoint(newPoint)
//                }
//            }
//            JumpPhase.AIRCRAFT -> {
//
//                if (newPoint.verticalSpeed < -freefallVerticalSpeedThreshold && newPoint.groundSpeed < freefallGroundspeedThreshold) {
//                    playTone()
//                    currentPoint!!.phase = JumpPhase.FREEFALL
//                    trackingServiceCallbacks.postSkydiveDataPoint(currentPoint!!)
//                    newPoint.phase = JumpPhase.FREEFALL
//                    currentPoint = newPoint
//                    trackingServiceCallbacks.postSkydiveDataPoint(newPoint)
//                } else {
//                    newPoint.phase = JumpPhase.AIRCRAFT
//                    currentPoint = newPoint
//                    trackingServiceCallbacks.postSkydiveDataPoint(newPoint)
//                }
//            }
//            JumpPhase.FREEFALL -> {
//
//                if (newPoint.verticalSpeed > -canopyVerticalSpeedThreshold) {
//                    playTone()
//                    currentPoint!!.phase = JumpPhase.CANOPY
//                    trackingServiceCallbacks.postSkydiveDataPoint(currentPoint!!)
//                    newPoint.phase = JumpPhase.CANOPY
//                    currentPoint = newPoint
//                    trackingServiceCallbacks.postSkydiveDataPoint(newPoint)
//                } else {
//                    newPoint.phase = JumpPhase.FREEFALL
//                    currentPoint = newPoint
//                    trackingServiceCallbacks.postSkydiveDataPoint(newPoint)
//                }
//            }
//            JumpPhase.CANOPY -> {
//
//                if (newPoint.altitude < landedAltitudeThreshold) {
//                    playTone()
//                    currentPoint!!.phase = JumpPhase.LANDED
//                    trackingServiceCallbacks.postSkydiveDataPoint(currentPoint!!)
//                    newPoint.phase = JumpPhase.LANDED
//                    currentPoint = newPoint
//                    trackingServiceCallbacks.postSkydiveDataPoint(newPoint)
//                } else {
//                    newPoint.phase = JumpPhase.CANOPY
//                    currentPoint = newPoint
//                    trackingServiceCallbacks.postSkydiveDataPoint(newPoint)
//                }
//            }
//            JumpPhase.LANDED -> {
//
//                newPoint.phase = JumpPhase.LANDED
//                currentPoint = newPoint
//                trackingServiceCallbacks.postSkydiveDataPoint(newPoint)
//            }
//        }
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//
//        jumpID = UUID.randomUUID().toString()
//        stopSelf()
//    }
//
//    fun createMostRecentDataPoint(phase: JumpPhase = JumpPhase.UNKNOWN): JumpDatapoint {
//        return JumpDatapoint(
//            dataPointID = UUID.randomUUID().toString(),
//            jumpID = jumpID,
//            latitude = mostRecentLocation!!.latitude,
//            longitude = mostRecentLocation!!.longitude,
//            timeStamp = mostRecentLocation!!.time,
//            altitude = mostRecentAltitude!! - startAltitude!!,
//            verticalSpeed = speed,
//            groundSpeed = mostRecentLocation!!.speed,
//            airPressure = mostRecentPressure!!,
//            phase = phase
//        )
//    }
//
//    // Creates notification required for foreground service
//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun createNotificationChannel(
//        channelId: String,
//        channelName: String
//    ): String {
//        val chan = NotificationChannel(
//            channelId,
//            channelName,
//            NotificationManager.IMPORTANCE_NONE
//        )
//        chan.lightColor = Color.BLUE
//        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
//        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        service.createNotificationChannel(chan)
//        return channelId
//    }
//
//    private fun createNotification(): Notification {
//        val channelId = createNotificationChannel(
//            "TRACKING_SERVICE",
//            "My Background Service"
//        )
//        val pendingIntent: PendingIntent =
//            Intent(
//                this,
//                MainApplication::class.java
//            ).let { notificationIntent ->
//                PendingIntent.getActivity(
//                    this,
//                    0,
//                    notificationIntent,
//                    PendingIntent.FLAG_IMMUTABLE
//                )
//            }
//        return Notification.Builder(
//            this,
//            "TRACKING_SERVICE"
//        )
//            .setContentTitle("Tracking Service")
//            .setContentText("Your jump is being tracked")
//            .setSmallIcon(R.drawable.tracking)
//            .setContentIntent(pendingIntent)
//            .setTicker("Tracking...")
//            .build()
//    }
//
//
//
//    var lastSpeedCheck: Long = System.currentTimeMillis()
//    var altitude1: Float? = null
//    var speed: Float = 0F
//    private fun calculateVerticalSpeed(altitude2: Float) {
//        if (altitude1 == null) {
//            altitude1 = altitude2
//            return
//        }
//        val currentTime = System.currentTimeMillis()
//        if (currentTime > lastSpeedCheck + 5000) {
//            val speedo = (altitude2 - altitude1!!) / 5
//
//            speed = speedo
//            lastSpeedCheck = currentTime
//            altitude1 = altitude2
//        }
//
//    }
//
//    override fun onAccuracyChanged(
//        sensor: Sensor?,
//        accuracy: Int
//    ) {
//    }
//}
//


