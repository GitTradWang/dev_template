package com.tradwang.common.base

/**
 * ProjectName   CommonStudy
 * PackageName  com.tradwang.common.base
 * @author  tradwang
 * @since   18-7-20   上午10:52.
 * @email  wangxiaojun@bianla.cn
 * @version  0.1
 * @describe  app监听
 */
interface AppLifeCallBack {
    fun onAppResumed()

    fun onAppPaused()
}