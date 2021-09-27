package cn.cangjie.uikit.scanner

interface CaptureLifecycle {
    /**
     * [Activity.onCreate]
     */
    fun onCreate()

    /**
     * [Activity.onResume]
     */
    fun onResume()

    /**
     * [Activity.onPause]
     */
    fun onPause()

    /**
     * [Activity.onDestroy]
     */
    fun onDestroy()
}