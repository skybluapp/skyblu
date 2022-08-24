package com.skyblu.configuration

import android.util.Log
import org.junit.Assert.*
import org.junit.BeforeClass

import org.junit.Test

class StringsKtTest {



    @Test
    fun toCsv() {
        var testString = "JumpDatapoint(dataPointID=81e2ac72-02a9-4927-9e00-0049d97f1d5d, jumpID=78154bec-bbb7-4b75-89de-1d2d84b0af0d, latitude=52.77591, longitude=-1.07134, airPressure=1013.25, altitude=0.0, timeStamp=1659367925467, verticalSpeed=0.0, groundSpeed=4.297508, phase=WALKING)\nJumpDatapoint(dataPointID=81e2ac72-02a9-4927-9e00-0049d97f1d5d, jumpID=78154bec-bbb7-4b75-89de-1d2d84b0af0d, latitude=52.77591, longitude=-1.07134, airPressure=1013.25, altitude=0.0, timeStamp=1659367925467, verticalSpeed=0.0, groundSpeed=4.297508, phase=WALKING)"
        testString = testString.toCsv()
        assertEquals("dataPointID=81e2ac72-02a9-4927-9e00-0049d97f1d5d, jumpID=78154bec-bbb7-4b75-89de-1d2d84b0af0d, latitude=52.77591, longitude=-1.07134, airPressure=1013.25, altitude=0.0, timeStamp=1659367925467, verticalSpeed=0.0, groundSpeed=4.297508, phase=WALKING", testString )

    }
}