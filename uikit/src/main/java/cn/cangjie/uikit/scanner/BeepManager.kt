package cn.cangjie.uikit.scanner

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Vibrator
import android.preference.PreferenceManager
import cn.cangjie.uikit.R
import java.io.Closeable
import java.io.IOException

class BeepManager internal constructor(private val activity: Activity) : MediaPlayer.OnErrorListener, Closeable {
    private var mediaPlayer: MediaPlayer? = null
    private var playBeep = false
    private var vibrate = false
    fun setVibrate(vibrate: Boolean) {
        this.vibrate = vibrate
    }

    fun setPlayBeep(playBeep: Boolean) {
        this.playBeep = playBeep
    }

    @Synchronized
    fun updatePrefs() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
        shouldBeep(prefs, activity)
        if (playBeep && mediaPlayer == null) {
            activity.volumeControlStream = AudioManager.STREAM_MUSIC
            mediaPlayer = buildMediaPlayer(activity)
        }
    }

    @Synchronized
    fun playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer!!.start()
        }
        if (vibrate) {
            val vibrator = activity.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(VIBRATE_DURATION)
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private fun buildMediaPlayer(activity: Context): MediaPlayer? {
        val mediaPlayer = MediaPlayer()
        try {
            activity.resources.openRawResourceFd(R.raw.zxl_beep).use { file ->
                mediaPlayer.setDataSource(file.fileDescriptor, file.startOffset, file.length)
                mediaPlayer.setOnErrorListener(this)
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
                mediaPlayer.isLooping = false
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME)
                mediaPlayer.prepare()
                return mediaPlayer
            }
        } catch (ioe: IOException) {
            mediaPlayer.release()
            return null
        }
    }

    @Synchronized
    override fun onError(mp: MediaPlayer, what: Int, extra: Int): Boolean {
        if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
            activity.finish()
        } else {
            close()
            updatePrefs()
        }
        return true
    }

    @Synchronized
    override fun close() {
        if (mediaPlayer != null) {
            mediaPlayer!!.release()
            mediaPlayer = null
        }
    }

    companion object {
        private const val BEEP_VOLUME = 0.10f
        private const val VIBRATE_DURATION = 200L
        private fun shouldBeep(prefs: SharedPreferences, activity: Context): Boolean {
            var shouldPlayBeep = prefs.getBoolean(Preferences.KEY_PLAY_BEEP, false)
            if (shouldPlayBeep) {
                // See if sound settings overrides this
                val audioService = activity.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                if (audioService.ringerMode != AudioManager.RINGER_MODE_NORMAL) {
                    shouldPlayBeep = false
                }
            }
            return shouldPlayBeep
        }
    }

    init {
        updatePrefs()
    }
}