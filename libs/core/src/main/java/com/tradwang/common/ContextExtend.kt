package com.tradwang.common

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.ContextWrapper
import android.view.View
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog

fun View.activity(): Activity? {
    return this.context.activity()
}

fun Dialog.activity(): Activity? {
    return this.context.activity()
}

fun Activity.dialogLoading(loadingMsg: String = this.resources.getString(R.string.common_loading)): Dialog {
    return QMUITipDialog.Builder(this).setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING).setTipWord(loadingMsg).create()
}

fun Activity.dialogSuccess(successMsg: String = resources.getString(R.string.common_loading_success)): Dialog {
    return QMUITipDialog.Builder(this).setIconType(QMUITipDialog.Builder.ICON_TYPE_SUCCESS).setTipWord(successMsg).create()
}

fun Activity.dialogFail(failMsg: String = resources.getString(R.string.common_loading_fail)): Dialog {
    return QMUITipDialog.Builder(this).setIconType(QMUITipDialog.Builder.ICON_TYPE_FAIL).setTipWord(failMsg).create()
}

fun Activity.dialogInfo(msg: String): Dialog {
    return QMUITipDialog.Builder(this).setIconType(QMUITipDialog.Builder.ICON_TYPE_INFO).setTipWord(msg).create()
}

fun Activity.dialogTip(msg: String): Dialog {
    return QMUITipDialog.Builder(this).setIconType(QMUITipDialog.Builder.ICON_TYPE_NOTHING).setTipWord(msg).create()
}


fun Context.activity(): Activity? {
    var _context = this
    while (_context is ContextWrapper) {
        if (_context is Activity) return _context
        _context = _context.baseContext
    }
    return null
}
