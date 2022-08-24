package com.skyblu.data.workManager

import androidx.work.WorkManager

/**
 * An interface that provides an instance of the devices background work management system
 * @property getWorkManager provides an instance of the work management system
 */
interface WorkManagerInterface{
    fun getWorkManager() : WorkManager
}