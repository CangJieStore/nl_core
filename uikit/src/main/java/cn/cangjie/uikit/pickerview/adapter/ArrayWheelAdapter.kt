package cn.cangjie.uikit.pickerview.adapter

import cn.cangjie.uikit.pickerview.view.wheelview.adapter.WheelAdapter

class ArrayWheelAdapter<T>(
    private val items: List<T?>?
) : WheelAdapter<Any?> {
    override fun getItem(index: Int): Any? {
        return if (index >= 0 && index < items?.size!!) {
            items?.get(index)
        } else ""
    }

    override val itemsCount: Int
        get() = items?.size!!

    override fun indexOf(o: Any?): Int {
        return items?.indexOf(o)!!
    }

}