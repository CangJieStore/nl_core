package cn.cangjie.uikit.pickerview.utils

import android.view.Gravity
import cn.cangjie.uikit.R

/**
 * Created by Sai on 15/8/9.
 */
object PickerViewAnimateUtil {
    private const val INVALID = -1
    @JvmStatic
    fun getAnimationResource(gravity: Int, isInAnimation: Boolean): Int {
        return if (gravity == Gravity.BOTTOM) {
            if (isInAnimation) R.anim.pickerview_slide_in_bottom else R.anim.pickerview_slide_out_bottom
        } else INVALID
    }
}