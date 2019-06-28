package com.tradwang.commonstudy

/**
 * ProjectName   CommonStudy
 * PackageName  com.tradwang.commonstudy
 * @author  tradwang
 * @since   18-9-13   下午5:53.
 * @email  wangxiaojun@bianla.cn
 * @version  0.1
 * @describe  TODO
 */
data class ApiConfig(var server: String? = null, var h5: String? = null, var debugConfig: DebugConfig? = null)

data class DebugConfig(var debug: Boolean = false, var treeSource: Boolean = true)