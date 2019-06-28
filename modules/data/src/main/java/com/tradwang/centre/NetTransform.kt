package com.tradwang.centre

import android.accounts.NetworkErrorException
import com.tradwang.centre.entry.BaseBean
import com.tradwang.common.safe
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * ProjectName   CommonStudy
 * PackageName  com.tradwang.common
 * @author  tradwang
 * @since   18-7-31   下午3:54.
 * @email  wangxiaojun@bianla.cn
 * @version  0.1
 * @describe  TODO
 */
class NetTransform<T> : ObservableTransformer<BaseBean<T>, BaseBean<T>> {
    override fun apply(upstream: Observable<BaseBean<T>>): ObservableSource<BaseBean<T>> {
        return upstream.flatMap { data ->
            return@flatMap Observable.create<BaseBean<T>> {
                val message = checkData(data)
                if (message == null) {
                    it.onNext(data)
                    it.onComplete()
                } else {
                    it.onError(IOException(message))
                    it.onComplete()
                }
            }
        }
    }

    private fun checkData(bean: BaseBean<T>): String? {
        return if (bean.errorCode == 0) {
            null
        } else {
            bean.errorMsg
        }
    }
}

fun Throwable.netError(onApiLoadError: (msg: String, isNetError: Boolean) -> Unit = { _, _ -> }): String {
    var message = ""
    val errorMessage: (msg: String, isNetError: Boolean) -> Unit = { msg, b ->
        message = msg
        onApiLoadError(msg, b)
    }
    when (this) {
        is SocketTimeoutException -> errorMessage("time error ", true)
        is NetworkErrorException -> errorMessage("net error", true)
        is UnknownHostException -> errorMessage("host error", true)
        is ConnectException -> errorMessage("connect error", true)
        is HttpException -> {
            errorMessage(when (this.code()) {
                400 -> "Bad Request 请求出现语法错误"
                401 -> "Unauthorized 访问被拒绝"
                403 -> "Forbidden 资源不可用"
                404 -> "Not Found 无法找到指定位置的资源"
                405 -> "Method Not Allowed 请求方法（GET、POST、HEAD、Delete、PUT、TRACE等）对指定的资源不适用"
                in 406..499 -> "客户端错误"
                500 -> "Internal Server Error 服务器遇到了意料不到的情况，不能完成客户的请求"
                502 -> "Bad Gateway .Web 服务器用作网关或代理服务器时收到了无效响应"
                503 -> "Service Unavailable 服务不可用,服务器由于维护或者负载过重未能应答"
                504 -> "Gateway Timeout 网关超时"
                505 -> "HTTP Version Not Supported 服务器不支持请求中所指明的HTTP版本"
                in 506..599 -> "服务端错误"
                else -> this.message().safe()
            }, true)
        }
        is IOException -> {
            val msg = this.message.safe()
            errorMessage(msg, false)
        }
        else -> errorMessage(this.message.safe(), false)
    }
    return message
}