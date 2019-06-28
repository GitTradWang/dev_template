package com.tradwang.commonstudy

import android.content.Context
import androidx.annotation.Keep
import androidx.multidex.MultiDex
import com.taobao.sophix.PatchStatus
import com.taobao.sophix.SophixApplication
import com.taobao.sophix.SophixEntry
import com.taobao.sophix.SophixManager
import com.tradwang.centre.SP
import com.tradwang.common.utils.logI

/**
 * Sophix入口类，专门用于初始化Sophix，不应包含任何业务逻辑。
 * 此类必须继承自SophixApplication，onCreate方法不需要实现。
 * 此类不应与项目中的其他类有任何互相调用的逻辑，必须完全做到隔离。
 * AndroidManifest中设置application为此类，而SophixEntry中设为原先Application类。
 * 注意原先Application里不需要再重复初始化Sophix，并且需要避免混淆原先Application类。
 * 如有其它自定义改造，请咨询官方后妥善处理。
 */
class SophixStubApplication : SophixApplication() {
    // 此处SophixEntry应指定真正的Application，并且保证RealApplicationStub类名不被混淆。
    @Keep
    @SophixEntry(StudyApp::class)
    internal class RealApplicationStub

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        //         如果需要使用MultiDex，需要在此处调用。
        MultiDex.install(this)
        initSophix()
    }

    private fun initSophix() {
        var appVersion = "0.0.0"
        try {
            appVersion = this.packageManager.getPackageInfo(this.packageName, 0).versionName
        } catch (ignored: Exception) {
        }

        val instance = SophixManager.getInstance()
        instance.setContext(this)
                .setAppVersion(appVersion)
                .setSecretMetaData(null, null, null)
                .setEnableDebug(true)
                .setEnableFullLog()
                .setPatchLoadStatusStub { mode, code, info, handlePatchVersion ->

                    "mode : $mode    info : $info  handlePatchVersion : $handlePatchVersion".logI()

                    if (code == PatchStatus.CODE_LOAD_SUCCESS) {
                        // 加载成功
                    } else if (code == PatchStatus.CODE_LOAD_RELAUNCH) {
                        // 如果需要在后台重启，建议此处用SharePreference保存状态。
                        SP.needRestart = true
                    }
                }.initialize()
    }
}