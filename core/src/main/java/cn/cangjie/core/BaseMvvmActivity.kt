package cn.cangjie.core

import android.os.Bundle
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import cn.cangjie.core.event.MsgEvent
import cn.cangjie.core.loading.LoadingConfig
import cn.cangjie.core.loading.LoadingDialog
import cn.cangjie.core.mvvm.ViewModelFactory
import java.lang.reflect.ParameterizedType

/**
 * @author nvwa@cangjie
 * Create by AS at 2020/7/8 14:18
 */
abstract class BaseMvvmActivity<VB : ViewDataBinding, VM : BaseViewModel> : BaseActivity<VB>() {

    private var viewModelId = 0
    protected lateinit var viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModelId = initVariableId()
        createViewModel()
        lifecycle.addObserver(viewModel)
        mBinding.setVariable(viewModelId, viewModel)
        registerActionEvent()
        subscribeModel(viewModel)
        super.onCreate(savedInstanceState)
    }

    private fun registerActionEvent() {
        viewModel.defUI.showDialog.observe(this, Observer {
            loading(it)
        })
        viewModel.defUI.dismissDialog.observe(this, Observer {
            dismissLoading()
        })
        viewModel.defUI.toastEvent.observe(this, Observer {
            toast(it)
        })
        viewModel.defUI.actionEvent.observe(this, Observer {
            handleEvent(it)
        })
    }

    open fun loading(word: String?) {
        val loadingConfig = LoadingConfig.Builder()
            .isCanceledOnTouchOutside(false)
            .setLoadingDrawable(R.drawable.rotate)
            .isCancelable(false).build()
        LoadingDialog.showProgress(this, word, loadingConfig)
    }

    open fun dismissLoading() {
        LoadingDialog.dismissProgress()
    }

    open fun handleEvent(msg: MsgEvent) {}

    abstract fun initVariableId(): Int

    open fun subscribeModel(model: VM) {}

    @Suppress("UNCHECKED_CAST")
    private fun createViewModel() {
        val type = javaClass.genericSuperclass
        if (type is ParameterizedType) {
            val tp = type.actualTypeArguments[1]
            val tClass = tp as? Class<VM> ?: BaseViewModel::class.java
            viewModel = ViewModelProvider(this, ViewModelFactory()).get(tClass) as VM
        }
    }
}