package cn.cangjie.uikit.scanner

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import com.google.zxing.BarcodeFormat
import com.google.zxing.DecodeHintType
import com.google.zxing.ResultPointCallback
import cn.cangjie.uikit.scanner.camera.CameraManager
import java.util.*
import java.util.concurrent.CountDownLatch

internal class DecodeThread(context: Context, cameraManager: CameraManager,
                            captureHandler: CaptureHandler,
                            decodeFormats: MutableCollection<BarcodeFormat?>?,
                            baseHints: Map<DecodeHintType, Any?>?,
                            characterSet: String?,
                            resultPointCallback: ResultPointCallback?) : Thread() {
    private val context: Context
    private val cameraManager: CameraManager
    private val hints: MutableMap<DecodeHintType, Any?>
    private var handler: Handler? = null
    private val captureHandler: CaptureHandler
    private val handlerInitLatch: CountDownLatch
    fun getHandler(): Handler? {
        try {
            handlerInitLatch.await()
        } catch (ie: InterruptedException) {
            // continue?
        }
        return handler
    }

    override fun run() {
        Looper.prepare()
        handler = DecodeHandler(context, cameraManager, captureHandler, hints)
        handlerInitLatch.countDown()
        Looper.loop()
    }

    companion object {
        const val BARCODE_BITMAP = "barcode_bitmap"
        const val BARCODE_SCALED_FACTOR = "barcode_scaled_factor"
    }

    init {
        var decodeFormats = decodeFormats
        this.context = context
        this.cameraManager = cameraManager
        this.captureHandler = captureHandler
        handlerInitLatch = CountDownLatch(1)
        hints = EnumMap<DecodeHintType, Any>(DecodeHintType::class.java)
        if (baseHints != null) {
            hints.putAll(baseHints)
        }

        // The prefs can't change while the thread is running, so pick them up once here.
        if (decodeFormats == null || decodeFormats.isEmpty()) {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            decodeFormats = EnumSet.noneOf(BarcodeFormat::class.java)
            if (prefs.getBoolean(Preferences.KEY_DECODE_1D_PRODUCT, true)) {
                decodeFormats.addAll(DecodeFormatManager.PRODUCT_FORMATS!!)
            }
            if (prefs.getBoolean(Preferences.KEY_DECODE_1D_INDUSTRIAL, true)) {
                decodeFormats.addAll(DecodeFormatManager.INDUSTRIAL_FORMATS!!)
            }
            if (prefs.getBoolean(Preferences.KEY_DECODE_QR, true)) {
                decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS)
            }
            if (prefs.getBoolean(Preferences.KEY_DECODE_DATA_MATRIX, true)) {
                decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS)
            }
            if (prefs.getBoolean(Preferences.KEY_DECODE_AZTEC, false)) {
                decodeFormats.addAll(DecodeFormatManager.AZTEC_FORMATS)
            }
            if (prefs.getBoolean(Preferences.KEY_DECODE_PDF417, false)) {
                decodeFormats.addAll(DecodeFormatManager.PDF417_FORMATS)
            }
        }
        hints[DecodeHintType.POSSIBLE_FORMATS] = decodeFormats
        if (characterSet != null) {
            hints[DecodeHintType.CHARACTER_SET] = characterSet
        }
        hints[DecodeHintType.NEED_RESULT_POINT_CALLBACK] = resultPointCallback
    }
}