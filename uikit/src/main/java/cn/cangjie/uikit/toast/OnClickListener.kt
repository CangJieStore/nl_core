package cn.cangjie.uikit.toast

interface OnClickListener<V : Any> {
    /**
     * 点击回调
     */
    fun onClick(toast: CJToast, view: V)
}