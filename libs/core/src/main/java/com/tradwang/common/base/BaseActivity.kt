package com.tradwang.common.base

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import com.qmuiteam.qmui.util.QMUIStatusBarHelper
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog
import com.tradwang.common.R
import java.io.IOException
import java.io.Serializable

/**
 *  Project Name : CommonStudy
 *  Package Name : com.tradwang.common.base
 *  @since 2018/2/11 13: 28
 *  @author : TradWang
 *  @email : trad_wang@sina.com
 *  @version :
 *  @describe :
 */
abstract class BaseActivity : AppCompatActivity() {

    private val netWorkReceiver by lazy { NetWorkReceiver() }

    private val connectivityManager by lazy { this@BaseActivity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager }

    private val defaultNetworkCallback by lazy { DefaultNetworkCallback() }

    /**
     * 启动一个Activity
     * @param targetActivity Activity 目标Activity
     * @param mutableList MutableList<Pair<String, Any>>? 数据源
     * @param requestCode Int 请求码
     */
    fun startActivity(targetActivity: Activity, mutableList: MutableList<Pair<String, Any>>? = null, requestCode: Int = -1) {
        val intent = Intent()
        intent.setClass(this, targetActivity::class.java)
        mutableList?.forEach {
            val data = it.second
            when (data) {
                is Boolean -> intent.putExtra(it.first, data)
                is Int -> intent.putExtra(it.first, data)
                is String -> intent.putExtra(it.first, data)
                is Char -> intent.putExtra(it.first, data)
                is Float -> intent.putExtra(it.first, data)
                is Double -> intent.putExtra(it.first, data)
                is Serializable -> intent.putExtra(it.first, data)
                is Parcelable -> intent.putExtra(it.first, data)
                else -> throw IOException("params is not support")
            }
        }
        if (requestCode != -1) {
            this.startActivityForResult(intent, requestCode)
        } else {
            this.startActivity(intent)
        }
    }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (needFullScreen()) {
            QMUIStatusBarHelper.translucent(this)
        }
        if (needCheckNetWork()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                connectivityManager.registerDefaultNetworkCallback(defaultNetworkCallback)
            } else {
                registerReceiver(netWorkReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
            }
        }
    }

    fun loadingDialog(loadingMsg: String = resources.getString(R.string.common_loading)): QMUITipDialog {
        return QMUITipDialog.Builder(this).setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING).setTipWord(loadingMsg).create()
    }

    fun successDialog(successMsg: String = resources.getString(R.string.common_loading_success)): QMUITipDialog {
        return QMUITipDialog.Builder(this).setIconType(QMUITipDialog.Builder.ICON_TYPE_SUCCESS).setTipWord(successMsg).create()
    }

    fun failDialog(failMsg: String = resources.getString(R.string.common_loading_fail)): QMUITipDialog {
        return QMUITipDialog.Builder(this).setIconType(QMUITipDialog.Builder.ICON_TYPE_FAIL).setTipWord(failMsg).create()
    }

    fun infoDialog(msg: String): QMUITipDialog {
        return QMUITipDialog.Builder(this).setIconType(QMUITipDialog.Builder.ICON_TYPE_INFO).setTipWord(msg).create()
    }

    fun tipDialog(msg: String): QMUITipDialog {
        return QMUITipDialog.Builder(this).setIconType(QMUITipDialog.Builder.ICON_TYPE_NOTHING).setTipWord(msg).create()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (needCheckNetWork()) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                connectivityManager.unregisterNetworkCallback(defaultNetworkCallback)
            } else {
                unregisterReceiver(netWorkReceiver)
            }
        }
    }

    /**
     *网络失去连接的时候会调用
     */
    protected fun onNetDisconnect() {

    }

    /**
     * 网路连接时候调用
     */
    protected fun onNetConnect() {

    }

    /**
     * 是否需要监听网络连接状态
     * @return Boolean 是否需要
     */
    protected open fun needCheckNetWork() = true

    /**
     * 是否需要沉浸试状态栏
     * @return Boolean need
     */
    protected open fun needFullScreen() = false

    inner class DefaultNetworkCallback : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network?) {
            super.onAvailable(network)
            onNetConnect()
        }

        override fun onUnavailable() {
            super.onUnavailable()
            onNetDisconnect()
        }
    }

    inner class NetWorkReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            //获得ConnectivityManager对象
            val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
            if (connMgr?.activeNetworkInfo?.isConnected == true) {
                //网络连接
                onNetConnect()
            } else {
                //无网络链接
                onNetDisconnect()
            }
        }
    }

}