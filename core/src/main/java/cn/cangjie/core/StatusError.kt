package cn.cangjie.core

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import cn.cangjie.core.widget.CenterDrawableTextView
import cn.cangjie.core.widget.ErrorReload

/**
 * @author nvwa@cangjie
 * Create by AS at 2020/7/7 11:16
 */
class StatusError @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : CenterDrawableTextView(context, attrs, defStyleAttr) {
    var errorReload: ErrorReload? = null

    init {
        val errDrawable = ContextCompat.getDrawable(context, R.drawable.tag_load_error)
        errDrawable?.setBounds(0, 0, errDrawable.minimumWidth / 2, errDrawable.minimumHeight / 2)
        compoundDrawablePadding = 12
        setCompoundDrawables(null, errDrawable, null, null)
        text = resources.getString(R.string.reload_data)
        setTextColor(Color.parseColor("#FFCCCCCC"))
        setOnClickListener { errorReload?.reload() }
    }
}