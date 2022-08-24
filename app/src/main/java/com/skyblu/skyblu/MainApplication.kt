package com.skyblu.skyblu

import android.app.Application
import timber.log.Timber

/**
 * The main (and only) activity for the application.
 */
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}