package com.tradwang.commonstudy

/**
 * ProjectName   CommonStudy
 * PackageName  com.tradwang.commonstudy
 * @author  tradwang
 * @since   18-9-13   下午5:52.
 * @email  wangxiaojun@bianla.cn
 * @version  0.1
 * @describe  TODO
 */

fun appConfig(block: ApiConfig.() -> Unit): ApiConfig = ApiConfig().apply(block)

fun ApiConfig.debugConfig(block: DebugConfig.() -> Unit) {
    debugConfig = DebugConfig().apply(block)
}
