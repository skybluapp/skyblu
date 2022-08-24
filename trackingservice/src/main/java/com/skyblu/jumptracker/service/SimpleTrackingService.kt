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
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
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
import java.lang.Exception
import java.util.*
import javax.inject.Inject
import kotlin.math.absoluteValue

/**
 * A service that dispatches instances of SkydiveDataPoint whenever either altitude or GPS thresholds are met
 */
@AndroidEntryPoint
class SimpleTrackingService : Service(), SensorEventListener {

    private val binder = TrackingServiceBinder()

    @Inject
    lateinit var datastore: DatastoreInterface

    /**
     * By default noting occurs when service actions occur
     */
    private var trackingServiceCallbacks: TrackingServiceCallbacks = EmptyTrackingServiceCallbacks()

    /**
     * Binder is passed to client. Client calls provideCallbacks to inform the service what should be done when service actions occur.
     */
    inner class TrackingServiceBinder : Binder() {
        /**
         * Client calls provideCallbacks to set what should be done when service actions are triggered
         * @param callbacks A class that implements TrackingServiceCallbacks functions, called when service actions are triggered
         * @see TrackingServiceCallbacks
         */
        fun provideCallbacks(callbacks: TrackingServiceCallbacks) {
            trackingServiceCallbacks = callbacks
        }
    }

    /**
     * Access sensor and location data
     */
    private lateinit var sensorManager: SensorManager
    private lateinit var locationClient: LocationManager


    //Holds starting values of altitude and pressure
    var startAltitude: Float? = null
    private var jumpID = UUID.randomUUID().toString()


    // Holds weather the output loop has been stopped
    var isPaused: Boolean = false
    var lastTime: Long = 0

    //Runs when service is created (Instantiates sensorManager and location client)
    override fun onCreate() {
        super.onCreate()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        locationClient =  getSystemService(LOCATION_SERVICE) as LocationManager
    }

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        startForeground(
            1,
            createNotification()
        )

        scope.launch {
            while(true){
                val firstAlt = altitude
                delay(5000)
                val secondAlt = altitude
                speed =  measureSpeed(firstAlt, secondAlt)
                dispatch()
            }
        }

        return super.onStartCommand(
            intent,
            flags,
            startId
        )


    }

    private fun measureSpeed(
        firstAlt: Float?,
        secondAlt: Float?
    ): Float {
        if(firstAlt == null || secondAlt == null){
            return 0f
        }
        val heightChange = secondAlt - firstAlt
        return heightChange / 5

    }

    //Runs once client is bound to service
    override fun onBind(intent: Intent?): IBinder {
        jumpID = UUID.randomUUID().toString()
        requestLocationUpdates()
        requestPressureUpdates()
        isPaused = false
        return binder
    }

    //Runs when a client unbinds
    override fun onUnbind(intent: Intent?): Boolean {
        isPaused = true
        sensorManager.unregisterListener(this)
        //locationClient.removeLocationUpdates(locationCallback)
        stopSelf()
        jumpID = UUID.randomUUID().toString()
        return super.onUnbind(intent)
    }

    //Request to receive location updates
    private fun requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Timber.d("Location permission granted")
//            locationClient.requestLocationUpdates(
//                LocationRequest.create().setInterval(0)
//                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY),
//                locationCallback,
//                Looper.getMainLooper()
            locationClient.requestLocationUpdates(GPS_PROVIDER, 1, 10f, p)

        } else {
            Timber.d("Location permission denied")
            stopSelf()
        }
    }

    //Request updates from the pressure sensor
    private fun requestPressureUpdates() {
        sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)?.also { pressure ->
            sensorManager.registerListener(
                this,
                pressure,
                SensorManager.SENSOR_DELAY_FASTEST
            )
        }
    }

    var location : Location? = null
    var pressure : Float? = null
    var altitude : Float? = null
    private var speed : Float? = null

    //Updates last location whenever a new location is recieved
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            location = locationResult.lastLocation

        }
    }

    val p : android.location.LocationListener = LocationListener{
        Timber.d("Location!")
        location = it
    }

    //Updates last pressure whenever a new pressure is received
    override fun onSensorChanged(event: SensorEvent?) {
        when(event?.sensor){
            sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE) -> {
                pressure = event?.values?.get(0)
                altitude = pressure?.hpaToMeters()
            }
        }
    }

    private fun dispatch() {
        val datapoint = createMostRecentDataPoint()
        if(datapoint != null){
            trackingServiceCallbacks.postSkydiveDataPoint(datapoint)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        jumpID = UUID.randomUUID().toString()
        stopSelf()
    }

    fun createMostRecentDataPoint(phase: JumpPhase = JumpPhase.UNKNOWN): JumpDatapoint? {
        try {
            val jump = JumpDatapoint(
                dataPointID = UUID.randomUUID().toString(),
                jumpID = jumpID,
                latitude = location?.latitude!!,
                longitude = location?.longitude!!,
                timeStamp = System.currentTimeMillis(),
                altitude = altitude!!,
                verticalSpeed = speed!!,
                groundSpeed = location!!.speed,
                airPressure = pressure!!,
                phase = phase
            )
            Timber.d("Good datapoint $jump")
            return jump
        } catch (e : Exception){
            Timber.d("Null datapoint vSpeed: $speed, alt: $altitude, gSpeed: ${location?.speed}, lon: ${location?.longitude}, lat: ${location?.latitude}")
            return null
        }
    }

    // Creates notification required for foreground service
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(
        channelId: String,
        channelName: String
    ): String {
        val chan = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_NONE
        )
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }

    private fun createNotification(): Notification {
        val channelId = createNotificationChannel(
            "TRACKING_SERVICE",
            "My Background Service"
        )
        val pendingIntent: PendingIntent =
            Intent(
                this,
                MainApplication::class.java
            ).let { notificationIntent ->
                PendingIntent.getActivity(
                    this,
                    0,
                    notificationIntent,
                    PendingIntent.FLAG_IMMUTABLE
                )
            }
        return Notification.Builder(
            this,
            "TRACKING_SERVICE"
        )
            .setContentTitle("Tracking Service")
            .setContentText("Your jump is being tracked")
            .setSmallIcon(R.drawable.tracking)
            .setContentIntent(pendingIntent)
            .setTicker("Tracking...")
            .build()
    }


    override fun onAccuracyChanged(
        sensor: Sensor?,
        accuracy: Int
    ) {
    }
}



