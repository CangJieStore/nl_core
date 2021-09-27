package cn.cangjie.build.ui.model

import cn.cangjie.uikit.update.model.LibraryUpdateEntity

class UpdateModel : LibraryUpdateEntity {
    var isForceUpdate = 0
    var preBaselineCode = 0
    var versionName: String? = null
    var versionCode = 0
    var downurl: String? = null
    var updateLog: String? = null
    var size: String? = null
    var hasAffectCodes: String? = null
    var createTime: Long = 0
    var iosVersion = 0
    override fun getAppVersionCode(): Int {
        return versionCode
    }

    override fun forceAppUpdateFlag(): Int {
        return isForceUpdate
    }

    override fun getAppVersionName(): String {
        return versionName!!.replaceFirst("v".toRegex(), "")
    }

    override fun getAppApkUrls(): String {
        return downurl!!
    }

    override fun getAppUpdateLog(): String {
        return updateLog!!
    }

    override fun getAppApkSize(): String {
        return size!!
    }

    override fun getAppHasAffectCodes(): String {
        return hasAffectCodes!!
    }

    override fun getFileMd5Check(): String {
        return ""
    }
}