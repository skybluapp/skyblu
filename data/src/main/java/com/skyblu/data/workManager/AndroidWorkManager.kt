package com.skyblu.data.workManager

import android.content.Context
import androidx.work.WorkManager

class AndroidWorkManager(val context : Context) : WorkManagerInterface {
    /**
     * Provides an instance of Android Work Manager
     * @property getWorkManager provides an instance of Android Work Manager
     */
    override fun getWorkManager() : WorkManager {
        return androidx.work.WorkManager.getInstance(context)
    }
}
