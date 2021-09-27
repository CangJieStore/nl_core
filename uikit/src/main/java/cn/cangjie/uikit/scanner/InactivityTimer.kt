package cn.cangjie.uikit.scanner

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.AsyncTask
import android.os.BatteryManager
import java.lang.ref.WeakReference
import java.util.concurrent.RejectedExecutionException

class InactivityTimer(private val activity: Activity) {
    private val powerStatusReceiver: BroadcastReceiver
    private var registered: Boolean
    private var inactivityTask: AsyncTask<Any?, Any?, Any?>? = null
    fun onActivity() {
        cancel()
        inactivityTask = InactivityAsyncTask(activity)
        try {
            inactivityTask?.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        } catch (ree: RejectedExecutionException) {
        }
    }

    fun onPause() {
        cancel()
        if (registered) {
            activity.unregisterReceiver(powerStatusReceiver)
            registered = false
        } else {
        }
    }

    fun onResume() {
        if (registered) {
        } else {
            activity.registerReceiver(powerStatusReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            registered = true
        }
        onActivity()
    }

    private fun cancel() {
        val task: AsyncTask<*, *, *>? = inactivityTask
        if (task != null) {
            task.cancel(true)
            inactivityTask = null
        }
    }

    fun shutdown() {
        cancel()
    }

    private class PowerStatusReceiver(inactivityTimer: InactivityTimer) : BroadcastReceiver() {
        private val weakReference: WeakReference<InactivityTimer>
        override fun onReceive(context: Context, intent: Intent) {
            if (Intent.ACTION_BATTERY_CHANGED == intent.action) {
                // 0 indicates that we're on battery
                val inactivityTimer = weakReference.get()
                if (inactivityTimer != null) {
                    val onBatteryNow = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) <= 0
                    if (onBatteryNow) {
                        inactivityTimer.onActivity()
                    } else {
                        inactivityTimer.cancel()
                    }
                }
            }
        }

        init {
            weakReference = WeakReference(inactivityTimer)
        }
    }

    private class InactivityAsyncTask(activity: Activity) : AsyncTask<Any?, Any?, Any?>() {
        private val weakReference: WeakReference<Activity> = WeakReference(activity)
        override fun doInBackground(vararg p0: Any?): Any? {
            try {
                Thread.sleep(INACTIVITY_DELAY_MS)
                val activity = weakReference.get()
                activity?.finish()
            } catch (e: InterruptedException) {
                // continue without killing
            }
            return null
        }

    }

    companion object {
        private const val INACTIVITY_DELAY_MS = 5 * 60 * 1000L
    }

    init {
        powerStatusReceiver = PowerStatusReceiver(this)
        registered = false
        onActivity()
    }
}