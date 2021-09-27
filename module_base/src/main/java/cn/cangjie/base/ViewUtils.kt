package cn.cangjie.base

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import com.gyf.immersionbar.ImmersionBar

object ViewUtils {

    fun increaseViewHeightByStatusBarHeight(activity: Activity?, view: View) {
        var lp = view.layoutParams
        if (lp == null) {
            lp = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        val layoutParams = lp as ViewGroup.MarginLayoutParams
        layoutParams.height += ImmersionBar.getStatusBarHeight(activity!!)
        view.layoutParams = layoutParams
    }
}