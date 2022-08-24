package com.skyblu.models.jump

import android.util.Log
import junit.framework.TestCase
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import timber.log.Timber

class JumpDatapointKtTest : TestCase() {


    @BeforeClass
    fun plant(){
        Timber.plant(Timber.DebugTree())
    }



    @AfterClass
    fun uproot(){
        Timber.uproot(Timber.DebugTree())
    }

    @Test
    fun toCsv() {

    }
}