package cn.cangjie.base

import android.content.Context

object DensityUtil {

    fun dip2px(c: Context, dpValue: Float): Int {
        var scale: Float = c.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }
}