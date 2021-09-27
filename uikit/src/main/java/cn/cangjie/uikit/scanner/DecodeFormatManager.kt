package cn.cangjie.uikit.scanner

import android.content.Intent
import android.net.Uri
import com.google.zxing.BarcodeFormat
import java.util.*
import java.util.regex.Pattern

object DecodeFormatManager {
    private val COMMA_PATTERN = Pattern.compile(",")
    var PRODUCT_FORMATS: Set<BarcodeFormat>? = null
    var INDUSTRIAL_FORMATS: Set<BarcodeFormat>? = null
    @JvmField
    var ONE_D_FORMATS: MutableSet<BarcodeFormat>? = null
    @JvmField
    val QR_CODE_FORMATS: Set<BarcodeFormat> = EnumSet.of(BarcodeFormat.QR_CODE)
    @JvmField
    val DATA_MATRIX_FORMATS: Set<BarcodeFormat> = EnumSet.of(BarcodeFormat.DATA_MATRIX)
    @JvmField
    val AZTEC_FORMATS: Set<BarcodeFormat> = EnumSet.of(BarcodeFormat.AZTEC)
    @JvmField
    val PDF417_FORMATS: Set<BarcodeFormat> = EnumSet.of(BarcodeFormat.PDF_417)
    private var FORMATS_FOR_MODE: MutableMap<String, Set<BarcodeFormat>?>? = null
    fun parseDecodeFormats(intent: Intent): Set<BarcodeFormat>? {
        var scanFormats: Iterable<String?>? = null
        val scanFormatsString: CharSequence? = intent.getStringExtra(Intents.Scan.FORMATS)
        if (scanFormatsString != null) {
            scanFormats = listOf(*COMMA_PATTERN.split(scanFormatsString))
        }
        return parseDecodeFormats(scanFormats, intent.getStringExtra(Intents.Scan.MODE))
    }

    fun parseDecodeFormats(inputUri: Uri): Set<BarcodeFormat>? {
        var formats = inputUri.getQueryParameters(Intents.Scan.FORMATS)
        if (formats != null && formats.size == 1 && formats[0] != null) {
            formats = Arrays.asList(*COMMA_PATTERN.split(formats[0]))
        }
        return parseDecodeFormats(formats, inputUri.getQueryParameter(Intents.Scan.MODE))
    }

    private fun parseDecodeFormats(scanFormats: Iterable<String?>?, decodeMode: String?): Set<BarcodeFormat>? {
        if (scanFormats != null) {
            val formats: MutableSet<BarcodeFormat> = EnumSet.noneOf(BarcodeFormat::class.java)
            try {
                for (format in scanFormats) {
                    formats.add(BarcodeFormat.valueOf(format!!))
                }
                return formats
            } catch (iae: IllegalArgumentException) {
                // ignore it then
            }
        }
        return if (decodeMode != null) {
            FORMATS_FOR_MODE!![decodeMode]
        } else null
    }

    init {
        PRODUCT_FORMATS = EnumSet.of(BarcodeFormat.UPC_A,
                BarcodeFormat.UPC_E,
                BarcodeFormat.EAN_13,
                BarcodeFormat.EAN_8,
                BarcodeFormat.RSS_14,
                BarcodeFormat.RSS_EXPANDED)
        INDUSTRIAL_FORMATS = EnumSet.of(BarcodeFormat.CODE_39,
                BarcodeFormat.CODE_93,
                BarcodeFormat.CODE_128,
                BarcodeFormat.ITF,
                BarcodeFormat.CODABAR)
        ONE_D_FORMATS = EnumSet.copyOf(PRODUCT_FORMATS)
        ONE_D_FORMATS?.addAll(INDUSTRIAL_FORMATS!!)
    }

    init {
        FORMATS_FOR_MODE = HashMap()
        FORMATS_FOR_MODE!![Intents.Scan.ONE_D_MODE] = ONE_D_FORMATS
        FORMATS_FOR_MODE!![Intents.Scan.PRODUCT_MODE] = PRODUCT_FORMATS
        FORMATS_FOR_MODE!![Intents.Scan.QR_CODE_MODE] = QR_CODE_FORMATS
        FORMATS_FOR_MODE!![Intents.Scan.DATA_MATRIX_MODE] = DATA_MATRIX_FORMATS
        FORMATS_FOR_MODE!![Intents.Scan.AZTEC_MODE] = AZTEC_FORMATS
        FORMATS_FOR_MODE!![Intents.Scan.PDF417_MODE] = PDF417_FORMATS
    }
}