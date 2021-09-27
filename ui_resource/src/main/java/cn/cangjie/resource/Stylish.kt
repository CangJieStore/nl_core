package cn.cangjie.resource

import android.content.Context
import android.graphics.Typeface
import android.widget.TextView
import java.io.IOException
import java.util.*

class Stylish private constructor() {
    companion object {
        val TYPEFACE: MutableMap<String, Typeface?> =
            HashMap()
        private val TAG = Stylish::class.java.simpleName
        var FONT_REGULAR = ""
        var FONT_BOLD = ""
        var FONT_ITALIC = ""
        var FONT_BOLD_ITALIC = ""
        @JvmStatic
        var instance: Stylish? = null

        init {
            instance = Stylish()
        }
    }

    var fontScale = 1.0f
    operator fun set(regular: String, bold: String, italic: String) {
        FONT_REGULAR = regular
        FONT_BOLD = bold
        FONT_ITALIC = italic
    }

    operator fun set(
        regular: String,
        bold: String,
        italic: String,
        boldItalic: String
    ) {
        FONT_REGULAR = regular
        FONT_BOLD = bold
        FONT_ITALIC = italic
        FONT_BOLD_ITALIC = boldItalic
    }

    fun setFontRegular(fontRegular: String) {
        FONT_REGULAR = fontRegular
    }

    fun setFontBold(fontBold: String) {
        FONT_BOLD = fontBold
    }

    fun setFontItalic(fontItalic: String) {
        FONT_ITALIC = fontItalic
    }

    fun setFontBoldItalic(fontBoldItalic: String) {
        FONT_BOLD_ITALIC = fontBoldItalic
    }

    fun getTypeface(context: Context, font: String, style: Int): Typeface? {
        var typeface: Typeface?
        if (!TYPEFACE.containsKey(font)) {
            try {
                typeface = Typeface.createFromAsset(
                    context.assets,
                    font
                )
                TYPEFACE[font] = typeface
            } catch (e: Exception) {
                typeface = Typeface.defaultFromStyle(style)
            }
        } else typeface = TYPEFACE[font]
        return typeface
    }

    fun reqular(context: Context): Typeface? {
        return getTypeface(context, FONT_REGULAR, Typeface.NORMAL)
    }

    fun bold(context: Context): Typeface? {
        return getTypeface(context, FONT_BOLD, Typeface.BOLD)
    }

    fun italic(context: Context): Typeface? {
        return getTypeface(context, FONT_ITALIC, Typeface.ITALIC)
    }

    fun boldItalic(context: Context): Typeface? {
        return getTypeface(context, FONT_BOLD_ITALIC, Typeface.BOLD_ITALIC)
    }

    fun isExist(context: Context, font: String?): Boolean {
        return try {
            Arrays.asList(
                *context.resources.assets.list("")
            ).contains(font)
        } catch (e: IOException) {
            false
        }
    }

    val regular: Typeface?
        get() = TYPEFACE[FONT_REGULAR]

    val bold: Typeface?
        get() = TYPEFACE[FONT_BOLD]

    val italic: Typeface?
        get() = TYPEFACE[FONT_ITALIC]

    val boldItalic: Typeface?
        get() = TYPEFACE[FONT_BOLD_ITALIC]

    fun setTextStyle(textView: TextView, style: Int) {
        val font: String = when (style) {
            Typeface.BOLD -> FONT_BOLD
            Typeface.ITALIC -> FONT_ITALIC
            Typeface.NORMAL -> FONT_REGULAR
            Typeface.BOLD_ITALIC -> FONT_BOLD_ITALIC
            else -> FONT_REGULAR
        }
        try {
            textView.typeface = instance!!.getTypeface(textView.context, font, style)
        } catch (e: Exception) {
//            e.printStackTrace();
        }
    }
}