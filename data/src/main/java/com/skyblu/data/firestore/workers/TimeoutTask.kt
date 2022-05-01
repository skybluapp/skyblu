package com.skyblu.data.firestore.workers

import androidx.work.ListenableWorker
import com.google.android.gms.tasks.Task
import com.skyblu.configuration.TIMEOUT_MILLIS

import kotlinx.coroutines.delay
import timber.log.Timber

/**
 * Runs a task for a set period of time.
 * Task will fail if it is not complete within a set period of time
 * @param task The task to execute
 * @return result from the task, or failure the task fails to complete in time
 */
suspend fun <T>timeoutTask(task : (onSuccess : () -> Unit, onFail : () -> Unit) -> Task<T>) : ListenableWorker.Result{
    val startTime = System.currentTimeMillis()
    var result: ListenableWorker.Result? = null
    task(
        {result = ListenableWorker.Result.success()},
        {result = ListenableWorker.Result.failure()},
    )
    while (result == null) {
        if (System.currentTimeMillis() - TIMEOUT_MILLIS > startTime) {
            return ListenableWorker.Result.failure()
        }
        delay(1000)
    }
    return result as ListenableWorker.Result
}