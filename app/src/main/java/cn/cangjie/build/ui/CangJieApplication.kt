package cn.cangjie.build.ui

import androidx.multidex.MultiDexApplication
import cn.cangjie.build.R
import cn.cangjie.build.ui.model.UpdateModel
import cn.cangjie.build.ui.utils.OkHttp3Connection
import cn.cangjie.core.utils.ThemeHelper
import cn.cangjie.uikit.update.model.TypeConfig
import cn.cangjie.uikit.update.model.UpdateConfig
import cn.cangjie.uikit.update.utils.AppUpdateUtils
import cn.cangjie.uikit.update.utils.SSLUtils
import com.cangjie.player.BuildConfig
import com.cangjie.player.core.player.CJPlayerFactory
import com.cangjie.player.core.player.VideoViewConfig
import com.cangjie.player.core.player.VideoViewManager
import com.hjq.toast.ToastUtils
import com.hjq.toast.style.ToastBlackStyle
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier

/**
 * @author nvwa@cangjie
 * Create by AS at 2020/7/16 14:30
 */
class CangJieApplication : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        val builder: OkHttpClient.Builder = OkHttpClient.Builder()
        builder.connectTimeout(30000, TimeUnit.SECONDS)
            .readTimeout(30000, TimeUnit.SECONDS)
            .writeTimeout(30000, TimeUnit.SECONDS)
            .sslSocketFactory(
                SSLUtils.getSslSocketFactory().sSLSocketFactory,
                SSLUtils.getSslSocketFactory().trustManager
            )
            .hostnameVerifier(HostnameVerifier { _, _ -> true })
            .retryOnConnectionFailure(true)

        val updateConfig: UpdateConfig = UpdateConfig()
            .setDataSourceType(TypeConfig.DATA_SOURCE_TYPE_MODEL)
            .setShowNotification(true) //配置更新的过程中是否在通知栏显示进度
            .setNotificationIconRes(R.mipmap.download_icon) //配置通知栏显示的图标
            .setUiThemeType(TypeConfig.UI_THEME_L)
            .setAutoDownloadBackground(false)
            .setNeedFileMD5Check(false)
            .setCustomDownloadConnectionCreator(OkHttp3Connection.Creator(builder))
            .setModelClass(UpdateModel())
        AppUpdateUtils.init(this, updateConfig)
        ToastUtils.init(this, ToastBlackStyle(this))
        ThemeHelper.applyTheme(ThemeHelper.DEFAULT_MODE)
        //播放器配置，注意：此为全局配置，按需开启
        VideoViewManager.setConfig(
            VideoViewConfig.newBuilder()
                .setLogEnabled(BuildConfig.DEBUG)
                .setPlayerFactory(CJPlayerFactory.create())
                .build()
        )
    }
}