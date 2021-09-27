package cn.cangjie.uikit.scanner.camera

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import cn.cangjie.uikit.scanner.Preferences

enum class FrontLightMode {
    /** Always on.  */
    ON,

    /** On only when ambient light is low.  */
    AUTO,

    /** Always off.  */
    OFF;

    companion object {
        private fun parse(modeString: String?): FrontLightMode {
            return modeString?.let { valueOf(it) } ?: AUTO
        }

        fun readPref(sharedPrefs: SharedPreferences): FrontLightMode {
            return parse(sharedPrefs.getString(Preferences.KEY_FRONT_LIGHT_MODE, AUTO.toString()))
        }

        fun put(context: Context?, mode: FrontLightMode) {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            prefs.edit().putString(Preferences.KEY_FRONT_LIGHT_MODE, mode.toString()).commit()
        }
    }
}