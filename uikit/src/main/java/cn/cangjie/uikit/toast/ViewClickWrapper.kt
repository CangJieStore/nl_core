package cn.cangjie.uikit.toast

import android.view.View
import cn.cangjie.uikit.toast.CJToast
import cn.cangjie.uikit.toast.OnClickListener

internal class ViewClickWrapper(private val mToast: CJToast, view: View, private val mListener: OnClickListener<Any>) : View.OnClickListener {
    override fun onClick(v: View) {
        mListener.onClick(mToast, v)
    }
    init {
        view.setOnClickListener(this)
    }
}