package com.cangjie.pickerview.view

import android.graphics.Typeface
import android.view.View
import cn.cangjie.uikit.R
import cn.cangjie.uikit.pickerview.adapter.ArrayWheelAdapter
import cn.cangjie.uikit.pickerview.listener.OnOptionsSelectChangeListener
import cn.cangjie.uikit.pickerview.view.wheelview.WheelView
import cn.cangjie.uikit.pickerview.view.wheelview.listener.OnItemSelectedListener

class WheelOptions<T>(var view: View, //切换时，还原第一项
                      private val isRestoreItem: Boolean) {
    private val wv_option1: WheelView = view.findViewById<View>(R.id.options1) as WheelView
    private val wv_option2: WheelView = view.findViewById<View>(R.id.options2) as WheelView
    private val wv_option3: WheelView = view.findViewById<View>(R.id.options3) as WheelView
    private var mOptions1Items: List<T?>? = null
    private var mOptions2Items: List<List<T?>>? = null
    private var mOptions3Items: List<List<List<T?>>>? = null
    private var linkage = true //默认联动
    private var wheelListener_option1: OnItemSelectedListener? = null
    private var wheelListener_option2: OnItemSelectedListener? = null
    private var optionsSelectChangeListener: OnOptionsSelectChangeListener? = null
    fun setPicker(options1Items: List<T>?,
                  options2Items: List<List<T>>?,
                  options3Items: List<List<List<T>>>?) {
        mOptions1Items = options1Items
        mOptions2Items = options2Items
        mOptions3Items = options3Items

        // 选项1
        wv_option1.adapter = ArrayWheelAdapter<Any?>(mOptions1Items) // 设置显示数据
        wv_option1.currentItem = 0 // 初始化时显示的数据
        // 选项2
        if (mOptions2Items != null) {
            wv_option2.adapter = ArrayWheelAdapter<Any?>((mOptions2Items as List<List<T>>)[0]) // 设置显示数据
        }
        wv_option2.currentItem = wv_option2.currentItem // 初始化时显示的数据
        // 选项3
        if (mOptions3Items != null) {
            wv_option3.adapter = ArrayWheelAdapter<Any?>((mOptions3Items as List<List<List<T>>>)[0][0]) // 设置显示数据
        }
        wv_option3.currentItem = wv_option3.currentItem
        wv_option1.setIsOptions(true)
        wv_option2.setIsOptions(true)
        wv_option3.setIsOptions(true)
        if (mOptions2Items == null) {
            wv_option2.visibility = View.GONE
        } else {
            wv_option2.visibility = View.VISIBLE
        }
        if (mOptions3Items == null) {
            wv_option3.visibility = View.GONE
        } else {
            wv_option3.visibility = View.VISIBLE
        }

        // 联动监听器
        wheelListener_option1 = object : OnItemSelectedListener {
            override fun onItemSelected(index: Int) {
                var opt2Select = 0
                if (mOptions2Items == null) { //只有1级联动数据
                    if (optionsSelectChangeListener != null) {
                        optionsSelectChangeListener!!.onOptionsSelectChanged(wv_option1.currentItem, 0, 0)
                    }
                } else {
                    if (!isRestoreItem) {
                        opt2Select = wv_option2.currentItem //上一个opt2的选中位置
                        //新opt2的位置，判断如果旧位置没有超过数据范围，则沿用旧位置，否则选中最后一项
                        opt2Select = if (opt2Select >= (mOptions2Items as List<List<T>>)[index].size - 1) (mOptions2Items as List<List<T>>)[index].size - 1 else opt2Select
                    }
                    wv_option2.adapter = ArrayWheelAdapter<Any?>((mOptions2Items as List<List<T>>)[index])
                    wv_option2.currentItem = opt2Select
                    if (mOptions3Items != null) {
                        wheelListener_option2!!.onItemSelected(opt2Select)
                    } else { //只有2级联动数据，滑动第1项回调
                        if (optionsSelectChangeListener != null) {
                            optionsSelectChangeListener!!.onOptionsSelectChanged(index, opt2Select, 0)
                        }
                    }
                }
            }
        }
        wheelListener_option2 = object : OnItemSelectedListener {
            override fun onItemSelected(index: Int) {
                var index = index
                if (mOptions3Items != null) {
                    var opt1Select = wv_option1.currentItem
                    opt1Select = if (opt1Select >= (mOptions3Items as List<List<List<T>>>).size - 1) (mOptions3Items as List<List<List<T>>>).size - 1 else opt1Select
                    index = if (index >= mOptions2Items!![opt1Select].size - 1) mOptions2Items!![opt1Select].size - 1 else index
                    var opt3 = 0
                    if (!isRestoreItem) {
                        // wv_option3.getCurrentItem() 上一个opt3的选中位置
                        //新opt3的位置，判断如果旧位置没有超过数据范围，则沿用旧位置，否则选中最后一项
                        opt3 = if (wv_option3.currentItem >= (mOptions3Items as List<List<List<T>>>)[opt1Select][index].size - 1) (mOptions3Items as List<List<List<T>>>)[opt1Select][index].size - 1 else wv_option3.currentItem
                    }
                    wv_option3.adapter = ArrayWheelAdapter<Any?>((mOptions3Items as List<List<List<T>>>)[wv_option1.currentItem][index])
                    wv_option3.currentItem = opt3

                    //3级联动数据实时回调
                    if (optionsSelectChangeListener != null) {
                        optionsSelectChangeListener!!.onOptionsSelectChanged(wv_option1.currentItem, index, opt3)
                    }
                } else { //只有2级联动数据，滑动第2项回调
                    if (optionsSelectChangeListener != null) {
                        optionsSelectChangeListener!!.onOptionsSelectChanged(wv_option1.currentItem, index, 0)
                    }
                }
            }
        }

        // 添加联动监听
        if (options1Items != null && linkage) {
            wv_option1.setOnItemSelectedListener(wheelListener_option1)
        }
        if (options2Items != null && linkage) {
            wv_option2.setOnItemSelectedListener(wheelListener_option2)
        }
        if (options3Items != null && linkage && optionsSelectChangeListener != null) {
            wv_option3.setOnItemSelectedListener(object : OnItemSelectedListener {
                override fun onItemSelected(index: Int) {
                    optionsSelectChangeListener!!.onOptionsSelectChanged(wv_option1.currentItem, wv_option2.currentItem, index)
                }
            })
        }
    }

    //不联动情况下
    fun setNPicker(options1Items: List<T>?, options2Items: List<T>?, options3Items: List<T>?) {

        // 选项1
        wv_option1.adapter = ArrayWheelAdapter(options1Items) // 设置显示数据
        wv_option1.currentItem = 0 // 初始化时显示的数据
        // 选项2
        if (options2Items != null) {
            wv_option2.adapter = ArrayWheelAdapter(options2Items) // 设置显示数据
        }
        wv_option2.currentItem = wv_option2.currentItem // 初始化时显示的数据
        // 选项3
        if (options3Items != null) {
            wv_option3.adapter = ArrayWheelAdapter(options3Items) // 设置显示数据
        }
        wv_option3.currentItem = wv_option3.currentItem
        wv_option1.setIsOptions(true)
        wv_option2.setIsOptions(true)
        wv_option3.setIsOptions(true)
        if (optionsSelectChangeListener != null) {
            wv_option1.setOnItemSelectedListener(object : OnItemSelectedListener {
                override fun onItemSelected(index: Int) {
                    optionsSelectChangeListener!!.onOptionsSelectChanged(index, wv_option2.currentItem, wv_option3.currentItem)
                }
            })
        }
        if (options2Items == null) {
            wv_option2.visibility = View.GONE
        } else {
            wv_option2.visibility = View.VISIBLE
            if (optionsSelectChangeListener != null) {
                wv_option2.setOnItemSelectedListener(object : OnItemSelectedListener {
                    override fun onItemSelected(index: Int) {
                        optionsSelectChangeListener!!.onOptionsSelectChanged(wv_option1.currentItem, index, wv_option3.currentItem)
                    }
                })
            }
        }
        if (options3Items == null) {
            wv_option3.visibility = View.GONE
        } else {
            wv_option3.visibility = View.VISIBLE
            if (optionsSelectChangeListener != null) {
                wv_option3.setOnItemSelectedListener(object : OnItemSelectedListener {
                    override fun onItemSelected(index: Int) {
                        optionsSelectChangeListener!!.onOptionsSelectChanged(wv_option1.currentItem, wv_option2.currentItem, index)
                    }
                })
            }
        }
    }

    fun setTextContentSize(textSize: Int) {
        wv_option1.setTextSize(textSize.toFloat())
        wv_option2.setTextSize(textSize.toFloat())
        wv_option3.setTextSize(textSize.toFloat())
    }

    private fun setLineSpacingMultiplier() {}

    /**
     * 设置选项的单位
     *
     * @param label1 单位
     * @param label2 单位
     * @param label3 单位
     */
    fun setLabels(label1: String?, label2: String?, label3: String?) {
        if (label1 != null) {
            wv_option1.setLabel(label1)
        }
        if (label2 != null) {
            wv_option2.setLabel(label2)
        }
        if (label3 != null) {
            wv_option3.setLabel(label3)
        }
    }

    /**
     * 设置x轴偏移量
     */
    fun setTextXOffset(x_offset_one: Int, x_offset_two: Int, x_offset_three: Int) {
        wv_option1.setTextXOffset(x_offset_one)
        wv_option2.setTextXOffset(x_offset_two)
        wv_option3.setTextXOffset(x_offset_three)
    }

    /**
     * 设置是否循环滚动
     *
     * @param cyclic 是否循环
     */
    fun setCyclic(cyclic: Boolean) {
        wv_option1.setCyclic(cyclic)
        wv_option2.setCyclic(cyclic)
        wv_option3.setCyclic(cyclic)
    }

    /**
     * 设置字体样式
     *
     * @param font 系统提供的几种样式
     */
    fun setTypeface(font: Typeface?) {
        wv_option1.setTypeface(font)
        wv_option2.setTypeface(font)
        wv_option3.setTypeface(font)
    }

    /**
     * 分别设置第一二三级是否循环滚动
     *
     * @param cyclic1,cyclic2,cyclic3 是否循环
     */
    fun setCyclic(cyclic1: Boolean, cyclic2: Boolean, cyclic3: Boolean) {
        wv_option1.setCyclic(cyclic1)
        wv_option2.setCyclic(cyclic2)
        wv_option3.setCyclic(cyclic3)
    }//非空判断//非空判断

    /**
     * 返回当前选中的结果对应的位置数组 因为支持三级联动效果，分三个级别索引，0，1，2。
     * 在快速滑动未停止时，点击确定按钮，会进行判断，如果匹配数据越界，则设为0，防止index出错导致崩溃。
     *
     * @return 索引数组
     */
    val currentItems: IntArray
        get() {
            val currentItems = IntArray(3)
            currentItems[0] = wv_option1.currentItem
            if (mOptions2Items != null && mOptions2Items!!.isNotEmpty()) { //非空判断
                currentItems[1] = if (wv_option2.currentItem > mOptions2Items!![currentItems[0]].size - 1) 0 else wv_option2.currentItem
            } else {
                currentItems[1] = wv_option2.currentItem
            }
            if (mOptions3Items != null && mOptions3Items!!.isNotEmpty()) { //非空判断
                currentItems[2] = if (wv_option3.currentItem > mOptions3Items!![currentItems[0]][currentItems[1]].size - 1) 0 else wv_option3.currentItem
            } else {
                currentItems[2] = wv_option3.currentItem
            }
            return currentItems
        }

    fun setCurrentItems(option1: Int, option2: Int, option3: Int) {
        if (linkage) {
            itemSelected(option1, option2, option3)
        } else {
            wv_option1.currentItem = option1
            wv_option2.currentItem = option2
            wv_option3.currentItem = option3
        }
    }

    private fun itemSelected(opt1Select: Int, opt2Select: Int, opt3Select: Int) {
        if (mOptions1Items != null) {
            wv_option1.currentItem = opt1Select
        }
        if (mOptions2Items != null) {
            wv_option2.adapter = ArrayWheelAdapter<Any?>(mOptions2Items!![opt1Select])
            wv_option2.currentItem = opt2Select
        }
        if (mOptions3Items != null) {
            wv_option3.adapter = ArrayWheelAdapter<Any?>(mOptions3Items!![opt1Select][opt2Select])
            wv_option3.currentItem = opt3Select
        }
    }

    /**
     * 设置间距倍数,但是只能在1.2-4.0f之间
     *
     * @param lineSpacingMultiplier
     */
    fun setLineSpacingMultiplier(lineSpacingMultiplier: Float) {
        wv_option1.setLineSpacingMultiplier(lineSpacingMultiplier)
        wv_option2.setLineSpacingMultiplier(lineSpacingMultiplier)
        wv_option3.setLineSpacingMultiplier(lineSpacingMultiplier)
    }

    /**
     * 设置分割线的颜色
     *
     * @param dividerColor
     */
    fun setDividerColor(dividerColor: Int) {
        wv_option1.setDividerColor(dividerColor)
        wv_option2.setDividerColor(dividerColor)
        wv_option3.setDividerColor(dividerColor)
    }

    /**
     * 设置分割线的类型
     *
     * @param dividerType
     */
    fun setDividerType(dividerType: WheelView.DividerType?) {
        wv_option1.setDividerType(dividerType)
        wv_option2.setDividerType(dividerType)
        wv_option3.setDividerType(dividerType)
    }

    /**
     * 设置分割线之间的文字的颜色
     *
     * @param textColorCenter
     */
    fun setTextColorCenter(textColorCenter: Int) {
        wv_option1.setTextColorCenter(textColorCenter)
        wv_option2.setTextColorCenter(textColorCenter)
        wv_option3.setTextColorCenter(textColorCenter)
    }

    /**
     * 设置分割线以外文字的颜色
     *
     * @param textColorOut
     */
    fun setTextColorOut(textColorOut: Int) {
        wv_option1.setTextColorOut(textColorOut)
        wv_option2.setTextColorOut(textColorOut)
        wv_option3.setTextColorOut(textColorOut)
    }

    /**
     * Label 是否只显示中间选中项的
     *
     * @param isCenterLabel
     */
    fun isCenterLabel(isCenterLabel: Boolean) {
        wv_option1.isCenterLabel(isCenterLabel)
        wv_option2.isCenterLabel(isCenterLabel)
        wv_option3.isCenterLabel(isCenterLabel)
    }

    fun setOptionsSelectChangeListener(optionsSelectChangeListener: OnOptionsSelectChangeListener?) {
        this.optionsSelectChangeListener = optionsSelectChangeListener
    }

    fun setLinkage(linkage: Boolean) {
        this.linkage = linkage
    }

    /**
     * 设置最大可见数目
     *
     * @param itemsVisible 建议设置为 3 ~ 9之间。
     */
    fun setItemsVisible(itemsVisible: Int) {
        wv_option1.setItemsVisibleCount(itemsVisible)
        wv_option2.setItemsVisibleCount(itemsVisible)
        wv_option3.setItemsVisibleCount(itemsVisible)
    }

    fun setAlphaGradient(isAlphaGradient: Boolean) {
        wv_option1.setAlphaGradient(isAlphaGradient)
        wv_option2.setAlphaGradient(isAlphaGradient)
        wv_option3.setAlphaGradient(isAlphaGradient)
    }

    init {
        // 初始化时显示的数据
    }
}