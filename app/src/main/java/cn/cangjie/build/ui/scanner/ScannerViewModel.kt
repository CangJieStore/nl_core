package cn.cangjie.build.ui.scanner

import cn.cangjie.core.BaseViewModel
import cn.cangjie.core.binding.BindingAction
import cn.cangjie.core.binding.BindingCommand
import cn.cangjie.core.event.MsgEvent

/**
 * @author nvwa@cangjie
 * Create by AS at 2020/8/10 09:41
 */
class ScannerViewModel : BaseViewModel() {
    val picScanner: BindingCommand<Any> = BindingCommand(object : BindingAction {
        override fun call() {
            action(MsgEvent(0))
        }
    })
}