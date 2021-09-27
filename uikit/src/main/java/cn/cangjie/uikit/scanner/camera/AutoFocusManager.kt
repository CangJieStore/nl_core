package cn.cangjie.uikit.scanner.camera

import android.content.Context
import android.hardware.Camera
import android.hardware.Camera.AutoFocusCallback
import android.os.AsyncTask
import android.preference.PreferenceManager
import cn.cangjie.uikit.scanner.Preferences
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.RejectedExecutionException

internal  // camera APIs
class AutoFocusManager(context: Context?, private val camera: Camera) : AutoFocusCallback {
    companion object {
        private const val AUTO_FOCUS_INTERVAL_MS = 1200L
        private var FOCUS_MODES_CALLING_AF: MutableCollection<String>? = null

        init {
            FOCUS_MODES_CALLING_AF = ArrayList(2)
            FOCUS_MODES_CALLING_AF?.add(Camera.Parameters.FOCUS_MODE_AUTO)
            FOCUS_MODES_CALLING_AF?.add(Camera.Parameters.FOCUS_MODE_MACRO)
        }
    }

    private var stopped = false
    private var focusing = false
    private val useAutoFocus: Boolean
    private var outstandingTask: AsyncTask<*, *, *>? = null

    @Synchronized
    override fun onAutoFocus(success: Boolean, theCamera: Camera) {
        focusing = false
        autoFocusAgainLater()
    }

    @Synchronized
    private fun autoFocusAgainLater() {
        if (!stopped && outstandingTask == null) {
            val newTask = AutoFocusTask(this)
            try {
                newTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
                outstandingTask = newTask
            } catch (ree: RejectedExecutionException) {
            }
        }
    }

    @Synchronized
    fun start() {
        if (useAutoFocus) {
            outstandingTask = null
            if (!stopped && !focusing) {
                try {
                    camera.autoFocus(this)
                    focusing = true
                } catch (re: RuntimeException) {
                    // Have heard RuntimeException reported in Android 4.0.x+; continue?
                    // Try again later to keep cycle going
                    autoFocusAgainLater()
                }
            }
        }
    }

    @Synchronized
    private fun cancelOutstandingTask() {
        if (outstandingTask != null) {
            if (outstandingTask!!.status != AsyncTask.Status.FINISHED) {
                outstandingTask!!.cancel(true)
            }
            outstandingTask = null
        }
    }

    @Synchronized
    fun stop() {
        stopped = true
        if (useAutoFocus) {
            cancelOutstandingTask()
            // Doesn't hurt to call this even if not focusing
            try {
                camera.cancelAutoFocus()
            } catch (re: RuntimeException) {
                // Have heard RuntimeException reported in Android 4.0.x+; continue?
            }
        }
    }

    private class AutoFocusTask(manager: AutoFocusManager) : AsyncTask<Any?, Any?, Any?>() {
        private val weakReference: WeakReference<AutoFocusManager> = WeakReference(manager)
        override fun doInBackground(vararg p0: Any?): Any? {
            try {
                Thread.sleep(AUTO_FOCUS_INTERVAL_MS)
            } catch (e: InterruptedException) {
                // continue
            }
            val manager = weakReference.get()
            manager?.start()
            return null
        }

    }

    init {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        val currentFocusMode = camera.parameters.focusMode
        useAutoFocus = sharedPrefs.getBoolean(Preferences.KEY_AUTO_FOCUS, true) &&
                FOCUS_MODES_CALLING_AF!!.contains(currentFocusMode)
        start()
    }
}