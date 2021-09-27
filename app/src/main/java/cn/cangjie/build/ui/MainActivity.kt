package cn.cangjie.build.ui

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import cn.cangjie.build.R
import cn.cangjie.build.databinding.ActivityMainBinding
import cn.cangjie.build.ui.model.ListModel
import cn.cangjie.build.ui.scanner.ScannerActivity
import cn.cangjie.core.BaseMvvmActivity
import cn.cangjie.core.requestPermissions
import cn.cangjie.uikit.pickerview.builder.TimePickerBuilder
import cn.cangjie.uikit.pickerview.listener.CustomListener
import cn.cangjie.uikit.pickerview.listener.OnTimeSelectListener
import cn.cangjie.uikit.pickerview.view.TimePickerView
import cn.cangjie.uikit.update.model.DownloadInfo
import cn.cangjie.uikit.update.model.TypeConfig
import cn.cangjie.uikit.update.utils.AppUpdateUtils
import com.cangjie.player.core.player.VideoView
import com.cangjie.player.ui.*
import com.gyf.immersionbar.ktx.immersionBar
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.alert
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : BaseMvvmActivity<ActivityMainBinding, MainViewModel>() {
    private var pvCustomLunar: TimePickerView? = null


    private fun update() {
        AppUpdateUtils.getInstance().clearAllData()
        val listModel = ListModel()
        listModel.isForceUpdate = false
        listModel.uiTypeValue = TypeConfig.UI_THEME_L
        listModel.isCheckFileMD5 = true
        listModel.sourceTypeVaule = TypeConfig.DATA_SOURCE_TYPE_MODEL
        val info =
            DownloadInfo().setApkUrl("http://jokesimg.cretinzp.com/apk/app-release_231_jiagu_sign.apk")
                .setFileSize(31338250)
                .setProdVersionCode(25)
                .setProdVersionName("2.3.1")
                .setMd5Check("68919BF998C29DA3F5BD2C0346281AC0")
                .setForceUpdateFlag(if (listModel.isForceUpdate) 1 else 0)
                .setUpdateLog("1、优化细节和体验，更加稳定\n2、引入大量优质用户\r\n3、修复已知bug\n4、风格修改")
        AppUpdateUtils.getInstance().updateConfig.uiThemeType = listModel.uiTypeValue
        AppUpdateUtils.getInstance().updateConfig.isNeedFileMD5Check = true
        AppUpdateUtils.getInstance().updateConfig.dataSourceType = listModel.sourceTypeVaule
        AppUpdateUtils.getInstance().updateConfig.isAutoDownloadBackground =
            listModel.isAutoUpdateBackground
        AppUpdateUtils.getInstance().updateConfig.isShowNotification =
            !listModel.isAutoUpdateBackground
        AppUpdateUtils.getInstance().checkUpdate(info)
    }

    override fun initVariableId(): Int = 0

    override fun layoutId(): Int = R.layout.activity_main
    override fun initActivity(savedInstanceState: Bundle?) {
        initLunarPicker()
        Log.e("toolbar", (mToolBar == null).toString())
        initPlayer()
//        update()
        btn_scanner.setOnClickListener {
            pvCustomLunar?.show()
            requestPermissions {
                putPermissions(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                )

                onAllPermissionsGranted = {
                    startActivity(Intent(this@MainActivity, ScannerActivity::class.java))
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
    }

    private fun initPlayer() {
        val controller = StandardVideoController(this)
        //根据屏幕方向自动进入/退出全屏
        controller.setEnableOrientation(true)
        val prepareView = PrepareView(this) //准备播放界面
        controller.addControlComponent(prepareView)
        controller.addControlComponent(CompleteView(this)) //自动完成播放界面
        controller.addControlComponent(ErrorView(this)) //错误界面
        val titleView = TitleView(this) //标题栏
        controller.addControlComponent(titleView)
        //根据是否为直播设置不同的底部控制条
        val isLive = false
        if (isLive) {
            controller.addControlComponent(LiveControlView(this)) //直播控制条
        } else {
            val vodControlView = VodControlView(this) //点播控制条
            controller.addControlComponent(vodControlView)
        }
        val gestureControlView = GestureView(this) //滑动控制视图
        controller.addControlComponent(gestureControlView)
        //根据是否为直播决定是否需要滑动调节进度
        controller.setCanChangePosition(!isLive)
        //设置标题
        val title = "test"
        titleView.setTitle(title)
        player!!.setVideoController(controller)

        player!!.setUrl("http://vfx.mtime.cn/Video/2019/03/14/mp4/190314223540373995.mp4")
        player!!.addOnStateChangeListener(mOnStateChangeListener)
        player!!.start()
    }

    private val mOnStateChangeListener: VideoView.OnStateChangeListener =
        object : VideoView.SimpleOnStateChangeListener() {
            override fun onPlayerStateChanged(playerState: Int) {
                when (playerState) {
                    VideoView.PLAYER_NORMAL -> {
                    }
                    VideoView.PLAYER_FULL_SCREEN -> {
                    }
                }
            }

            override fun onPlayStateChanged(playState: Int) {
                when (playState) {
                    VideoView.STATE_IDLE -> {
                    }
                    VideoView.STATE_PREPARING -> {
                    }
                    VideoView.STATE_PREPARED -> {
                    }
                    VideoView.STATE_PLAYING -> {
                        var videoSize: IntArray? = player!!.videoSize
                    }                    //需在此时获取视频宽高

                    VideoView.STATE_PAUSED -> {
                    }
                    VideoView.STATE_BUFFERING -> {
                    }
                    VideoView.STATE_BUFFERED -> {
                    }
                    VideoView.STATE_PLAYBACK_COMPLETED -> {
                    }
                    VideoView.STATE_ERROR -> {
                    }
                }
            }
        }
    private var i = 0
    fun onButtonClick(view: View) {
        when (view.id) {
            R.id.scale_default -> player!!.setScreenScaleType(VideoView.SCREEN_SCALE_DEFAULT)
            R.id.scale_169 -> player!!.setScreenScaleType(VideoView.SCREEN_SCALE_16_9)
            R.id.scale_43 -> player!!.setScreenScaleType(VideoView.SCREEN_SCALE_4_3)
            R.id.scale_original -> player!!.setScreenScaleType(VideoView.SCREEN_SCALE_ORIGINAL)
            R.id.scale_match_parent -> player!!.setScreenScaleType(VideoView.SCREEN_SCALE_MATCH_PARENT)
            R.id.scale_center_crop -> player!!.setScreenScaleType(VideoView.SCREEN_SCALE_CENTER_CROP)
            R.id.speed_0_5 -> player!!.speed = 0.5f
            R.id.speed_0_75 -> player!!.speed = 0.75f
            R.id.speed_1_0 -> player!!.speed = 1.0f
            R.id.speed_1_5 -> player!!.speed = 1.5f
            R.id.speed_2_0 -> player!!.speed = 2.0f
            R.id.screen_shot -> {
                val imageView = findViewById<ImageView>(R.id.iv_screen_shot)
                val bitmap = player!!.doScreenShot()
                imageView.setImageBitmap(bitmap)
            }
            R.id.mirror_rotate -> {
                player!!.setMirrorRotation(i % 2 == 0)
                i++
            }
            R.id.btn_mute -> player!!.isMute = true
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

    override fun subscribeModel(model: MainViewModel) {
        super.subscribeModel(model)
    }

    override fun onResume() {
        super.onResume()
        player!!.resume()
    }

    override fun onPause() {
        super.onPause()
        player!!.pause()
    }

    override fun onStop() {
        super.onStop()
        player!!.release()
    }

    private fun initLunarPicker() {
        val selectedDate = Calendar.getInstance() //系统当前时间
        val startDate = Calendar.getInstance()
        startDate[2014, 1] = 23
        val endDate = Calendar.getInstance()
        endDate[2069, 2] = 28
        //时间选择器 ，自定义布局
        pvCustomLunar = TimePickerBuilder(this, object : OnTimeSelectListener {
            override fun onTimeSelect(date: Date?, v: View?) {
                Toast.makeText(this@MainActivity, getTime(date!!), Toast.LENGTH_SHORT).show()
            }
        }).setDate(selectedDate)
            .setRangDate(startDate, endDate)
            .setLayoutRes(R.layout.pickerview_custom_lunar, object : CustomListener {
                override fun customLayout(v: View?) {
                    val tvSubmit = v!!.findViewById<View>(R.id.tv_finish) as TextView
                    val ivCancel = v.findViewById<View>(R.id.iv_cancel) as AppCompatTextView
                    tvSubmit.setOnClickListener {
                        pvCustomLunar!!.returnData()
                        pvCustomLunar!!.dismiss()
                    }
                    ivCancel.setOnClickListener { pvCustomLunar!!.dismiss() }
                }
            })
            .setType(booleanArrayOf(true, true, true, false, false, false))
            .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
            .setDividerColor(Color.RED)
            .build()
    }

    private fun getTime(date: Date): String { //可根据需要自行截取数据显示
        Log.d("getTime()", "choice date millis: " + date.time)
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return format.format(date)
    }
}