package com.tradwang.centre

import com.tradwang.common.base.App
import com.tradwang.common.utils.Preference

/**
 * ProjectName   CommonStudy
 * PackageName  com.tradwang.commonstudy
 * @author  tradwang
 * @since   18-7-20   上午10:33.
 * @email  wangxiaojun@bianla.cn
 * @version  0.1
 * @describe  TODO
 */
object SP {
    var needRestart by Preference(App.getInstance(), false)
    var loginUser by Preference(App.getInstance(), "")
}