package com.tradwang.common

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.widget.Toast
import com.tradwang.common.base.App
import com.tradwang.common.utils.ToastUtils
import java.text.SimpleDateFormat
import java.util.*

/**
 *  Package Name : com.tradwang.common.base
 *  @since 2017/12/28 18: 36
 *  @author : TradWang
 *  @email : trad_wang@sina.com
 *  @version : 存放扩展函数
 */

fun Int?.safe(i: Int? = null): Int {
    return when {
        this != null -> this
        i != null -> i
        else -> 0
    }
}

fun Float?.safe(fl: Float? = null): Float {
    return when {
        this != null -> this
        fl != null -> fl
        else -> 0f
    }
}

fun String?.safe(s: String? = null): String {
    return when {
        this != null -> this
        s != null -> s
        else -> ""
    }
}

fun Double?.safe(d: Double? = null): Double {
    return when {
        this != null -> this
        d != null -> d
        else -> 0.0
    }
}

fun Boolean?.safe(b: Boolean? = null): Boolean {
    return when {
        this != null -> this
        b != null -> b
        else -> false
    }
}

/**
 *字符串 Toast
 * @receiver String
 */
fun String?.toast(): String {
    if (this == null) return ""
    ToastUtils.showToast(App.getInstance(), this, Toast.LENGTH_SHORT)
    return this
}

/**
 * 获取格式化 保留[decimalLength] 位数的小数点，[intOptimization]true 代表整数不显示小数点
 */
fun Number.formatDecimal(decimalLength: Int = 1, intOptimization: Boolean = true): String {
    if (intOptimization && this.toFloat() - this.toInt() == 0f)
        return this.toInt().toString()
    return String.format("%.${decimalLength}f", this.toDouble())
}


fun Drawable.drawableToBitmap(): Bitmap {
    val bitmap = Bitmap.createBitmap(
            this.intrinsicWidth,
            this.intrinsicHeight,
            if (this.opacity != PixelFormat.OPAQUE)
                Bitmap.Config.ARGB_8888
            else
                Bitmap.Config.RGB_565)
    val canvas = Canvas(bitmap)
    this.setBounds(0, 0, this.intrinsicWidth, this.intrinsicHeight)
    this.draw(canvas)
    return bitmap
}

/**
 * 获取该时间离现在多久
 *
 * @return
 */
fun Date.getTimeSpan(timeStamp: Long = System.currentTimeMillis(), suffix: String = "前", prefix: String = ""): String {
    var timeSpan = timeStamp - this.time
    timeSpan /= 1000
    if (timeSpan < 60)
        return prefix + timeSpan.toString() + "秒$suffix"
    timeSpan /= 60
    if (timeSpan < 60) {
        return prefix + timeSpan.toString() + "分钟$suffix"
    }
    timeSpan /= 60
    if (timeSpan < 24) {
        return prefix + timeSpan.toString() + "小时$suffix"
    }
    timeSpan /= 24
    if (timeSpan < 30) {
        return prefix + timeSpan.toString() + "天$suffix"
    }
    timeSpan = (timeSpan / 30.42).toLong()
    if (timeSpan < 12) {
        return prefix + timeSpan.toString() + "月$suffix"
    }
    timeSpan /= 12
    return prefix + timeSpan.toString() + "年$suffix"
}

/**
 * 获取该时间离现在多久
 *
 * @return
 */
fun Long.getTimeSpan(timeStamp: Long = System.currentTimeMillis(), suffix: String = "前", prefix: String = ""): String {
    var timeSpan = timeStamp - this
    timeSpan /= 1000
    if (timeSpan < 60)
        return prefix + timeSpan.toString() + "秒$suffix"
    timeSpan /= 60
    if (timeSpan < 60) {
        return prefix + timeSpan.toString() + "分钟$suffix"
    }
    timeSpan /= 60
    if (timeSpan < 24) {
        return prefix + timeSpan.toString() + "小时$suffix"
    }
    timeSpan /= 24
    if (timeSpan < 30) {
        return prefix + timeSpan.toString() + "天$suffix"
    }
    timeSpan = (timeSpan / 30.42).toLong()
    if (timeSpan < 12) {
        return prefix + timeSpan.toString() + "月$suffix"
    }
    timeSpan /= 12
    return prefix + timeSpan.toString() + "年$suffix"
}

fun Date.date(char: Char = '/'): String {
    return SimpleDateFormat("yyyy${char}MM${char}dd", Locale.CHINA).format(this)
}

fun Date.year(): String {
    return SimpleDateFormat("yyyy", Locale.CHINA).format(this)
}

fun Date.month(): String {
    return SimpleDateFormat("MM", Locale.CHINA).format(this)
}

fun Date.day(): String {
    return SimpleDateFormat("dd", Locale.CHINA).format(this)
}

fun Date?.formatTime(format: String): String {
    if (this == null)
        return ""
    return SimpleDateFormat(format, Locale.CHINA).format(this)
}

fun Long?.formatTime(format: String): String {
    if (this == null)
        return ""
    return SimpleDateFormat(format, Locale.CHINA).format(Date(this))
}

fun Date.formatTime(format: SimpleDateFormat): String {
    return format.format(this)
}

fun ByteArray.toHexString(): String {
    val hexArray = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')
    val hexChars = CharArray(this.size * 2)
    var v: Int
    for (j in this.indices) {
        v = this[j].toInt() and 0xFF
        hexChars[j * 2] = hexArray[v.ushr(4)]
        hexChars[j * 2 + 1] = hexArray[v and 0x0F]
    }
    return String(hexChars)
}

fun String.hexStringToByteArray(): ByteArray {
    val len = this.length
    val data = ByteArray(len / 2)
    var i = 0
    while (i < len) {
        data[i / 2] = ((Character.digit(this[i], 16) shl 4) + Character.digit(this[i + 1], 16)).toByte()
        i += 2
    }
    return data
}
