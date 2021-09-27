package cn.cangjie.uikit.scanner

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.preference.PreferenceManager
import cn.cangjie.uikit.scanner.camera.CameraManager
import cn.cangjie.uikit.scanner.camera.FrontLightMode

class AmbientLightManager(private val context: Context) : SensorEventListener {
    /**
     * 光线太暗时，默认：照度45 lux
     */
    private var tooDarkLux = TOO_DARK_LUX

    /**
     * 光线足够亮时，默认：照度450 lux
     */
    private var brightEnoughLux = BRIGHT_ENOUGH_LUX
    private var cameraManager: CameraManager? = null
    private var lightSensor: Sensor? = null
    fun start(cameraManager: CameraManager?) {
        this.cameraManager = cameraManager
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        if (FrontLightMode.readPref(sharedPrefs) == FrontLightMode.AUTO) {
            val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
            lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
            if (lightSensor != null) {
                sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
            }
        }
    }

    fun stop() {
        if (lightSensor != null) {
            val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
            sensorManager.unregisterListener(this)
            cameraManager = null
            lightSensor = null
        }
    }

    override fun onSensorChanged(sensorEvent: SensorEvent) {
        val ambientLightLux = sensorEvent.values[0]
        if (cameraManager != null) {
            if (ambientLightLux <= tooDarkLux) {
                cameraManager!!.sensorChanged(true, ambientLightLux)
            } else if (ambientLightLux >= brightEnoughLux) {
                cameraManager!!.sensorChanged(false, ambientLightLux)
            }
        }
    }

    fun setTooDarkLux(tooDarkLux: Float) {
        this.tooDarkLux = tooDarkLux
    }

    fun setBrightEnoughLux(brightEnoughLux: Float) {
        this.brightEnoughLux = brightEnoughLux
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // do nothing
    }

    companion object {
        const val TOO_DARK_LUX = 45.0f
        const val BRIGHT_ENOUGH_LUX = 100.0f
    }

}