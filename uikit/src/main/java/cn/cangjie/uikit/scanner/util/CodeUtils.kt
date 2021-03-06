package cn.cangjie.uikit.scanner.util

import android.graphics.*
import android.text.TextPaint
import android.text.TextUtils
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import com.google.zxing.*
import com.google.zxing.common.GlobalHistogramBinarizer
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeReader
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import cn.cangjie.uikit.scanner.DecodeFormatManager
import java.util.*

object CodeUtils {
    const val DEFAULT_REQ_WIDTH = 450
    const val DEFAULT_REQ_HEIGHT = 800


    fun createQRCode(content: String?, heightPix: Int, codeColor: Int): Bitmap? {
        return createQRCode(content, heightPix, null, codeColor)
    }

    fun createQRCode(
        content: String?,
        heightPix: Int,
        logo: Bitmap? = null,
        codeColor: Int = Color.BLACK
    ): Bitmap? {
        return createQRCode(content, heightPix, logo, 0.2f, codeColor)
    }


    fun createQRCode(
        content: String?,
        heightPix: Int,
        logo: Bitmap?,
        @FloatRange(from = 0.0, to = 1.0) ratio: Float
    ): Bitmap? {
        //配置参数
        val hints: MutableMap<EncodeHintType, Any> = HashMap()
        hints[EncodeHintType.CHARACTER_SET] = "utf-8"
        //容错级别
        hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H
        //设置空白边距的宽度
        hints[EncodeHintType.MARGIN] = 1 //default is 4
        return createQRCode(content, heightPix, logo, ratio, hints)
    }

    fun createQRCode(
        content: String?,
        heightPix: Int,
        logo: Bitmap?,
        @FloatRange(from = 0.0, to = 1.0) ratio: Float,
        codeColor: Int
    ): Bitmap? {
        //配置参数
        val hints: MutableMap<EncodeHintType, Any> = HashMap()
        hints[EncodeHintType.CHARACTER_SET] = "utf-8"
        //容错级别
        hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H
        //设置空白边距的宽度
        hints[EncodeHintType.MARGIN] = 1 //default is 1
        return createQRCode(content, heightPix, logo, ratio, hints, codeColor)
    }


    @JvmOverloads
    fun createQRCode(
        content: String?,
        heightPix: Int,
        logo: Bitmap?,
        @FloatRange(from = 0.0, to = 1.0) ratio: Float,
        hints: Map<EncodeHintType, *>?,
        codeColor: Int = Color.BLACK
    ): Bitmap? {
        try {

            // 图像数据转换，使用了矩阵转换
            val bitMatrix =
                QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, heightPix, heightPix, hints)
            val pixels = IntArray(heightPix * heightPix)
            // 下面这里按照二维码的算法，逐个生成二维码的图片，
            // 两个for循环是图片横列扫描的结果
            for (y in 0 until heightPix) {
                for (x in 0 until heightPix) {
                    if (bitMatrix[x, y]) {
                        pixels[y * heightPix + x] = codeColor
                    } else {
                        pixels[y * heightPix + x] = Color.WHITE
                    }
                }
            }

            // 生成二维码图片的格式
            var bitmap = Bitmap.createBitmap(heightPix, heightPix, Bitmap.Config.ARGB_8888)
            bitmap!!.setPixels(pixels, 0, heightPix, 0, 0, heightPix, heightPix)
            if (logo != null) {
                bitmap = addLogo(bitmap, logo, ratio)
            }
            return bitmap
        } catch (e: WriterException) {
        }
        return null
    }

    /**
     * 在二维码中间添加Logo图案
     * @param src
     * @param logo
     * @param ratio  logo所占比例 因为二维码的最大容错率为30%，所以建议ratio的范围小于0.3
     * @return
     */
    private fun addLogo(
        src: Bitmap?,
        logo: Bitmap?,
        @FloatRange(from = 0.0, to = 1.0) ratio: Float
    ): Bitmap? {
        if (src == null) {
            return null
        }
        if (logo == null) {
            return src
        }

        //获取图片的宽高
        val srcWidth = src.width
        val srcHeight = src.height
        val logoWidth = logo.width
        val logoHeight = logo.height
        if (srcWidth == 0 || srcHeight == 0) {
            return null
        }
        if (logoWidth == 0 || logoHeight == 0) {
            return src
        }

        //logo大小为二维码整体大小
        val scaleFactor = srcWidth * ratio / logoWidth
        var bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888)
        try {
            val canvas = Canvas(bitmap)
            canvas.drawBitmap(src, 0f, 0f, null)
            canvas.scale(scaleFactor, scaleFactor, srcWidth / 2.toFloat(), srcHeight / 2.toFloat())
            canvas.drawBitmap(
                logo,
                (srcWidth - logoWidth) / 2.toFloat(),
                (srcHeight - logoHeight) / 2.toFloat(),
                null
            )
            canvas.save()
            canvas.restore()
        } catch (e: Exception) {
            bitmap = null
        }
        return bitmap
    }

    /**
     * 解析二维码图片
     * @param bitmapPath
     * @return
     */
    fun parseQRCode(bitmapPath: String?): String? {
        val hints: MutableMap<DecodeHintType, Any> = HashMap()
        hints[DecodeHintType.CHARACTER_SET] = "utf-8"
        hints[DecodeHintType.TRY_HARDER] = java.lang.Boolean.TRUE
        return parseQRCode(bitmapPath, hints)
    }

    /**
     * 解析二维码图片
     * @param bitmapPath
     * @param hints
     * @return
     */
    fun parseQRCode(bitmapPath: String?, hints: Map<DecodeHintType, *>?): String? {
        val result = parseQRCodeResult(bitmapPath, hints)
        return result?.text
    }

    /**
     * 解析二维码图片
     * @param bitmapPath
     * @param hints
     * @return
     */
    fun parseQRCodeResult(bitmapPath: String?, hints: Map<DecodeHintType, *>?): Result? {
        return parseQRCodeResult(bitmapPath, DEFAULT_REQ_WIDTH, DEFAULT_REQ_HEIGHT, hints)
    }

    /**
     * 解析二维码图片
     * @param bitmapPath
     * @param reqWidth
     * @param reqHeight
     * @param hints
     * @return
     */
    fun parseQRCodeResult(
        bitmapPath: String?,
        reqWidth: Int,
        reqHeight: Int,
        hints: Map<DecodeHintType, *>?
    ): Result? {
        var result: Result? = null
        try {
            val reader = QRCodeReader()
            val source = getRGBLuminanceSource(compressBitmap(bitmapPath, reqWidth, reqHeight))
            if (source != null) {
                var isReDecode: Boolean
                try {
                    val bitmap = BinaryBitmap(HybridBinarizer(source))
                    result = reader.decode(bitmap, hints)
                    isReDecode = false
                } catch (e: Exception) {
                    isReDecode = true
                }
                if (isReDecode) {
                    try {
                        val bitmap = BinaryBitmap(HybridBinarizer(source.invert()))
                        result = reader.decode(bitmap, hints)
                        isReDecode = false
                    } catch (e: Exception) {
                        isReDecode = true
                    }
                }
                if (isReDecode) {
                    try {
                        val bitmap = BinaryBitmap(GlobalHistogramBinarizer(source))
                        result = reader.decode(bitmap, hints)
                        isReDecode = false
                    } catch (e: Exception) {
                        isReDecode = true
                    }
                }
                if (isReDecode && source.isRotateSupported) {
                    try {
                        val bitmap = BinaryBitmap(HybridBinarizer(source.rotateCounterClockwise()))
                        result = reader.decode(bitmap, hints)
                    } catch (e: Exception) {
                    }
                }
                reader.reset()
            }
        } catch (e: Exception) {
        }
        return result
    }

    /**
     * 解析一维码/二维码图片
     * @param bitmapPath
     * @return
     */
    fun parseCode(bitmapPath: String?): String? {
        val hints: MutableMap<DecodeHintType, Any?> = HashMap()
        //添加可以解析的编码类型
        val decodeFormats = Vector<BarcodeFormat>()
        decodeFormats.addAll(DecodeFormatManager.ONE_D_FORMATS!!)
        decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS)
        decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS)
        decodeFormats.addAll(DecodeFormatManager.AZTEC_FORMATS)
        decodeFormats.addAll(DecodeFormatManager.PDF417_FORMATS)
        hints[DecodeHintType.CHARACTER_SET] = "utf-8"
        hints[DecodeHintType.TRY_HARDER] = java.lang.Boolean.TRUE
        hints[DecodeHintType.POSSIBLE_FORMATS] = decodeFormats
        return parseCode(bitmapPath, hints)
    }

    /**
     * 解析一维码/二维码图片
     * @param bitmapPath
     * @param hints 解析编码类型
     * @return
     */
    fun parseCode(bitmapPath: String?, hints: Map<DecodeHintType, Any?>?): String? {
        val result = parseCodeResult(bitmapPath, hints)
        return result?.text
    }

    /**
     * 解析一维码/二维码图片
     * @param bitmapPath
     * @param hints 解析编码类型
     * @return
     */
    fun parseCodeResult(bitmapPath: String?, hints: Map<DecodeHintType, Any?>?): Result? {
        return parseCodeResult(bitmapPath, DEFAULT_REQ_WIDTH, DEFAULT_REQ_HEIGHT, hints)
    }

    /**
     * 解析一维码/二维码图片
     * @param bitmapPath
     * @param reqWidth
     * @param reqHeight
     * @param hints 解析编码类型
     * @return
     */
    fun parseCodeResult(
        bitmapPath: String?,
        reqWidth: Int,
        reqHeight: Int,
        hints: Map<DecodeHintType, Any?>?
    ): Result? {
        var result: Result? = null
        try {
            val reader = MultiFormatReader()
            reader.setHints(hints)
            val source = getRGBLuminanceSource(compressBitmap(bitmapPath, reqWidth, reqHeight))
            if (source != null) {
                var isReDecode: Boolean
                try {
                    val bitmap = BinaryBitmap(HybridBinarizer(source))
                    result = reader.decodeWithState(bitmap)
                    isReDecode = false
                } catch (e: Exception) {
                    isReDecode = true
                }
                if (isReDecode) {
                    try {
                        val bitmap = BinaryBitmap(HybridBinarizer(source.invert()))
                        result = reader.decodeWithState(bitmap)
                        isReDecode = false
                    } catch (e: Exception) {
                        isReDecode = true
                    }
                }
                if (isReDecode) {
                    try {
                        val bitmap = BinaryBitmap(GlobalHistogramBinarizer(source))
                        result = reader.decodeWithState(bitmap)
                        isReDecode = false
                    } catch (e: Exception) {
                        isReDecode = true
                    }
                }
                if (isReDecode && source.isRotateSupported) {
                    try {
                        val bitmap = BinaryBitmap(HybridBinarizer(source.rotateCounterClockwise()))
                        result = reader.decodeWithState(bitmap)
                    } catch (e: Exception) {
                    }
                }
                reader.reset()
            }
        } catch (e: Exception) {
        }
        return result
    }

    /**
     * 压缩图片
     * @param path
     * @return
     */
    private fun compressBitmap(path: String?, reqWidth: Int, reqHeight: Int): Bitmap {
        val newOpts = BitmapFactory.Options()
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true //获取原始图片大小
        BitmapFactory.decodeFile(path, newOpts) // 此时返回bm为空
        val width = newOpts.outWidth.toFloat()
        val height = newOpts.outHeight.toFloat()
        // 缩放比，由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        var wSize = 1 // wSize=1表示不缩放
        if (width > reqWidth) { // 如果宽度大的话根据宽度固定大小缩放
            wSize = (width / reqWidth).toInt()
        }
        var hSize = 1 // wSize=1表示不缩放
        if (height > reqHeight) { // 如果高度高的话根据宽度固定大小缩放
            hSize = (height / reqHeight).toInt()
        }
        var size = Math.max(wSize, hSize)
        if (size <= 0) size = 1
        newOpts.inSampleSize = size // 设置缩放比例
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        newOpts.inJustDecodeBounds = false
        return BitmapFactory.decodeFile(path, newOpts)
    }

    /**
     * 获取RGBLuminanceSource
     * @param bitmap
     * @return
     */
    private fun getRGBLuminanceSource(bitmap: Bitmap): RGBLuminanceSource {
        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        return RGBLuminanceSource(width, height, pixels)
    }

    /**
     * 生成条形码
     * @param content
     * @param desiredWidth
     * @param desiredHeight
     * @return
     */
    fun createBarCode(content: String?, desiredWidth: Int, desiredHeight: Int): Bitmap? {
        return createBarCode(content, BarcodeFormat.CODE_128, desiredWidth, desiredHeight, null)
    }

    fun createBarCode(
        content: String?,
        desiredWidth: Int,
        desiredHeight: Int,
        isShowText: Boolean
    ): Bitmap? {
        return createBarCode(
            content,
            BarcodeFormat.CODE_128,
            desiredWidth,
            desiredHeight,
            null,
            isShowText,
            40,
            Color.BLACK
        )
    }

    /**
     * 生成条形码
     * @param content
     * @param desiredWidth
     * @param desiredHeight
     * @param isShowText
     * @param codeColor
     * @return
     */
    fun createBarCode(
        content: String?,
        desiredWidth: Int,
        desiredHeight: Int,
        isShowText: Boolean,
        @ColorInt codeColor: Int
    ): Bitmap? {
        return createBarCode(
            content,
            BarcodeFormat.CODE_128,
            desiredWidth,
            desiredHeight,
            null,
            isShowText,
            40,
            codeColor
        )
    }

    /**
     * 生成条形码
     * @param content
     * @param format
     * @param desiredWidth
     * @param desiredHeight
     * @param isShowText
     * @param codeColor
     * @return
     */
    fun createBarCode(
        content: String?,
        format: BarcodeFormat?,
        desiredWidth: Int,
        desiredHeight: Int,
        isShowText: Boolean,
        @ColorInt codeColor: Int
    ): Bitmap? {
        return createBarCode(
            content,
            format,
            desiredWidth,
            desiredHeight,
            null,
            isShowText,
            40,
            codeColor
        )
    }

    /**
     * 生成条形码
     * @param content
     * @param format
     * @param desiredWidth
     * @param desiredHeight
     * @param hints
     * @param isShowText
     * @return
     */
    fun createBarCode(
        content: String?,
        format: BarcodeFormat?,
        desiredWidth: Int,
        desiredHeight: Int,
        hints: Map<EncodeHintType?, *>?,
        isShowText: Boolean,
        @ColorInt codeColor: Int
    ): Bitmap? {
        return createBarCode(
            content,
            format,
            desiredWidth,
            desiredHeight,
            hints,
            isShowText,
            40,
            codeColor
        )
    }

    fun createBarCode(
        content: String?,
        format: BarcodeFormat?,
        desiredWidth: Int,
        desiredHeight: Int,
        hints: Map<EncodeHintType?, *>? = null,
        isShowText: Boolean = false,
        textSize: Int = 40,
        @ColorInt codeColor: Int = Color.BLACK
    ): Bitmap? {
        if (TextUtils.isEmpty(content)) {
            return null
        }
        val WHITE = Color.WHITE
        val writer = MultiFormatWriter()
        try {
            val result = writer.encode(
                content, format, desiredWidth,
                desiredHeight, hints
            )
            val width = result.width
            val height = result.height
            val pixels = IntArray(width * height)
            // All are 0, or black, by default
            for (y in 0 until height) {
                val offset = y * width
                for (x in 0 until width) {
                    pixels[offset + x] = if (result[x, y]) codeColor else WHITE
                }
            }
            val bitmap = Bitmap.createBitmap(
                width, height,
                Bitmap.Config.ARGB_8888
            )
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
            return if (isShowText) {
                addCode(bitmap, content, textSize, codeColor, textSize / 2)
            } else bitmap
        } catch (e: WriterException) {
        }
        return null
    }

    /**
     * 条形码下面添加文本信息
     * @param src
     * @param code
     * @param textSize
     * @param textColor
     * @return
     */
    private fun addCode(
        src: Bitmap?,
        code: String?,
        textSize: Int,
        @ColorInt textColor: Int,
        offset: Int
    ): Bitmap? {
        if (src == null) {
            return null
        }
        if (TextUtils.isEmpty(code)) {
            return src
        }

        //获取图片的宽高
        val srcWidth = src.width
        val srcHeight = src.height
        if (srcWidth <= 0 || srcHeight <= 0) {
            return null
        }
        var bitmap = Bitmap.createBitmap(
            srcWidth,
            srcHeight + textSize + offset * 2,
            Bitmap.Config.ARGB_8888
        )
        try {
            val canvas = Canvas(bitmap)
            canvas.drawBitmap(src, 0f, 0f, null)
            val paint = TextPaint()
            paint.textSize = textSize.toFloat()
            paint.color = textColor
            paint.textAlign = Paint.Align.CENTER
            canvas.drawText(
                code!!,
                srcWidth / 2.toFloat(),
                srcHeight + textSize / 2 + offset.toFloat(),
                paint
            )
            canvas.save()
            canvas.restore()
        } catch (e: Exception) {
            bitmap = null
        }
        return bitmap
    }
}