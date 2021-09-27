package cn.cangjie.build.ui.scanner

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.MotionEvent
import cn.cangjie.build.BR
import cn.cangjie.build.R
import cn.cangjie.build.databinding.ActivityScannerBinding
import cn.cangjie.core.BaseMvvmActivity
import cn.cangjie.core.event.MsgEvent
import cn.cangjie.core.requestPermissions
import cn.cangjie.core.utils.LogUtils
import cn.cangjie.uikit.scanner.CaptureHelper
import cn.cangjie.uikit.scanner.DecodeFormatManager
import cn.cangjie.uikit.scanner.OnCaptureCallback
import cn.cangjie.uikit.scanner.util.CodeUtils.parseCode
import cn.cangjie.uikit.toast.show
import duoge.work.base.scanner.UriUtils
import org.jetbrains.anko.alert

/**
 * @author nvwa@cangjie
 * Create by AS at 2020/8/10 09:39
 */
class ScannerActivity : BaseMvvmActivity<ActivityScannerBinding, ScannerViewModel>(),
    OnCaptureCallback {
    private var mCaptureHelper: CaptureHelper? = null


    override fun initActivity(savedInstanceState: Bundle?) {
        title = "扫一扫"
        mCaptureHelper =
            CaptureHelper(
                this@ScannerActivity,
                mBinding.surfaceView,
                mBinding.viewfinderView,
                mBinding.ivTorch
            )
        mCaptureHelper!!.setOnCaptureCallback(this)
        mCaptureHelper!!.onCreate()
        mCaptureHelper!!
            .decodeFormats(DecodeFormatManager.QR_CODE_FORMATS)
            .playBeep(true)
            .vibrate(true)

    }

    override fun initVariableId(): Int = BR.scanModel

    override fun layoutId(): Int = R.layout.activity_scanner

    override fun handleEvent(msg: MsgEvent) {
        super.handleEvent(msg)
        requestPermissions {
            putPermissions(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )

            onAllPermissionsGranted = {
                val pickIntent = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
                pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
                startActivityForResult(pickIntent, REQUEST_CODE_PHOTO)
            }

            onPermissionsNeverAsked = { toAppSettings() }

            onPermissionsDenied = { toAppSettings() }

            onShowRationale = { request ->
                alert("必要权限，请务必同意o(╥﹏╥)o", "温馨提示") {
                    positiveButton("行，给你~") { request.retryRequestPermissions() }
                    negativeButton("不，我不玩了！") {}
                }.show()
            }
        }

    }

    private fun toAppSettings() {
        alert("缺少必要权限, 是否手动打开^_^", "温馨提示") {
            positiveButton("去设置") {
                startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                })
            }
            negativeButton("拒绝") {}
        }.show()
    }

    public override fun onResume() {
        super.onResume()
        mCaptureHelper!!.onResume()
    }

    public override fun onPause() {
        super.onPause()
        mCaptureHelper!!.onPause()
    }

    public override fun onDestroy() {
        super.onDestroy()
        mCaptureHelper!!.onDestroy()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        mCaptureHelper!!.onTouchEvent(event)

        return super.onTouchEvent(event)
    }

    override fun onResultCallback(result: String?): Boolean {
        show(this, 2000, result!!)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                REQUEST_CODE_PHOTO -> parsePhoto(data)
            }
        }
    }

    private fun parsePhoto(data: Intent) {
        val path = UriUtils.getImagePath(this, data)
        if (path.isNullOrEmpty()) {
            return
        }
        asyncThread(Runnable {
            val result = parseCode(path)
            runOnUiThread {
//                show(this@ScannerActivity, 2000, result!!)
            }
        })
    }

    private fun asyncThread(runnable: Runnable) {
        Thread(runnable).start()
    }

    companion object {
        const val REQUEST_CODE_PHOTO = 0X02
    }

}