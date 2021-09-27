package cn.cangjie.uikit.toast

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import cn.cangjie.uikit.toast.CJToast
import cn.cangjie.uikit.toast.OnTouchListener

internal class ViewTouchWrapper(private val mToast: CJToast, view: View, private val mListener: OnTouchListener<Any>) : View.OnTouchListener {
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        return mListener.onTouch(mToast, v, event)
    }

    init {
        view.isFocusable = true
        view.isEnabled = true
        view.isClickable = true
        view.setOnTouchListener(this)
    }
}