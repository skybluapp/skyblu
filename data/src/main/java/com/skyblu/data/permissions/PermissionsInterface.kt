package com.skyblu.data.permissions

import android.app.Activity

/**
 * An interface for checking or and requesting permissions from the OS
 */
interface PermissionsInterface{
    fun checkPermissions() : Boolean
    fun requestPermissions(activity: Activity)
}