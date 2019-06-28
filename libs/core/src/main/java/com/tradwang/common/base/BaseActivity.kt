package com.tradwang.common.base

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import com.qmuiteam.qmui.util.QMUIStatusBarHelper
import com.tradwang.common.R
import com.tradwang.common.dialogFail
import com.tradwang.common.dialogLoading
import com.tradwang.common.dialogSuccess
import java.io.IOException
import java.io.Serializable

open  class BaseActivity : AppCompatActivity() {
    /**
     * 启动一个Activity
     * @param targetActivity Activity 目标Activity
     * @param params Pair<String, Any> 数据源
     * @param requestCode Int 请求码
     */
    fun startActivity(targetActivity: Activity, vararg params: Pair<String, Any>, requestCode: Int = -1) {
        val intent = Intent()
        intent.setClass(this, targetActivity::class.java)
        params.forEach {
            when (val data = it.second) {
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
    }

    fun loadingDialog(loadingMsg: String = resources.getString(R.string.common_loading)): Dialog {
        return dialogLoading(loadingMsg)
    }

    fun successDialog(successMsg: String = resources.getString(R.string.common_loading_success)): Dialog {
        return dialogSuccess(successMsg)
    }

    fun failDialog(failMsg: String = resources.getString(R.string.common_loading_fail)): Dialog {
        return dialogFail(failMsg)
    }

    /**
     * 是否需要沉浸试状态栏
     * @return Boolean need
     */
    protected open fun needFullScreen() = false
}