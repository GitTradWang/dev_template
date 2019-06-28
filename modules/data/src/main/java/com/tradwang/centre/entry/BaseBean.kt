package com.tradwang.centre.entry

/**
 * Project Name : CommonStudy
 * Package Name : com.tradwang.centre.base
 *
 * @author : TradWang
 * @version :
 * @email : trad_wang@sina.com
 * @describe : 所有网络数据Bean的基类
 * @since 2018/2/27 09: 21
 */

data class BaseBean<T> constructor(var errorCode: Int, var errorMsg: String, var data: T?)
