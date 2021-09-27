package cn.cangjie.resource

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import cn.cangjie.resource.Stylish.Companion.instance

class AButton : AppCompatButton {
    constructor(context: Context) : super(context) {
        setCustomTypeface(context, null)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        setCustomTypeface(context, attrs)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        setCustomTypeface(context, attrs)
    }

    private fun setCustomTypeface(
        context: Context,
        attrs: AttributeSet?
    ) {
        if (isInEditMode) return
        val a = context.obtainStyledAttributes(attrs, R.styleable.TextAppearance)
        val style = a.getInt(R.styleable.TextAppearance_android_textStyle, Typeface.BOLD)
        setTextStyle(style)
        a.recycle()
        post {
            //setTextSize(getTextSize() * Stylish.getInstance().getFontScale());
        }
    }

    private fun setTextStyle(style: Int) {
        instance!!.setTextStyle(this, style)
    }
}