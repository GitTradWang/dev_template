package com.tradwang.common.utils

import android.content.Context
import android.widget.Toast


/**
 *  Project Name : CommonStudy
 *  Package Name : com.tradwang.common.utils
 *  @since 2018/3/8 11: 16
 *  @author : TradWang
 *  @email : trad_wang@sina.com
 *  @version :
 *  @describe :
 */
class ToastUtils {

    companion object {

        private var toast: Toast? = null
        private var oneTime: Long = 0L
        private var twoTime: Long = 0L
        private var oldMsg: CharSequence? = null
        private var durationTimeMillis = 0L
        /**
         *@param context  The application to use.  Usually your {@link android.app.Application}
         *  @param msg     The text to show.  Can be formatted text.
         * @param duration How long to display the message.  Either {@link #Toast.LENGTH_SHORT} or {@link #Toast.LENGTH_LONG}
         */
        fun showToast(context: Context, msg: CharSequence, duration: Int) {

            durationTimeMillis = if (duration == Toast.LENGTH_SHORT) 4000L else 7000L

            if (toast == null) {
                oneTime = System.currentTimeMillis()
                toast = Toast.makeText(context, msg, duration)
                toast?.show()
            } else {
                twoTime = System.currentTimeMillis()
                if (msg == oldMsg) {
                    if (twoTime - oneTime > duration) {
                        toast?.show()
                    }
                } else {
                    oldMsg = msg
                    toast?.setText(msg)
                    toast?.show()
                }
            }
            oneTime = twoTime
        }
    }
}