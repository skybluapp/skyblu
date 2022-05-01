package com.skyblu.jumptracker

import com.skyblu.models.jump.JumpDatapoint

interface TrackingServiceCallbacks {
    fun postSkydiveDataPoint(dataPoint : JumpDatapoint)
    fun pressureSensorUnavailable()
    fun locationUnavailable()
}

class EmptyTrackingServiceCallbacks : TrackingServiceCallbacks{

    override fun postSkydiveDataPoint(dataPoint: JumpDatapoint) {

    }

    override fun pressureSensorUnavailable() {

    }
    override fun locationUnavailable() {

    }
}