package cn.cangjie.uikit.pickerview.adapter

import cn.cangjie.uikit.pickerview.view.wheelview.adapter.WheelAdapter

/**
 * Numeric Wheel adapter.
 */
class NumericWheelAdapter
    (private val minValue: Int, private val maxValue: Int) : WheelAdapter<Any?> {
    override fun getItem(index: Int): Any {
        return if (index in 0 until itemsCount) {
            minValue + index
        } else 0
    }

    override val itemsCount: Int
        get() = maxValue - minValue + 1

    override fun indexOf(o: Any?): Int {
        return try {
            o as Int - minValue
        } catch (e: Exception) {
            -1
        }
    }
}