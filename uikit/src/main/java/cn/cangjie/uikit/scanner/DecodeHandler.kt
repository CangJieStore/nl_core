package cn.cangjie.uikit.scanner

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Point
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.WindowManager
import cn.cangjie.uikit.R
import com.google.zxing.*
import com.google.zxing.common.GlobalHistogramBinarizer
import com.google.zxing.common.HybridBinarizer
import cn.cangjie.uikit.scanner.camera.CameraManager
import java.io.ByteArrayOutputStream
import kotlin.math.min

internal class DecodeHandler(context: Context, cameraManager: CameraManager, handler: CaptureHandler?, hints: MutableMap<DecodeHintType, Any?>) : Handler() {
    private val context: Context
    private val cameraManager: CameraManager
    private val handler: CaptureHandler?
    private val multiFormatReader: MultiFormatReader = MultiFormatReader()
    private var running = true
    private var lastZoomTime: Long = 0
    override fun handleMessage(message: Message) {
        if (message == null || !running) {
            return
        }
        if (message.what == R.id.decode) {
            decode(message.obj as ByteArray, message.arg1, message.arg2, isScreenPortrait, handler!!.isSupportVerticalCode)
        } else if (message.what == R.id.quit) {
            running = false
            Looper.myLooper()?.quit()
        }
    }

    private val isScreenPortrait: Boolean
        private get() {
            val manager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display = manager.defaultDisplay
            val screenResolution = Point()
            display.getSize(screenResolution)
            return screenResolution.x < screenResolution.y
        }

    /**
     * Decode the data within the viewfinder rectangle, and time how long it took. For efficiency,
     * reuse the same reader objects from one decode to the next.
     *
     * @param data   The YUV preview frame.
     * @param width  The width of the preview frame.
     * @param height The height of the preview frame.
     */
    private fun decode(data: ByteArray, width: Int, height: Int, isScreenPortrait: Boolean, isSupportVerticalCode: Boolean) {
        val start = System.currentTimeMillis()
        var rawResult: Result? = null
        var source = buildPlanarYUVLuminanceSource(data, width, height, isScreenPortrait)
        if (source != null) {
            var isReDecode: Boolean
            try {
                val bitmap = BinaryBitmap(HybridBinarizer(source))
                rawResult = multiFormatReader.decodeWithState(bitmap)
                isReDecode = false
            } catch (e: Exception) {
                isReDecode = true
            }
            if (isReDecode && handler!!.isSupportLuminanceInvert) {
                try {
                    val bitmap = BinaryBitmap(HybridBinarizer(source.invert()))
                    rawResult = multiFormatReader.decodeWithState(bitmap)
                    isReDecode = false
                } catch (e: Exception) {
                    isReDecode = true
                }
            }
            if (isReDecode) {
                try {
                    val bitmap = BinaryBitmap(GlobalHistogramBinarizer(source))
                    rawResult = multiFormatReader.decodeWithState(bitmap)
                    isReDecode = false
                } catch (e: Exception) {
                    isReDecode = true
                }
            }
            if (isReDecode && isSupportVerticalCode) {
                source = buildPlanarYUVLuminanceSource(data, width, height, !isScreenPortrait)
                if (source != null) {
                    try {
                        val bitmap = BinaryBitmap(HybridBinarizer(source))
                        rawResult = multiFormatReader.decodeWithState(bitmap)
                    } catch (e: Exception) {
                    }
                }
            }
            multiFormatReader.reset()
        }
        if (rawResult != null) {
            // Don't log the barcode contents for security.
            val end = System.currentTimeMillis()
            val barcodeFormat = rawResult.barcodeFormat
            if (handler != null && handler.isSupportAutoZoom && barcodeFormat == BarcodeFormat.QR_CODE) {
                val resultPoints = rawResult.resultPoints
                if (resultPoints.size >= 3) {
                    val distance1 = ResultPoint.distance(resultPoints[0], resultPoints[1])
                    val distance2 = ResultPoint.distance(resultPoints[1], resultPoints[2])
                    val distance3 = ResultPoint.distance(resultPoints[0], resultPoints[2])
                    val maxDistance = Math.max(Math.max(distance1, distance2), distance3).toInt()
                    if (handleAutoZoom(maxDistance, width)) {
                        val message = Message.obtain()
                        message.what = R.id.decode_succeeded
                        message.obj = rawResult
                        if (handler.isReturnBitmap) {
                            val bundle = Bundle()
                            bundleThumbnail(source, bundle)
                            message.data = bundle
                        }
                        handler.sendMessageDelayed(message, 300)
                        return
                    }
                }
            }
            if (handler != null) {
                val message = Message.obtain(handler, R.id.decode_succeeded, rawResult)
                if (handler.isReturnBitmap) {
                    val bundle = Bundle()
                    bundleThumbnail(source, bundle)
                    message.data = bundle
                }
                message.sendToTarget()
            }
        } else {
            if (handler != null) {
                val message = Message.obtain(handler, R.id.decode_failed)
                message.sendToTarget()
            }
        }
    }

    private fun buildPlanarYUVLuminanceSource(data: ByteArray, width: Int, height: Int, isRotate: Boolean): PlanarYUVLuminanceSource {
        var width = width
        var height = height
        val source: PlanarYUVLuminanceSource
        if (isRotate) {
            val rotatedData = ByteArray(data.size)
            for (y in 0 until height) {
                for (x in 0 until width) rotatedData[x * height + height - y - 1] = data[x + y * width]
            }
            val tmp = width
            width = height
            height = tmp
            source = cameraManager.buildLuminanceSource(rotatedData, width, height)!!
        } else {
            source = cameraManager.buildLuminanceSource(data, width, height)!!
        }
        return source
    }

    private fun handleAutoZoom(length: Int, width: Int): Boolean {
        if (lastZoomTime > System.currentTimeMillis() - 1000) {
            return true
        }
        if (length < width / 5) {
            val camera = cameraManager.openCamera?.camera
            if (camera != null) {
                val params = camera.parameters
                if (params.isZoomSupported) {
                    val maxZoom = params.maxZoom
                    val zoom = params.zoom
                    params.zoom = min(zoom + maxZoom / 5, maxZoom)
                    camera.parameters = params
                    lastZoomTime = System.currentTimeMillis()
                    return true
                } else {
                }
            }
        }
        return false
    }

    companion object {
        private fun bundleThumbnail(source: PlanarYUVLuminanceSource?, bundle: Bundle) {
            val pixels = source!!.renderThumbnail()
            val width = source.thumbnailWidth
            val height = source.thumbnailHeight
            val bitmap = Bitmap.createBitmap(pixels, 0, width, width, height, Bitmap.Config.ARGB_8888)
            val out = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out)
            bundle.putByteArray(DecodeThread.BARCODE_BITMAP, out.toByteArray())
            bundle.putFloat(DecodeThread.BARCODE_SCALED_FACTOR, width.toFloat() / source.width)
        }
    }

    init {
        multiFormatReader.setHints(hints)
        this.context = context
        this.cameraManager = cameraManager
        this.handler = handler
    }
}