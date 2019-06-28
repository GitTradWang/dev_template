package com.tradwang.commonstudy

import com.taobao.sophix.SophixManager
import com.tradwang.centre.SP
import com.tradwang.common.base.App

/**
 *  Project Name : CommonStudy
 *  Package Name : com.tradwang.commonstudy
 *  @since 2018/2/26 11: 27
 *  @author : TradWang
 *  @email : trad_wang@sina.com
 *  @version :
 *  @describe :
 */
class StudyApp : App() {

    override fun onAppResumed() {

    }

    override fun onAppPaused() {
        if (SP.needRestart) {
            SophixManager.getInstance().killProcessSafely()
        }
    }

    override fun onCreate() {
        super.onCreate()
//        SophixManager.getInstance().queryAndLoadNewPatch()
    }
}