package com.tradwang.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations


/**
 * Transformations map 扩展类
 * @receiver LiveData<D>   源 live data
 * @param apply (D) -> T 转换器
 * @return LiveData<T> 新 live data
 */
fun <D, T> LiveData<D>.map(apply: (D) -> T): LiveData<T> {
    return Transformations.map(this) { apply.invoke(it) }
}

/**
 * Transformations switchMap 扩展类
 * @receiver LiveData<D>  源 live data
 * @param apply (D) -> LiveData<T> 转换器
 * @return LiveData<T> 新的live data
 */
fun <D, T> LiveData<D>.switchMap(apply: (D) -> LiveData<T>): LiveData<T> {
    return Transformations.switchMap(this) { input -> apply.invoke(input) }
}