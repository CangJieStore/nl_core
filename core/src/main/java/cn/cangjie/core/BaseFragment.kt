package cn.cangjie.core

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import cn.cangjie.core.net.CJNet
import cn.cangjie.core.net.CJNetManager
import cn.cangjie.core.net.annotation.NetType
import com.gyf.immersionbar.ImmersionBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

/**
 * @author nvwa@cangjie
 * Create by AS at 2020/7/7 10:39
 */
abstract class BaseFragment<VB : ViewDataBinding> : Fragment(), CoroutineScope by MainScope() {
    protected var mBinding: VB? = null
    private var statusBarView: View? = null
    private var toolbar: Toolbar? = null

    override fun onStart() {
        super.onStart()
        CJNetManager.getInstance(requireActivity().application).register(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        retainInstance = true
        if (mBinding == null) {
            mBinding = DataBindingUtil.inflate(inflater, layoutId(), container, false)
            actionsOnViewInflate()
        }
        return if (mBinding != null) {
            mBinding!!.root.apply { (parent as? ViewGroup)?.removeView(this) }
        } else super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding?.lifecycleOwner = this
        statusBarView = view.findViewById(R.id.status_bar_view)
        toolbar = view.findViewById(R.id.toolbar)
        fitsLayoutOverlap()
        initFragment(view, savedInstanceState)
    }

    private fun fitsLayoutOverlap() {
        if (statusBarView != null) {
            ImmersionBar.setStatusBarView(this, statusBarView)
        } else {
            ImmersionBar.setTitleBar(this, toolbar)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        CJNetManager.getInstance(requireActivity().application).unRegister(this)
        cancel()
        mBinding?.unbind()
    }

    @CJNet
    fun onNetStatusChange(str: @NetType String) {
        if (str == "NONE") {
            netStatus(0)
        } else {
            netStatus(1)
        }
    }

    open fun netStatus(status: Int) {

    }
    open fun toast(notice: String?) {
    }
    abstract fun layoutId(): Int
    open fun actionsOnViewInflate() {}
    abstract fun initFragment(view: View, savedInstanceState: Bundle?)


}