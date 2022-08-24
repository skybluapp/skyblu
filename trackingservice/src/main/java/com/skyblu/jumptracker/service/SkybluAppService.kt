package com.skyblu.jumptracker.service

import android.Manifest
import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat.startForegroundService
import com.skyblu.jumptracker.TrackingServiceCallbacks
import com.skyblu.models.jump.JumpDatapoint
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.PermissionRequest
import timber.log.Timber
import javax.inject.Inject

/**
 * Singleton that manages the flow of data between the service and the activity
 */
class SkybluAppService @Inject constructor(
    private val context: Context,
) : ClientToService {

    lateinit var trackingService: SimpleTrackingService
    var isTrackingServiceBound: Boolean = false
    private var onRecieveTrackingPoint: (JumpDatapoint) -> Unit = {}

    /**
     * If set, service calls these functions when thresholds or criteria are met
     */
    private inner class Callbacks : TrackingServiceCallbacks {


        override fun postSkydiveDataPoint(dataPoint: JumpDatapoint) {
            onRecieveTrackingPoint(dataPoint)
        }

        override fun pressureSensorUnavailable() {
            Toast.makeText(
                context,
                "Pressure Sensor Unavailabe",
                Toast.LENGTH_SHORT
            ).show()
        }

        override fun locationUnavailable() {
            Toast.makeText(
                context,
                "Location Access Denied",
                Toast.LENGTH_SHORT
            ).show()
        }


    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(
            name: ComponentName?,
            service: IBinder?
        ) {
            val binder = service as SimpleTrackingService.TrackingServiceBinder
            binder.provideCallbacks(Callbacks())
            isTrackingServiceBound = true

        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isTrackingServiceBound = false
        }
    }

    override fun setRefreshRate(refreshRate: Int) {
        if (isTrackingServiceBound) {

            //trackingService.setRefreshRate(refreshRate = refreshRate.toLong())
        }
    }
    override fun getGroundAltitude(): Float? {

        return trackingService.startAltitude
    }


    override fun startTrackingService(
    ) {
        if (EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            if (!isTrackingServiceBound) {



                Intent(
                    context,
                    SimpleTrackingService::class.java
                )
                    .also { intent ->
                    startForegroundService(
                        context,
                        intent
                    )

                    (context as Application).bindService(
                        intent,
                        connection,
                        Context.BIND_AUTO_CREATE
                    )
                }

            }
        } else {
            EasyPermissions.requestPermissions(
                PermissionRequest.Builder(
                    context as ComponentActivity,
                    1,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                    .setRationale("Location permission is required to track location during a jump")
                    .build(),
            )
        }
    }

    override fun stopTrackingService() {
        if (isTrackingServiceBound) {

            (context as Application).unbindService(connection)
            isTrackingServiceBound = false
        }
    }

    override fun setOnRecieveSkydiveDataPoint(function: (JumpDatapoint) -> Unit) {
        onRecieveTrackingPoint = function
    }
}

interface ClientToService {
    fun setOnRecieveSkydiveDataPoint(function: (JumpDatapoint) -> Unit)
    fun stopTrackingService()
    fun startTrackingService()
    fun setRefreshRate(refreshRate: Int)
    fun getGroundAltitude() : Float?
}
