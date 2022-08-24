package com.skyblu.jumptracker

import android.app.Application
import timber.log.Timber

/**
 * Main application for Skybu
 */
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}