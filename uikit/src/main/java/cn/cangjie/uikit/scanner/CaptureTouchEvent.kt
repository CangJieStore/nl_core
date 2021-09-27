package cn.cangjie.uikit.scanner

import android.view.MotionEvent
interface CaptureTouchEvent {
    fun onTouchEvent(event: MotionEvent?): Boolean
}