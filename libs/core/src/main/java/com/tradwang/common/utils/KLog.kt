package com.tradwang.common.utils

import android.text.TextUtils
import android.util.Log
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.StringReader
import java.io.StringWriter
import java.util.*
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource

/**
 * <pre>
 * author: xiamin
 * blog : http://jerey.cn
 * desc : 利用Kotlin语法扩展, 一个可控制边框美化,可控制打印等级,可控制Log info, 自动识别类名为Tag, 同时支持自定义Tag的日志工具库
 * </pre>
 */

fun <T> T.logD(hint: String = "") = this.apply { KLog.d(contents = if (hint.isEmpty()) this.toString() else (hint + "║ " + this.toString())) }

fun <T> T.logI(hint: String = "") = this.apply { KLog.i(contents = if (hint.isEmpty()) this.toString() else (hint + "║ " + this.toString())) }

fun <T> T.logW(hint: String = "") = this.apply { KLog.w(contents = if (hint.isEmpty()) this.toString() else (hint + "║ " + this.toString())) }

fun <T> T.logE(hint: String = "") = this.apply { KLog.e(contents = if (hint.isEmpty()) this.toString() else (hint + "║ " + this.toString())) }

fun <T> T.logA(hint: String = "") = this.apply { KLog.a(contents = if (hint.isEmpty()) this.toString() else (hint + "║ " + this.toString())) }

fun <T> T.log() = this.apply { KLog.d(contents = this.toString()) }

class KLog private constructor() {

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }

    /**
     * LogTools设置类
     */
    class Settings {
        /**
         * 设置Log是否开启
         *
         * @param enable
         * @return
         */
        fun setLogEnable(enable: Boolean): Settings {
            KLog.mLogEnable = enable
            return this
        }

        /**
         * 设置打印等级,只有高于该打印等级的log会被打印<br>
         * 打印等级从低到高分别为: Log.VERBOSE < Log.DEBUG < Log.INFO < Log.WARN < Log.ERROR < Log.ASSERT
         *
         * @param logLevel
         */
        fun setLogLevel(logLevel: Int): Settings {
            KLog.mLogFilter = logLevel
            return this
        }

        /**
         * 设置边框是否开启
         *
         * @param enable
         * @return
         */
        fun setBorderEnable(enable: Boolean): Settings {
            KLog.mLogBorderEnable = enable
            return this
        }

        /**
         * 设置Log 行号,方法,class详情信息是否打印的开关
         *
         * @param enable
         * @return
         */
        fun setInfoEnable(enable: Boolean): Settings {
            KLog.mLogInfoEnable = enable
            return this
        }

        /**
         * 获取打印等级
         *
         * @return
         */
        val logLevel: Int
            get() {
                return KLog.mLogFilter
            }
    }

    companion object {
        private const val JSON = -1
        private const val XML = -2
        private const val MAX_LEN = 4000
        private const val TOP_BORDER = "╔═════════════════════════════════════════════════════════════════════════════════════"
        private const val LEFT_BORDER = "║ "
        private const val BOTTOM_BORDER = "╚═════════════════════════════════════════════════════════════════════════════════════"
        //解决windows和linux换行不一致的问题 功能和"\n"是一致的,但是此种写法屏蔽了 Windows和Linux的区别 更保险.
        private val LINE_SEPARATOR = System.getProperty("line.separator")
        private const val mGlobalLogTag = "" // log标签
        private var mLogEnable = true // log总开关
        private var mLogBorderEnable = false // log边框
        private var mLogInfoEnable = false // log详情开关
        private var mLogFilter = Log.VERBOSE // log过滤器

        fun d(contents: Any) {
            log(Log.DEBUG, mGlobalLogTag, contents)
        }

        fun d(tag: String = mGlobalLogTag, contents: Any) {
            log(Log.DEBUG, tag, contents)
        }

        fun i(contents: Any) {
            log(Log.INFO, mGlobalLogTag, contents)
        }

        fun i(tag: String = mGlobalLogTag, contents: Any) {
            log(Log.INFO, tag, contents)
        }

        fun w(contents: Any) {
            log(Log.WARN, mGlobalLogTag, contents)
        }

        fun w(tag: String = mGlobalLogTag, contents: Any) {
            log(Log.WARN, tag, contents)
        }

        fun e(tag: String = mGlobalLogTag, contents: Any) {
            log(Log.ERROR, tag, contents)
        }

        fun a(contents: Any) {
            log(Log.ASSERT, mGlobalLogTag, contents)
        }

        fun a(tag: String = mGlobalLogTag, contents: Any) {
            log(Log.ASSERT, tag, contents)
        }

        fun json(contents: Any) {
            log(JSON, mGlobalLogTag, contents)
        }

        fun json(tag: String = mGlobalLogTag, contents: Any) {
            log(JSON, tag, contents)
        }

        fun xml(contents: Any) {
            log(XML, mGlobalLogTag, contents)
        }

        fun xml(tag: String = mGlobalLogTag, contents: Any) {
            log(XML, tag, contents)
        }

        /**
         * @param type
         * @param tag
         * @param objects
         */
        private fun log(type: Int, tag: String, objects: Any) {
            //全局未开,直接返回
            if (!mLogEnable) {
                return
            }
            val processContents = processObj(type, tag, objects)
            val target = processContents[0]
            val msg = processContents[1]
            when (type) {
                Log.INFO, Log.ASSERT, Log.DEBUG, Log.ERROR, Log.WARN -> if (mLogFilter <= type) {
                    logOut(type, target, msg)
                }
                JSON -> logOut(Log.DEBUG, target, msg)
                XML -> logOut(Log.DEBUG, target, msg)
            }
        }

        private fun processObj(type: Int, tags: String, contents: Any): Array<String> {
            var tag = tags
            var targetElement = Thread.currentThread().stackTrace[5]
            var className = targetElement.className
            if (className.contains("KLog")) {
                targetElement = Thread.currentThread().stackTrace[6]
                className = targetElement.className
            }
            if (className.contains("KLog")) {
                targetElement = Thread.currentThread().stackTrace[7]
                className = targetElement.className
            }
            val classNameInfo = className.split(("\\.").toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (classNameInfo.isNotEmpty()) {
                className = classNameInfo[classNameInfo.size - 1]
            }
            if (className.contains("$")) {
                className = className.split(("\\$").toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
            }
            tag = if (TextUtils.isEmpty(tag) || isSpace(tag)) className else tag
            val head = Formatter().format("Thread: %s, Method: %s (File:%s Line:%d)$LINE_SEPARATOR", Thread.currentThread().name, targetElement.methodName, className, targetElement.lineNumber).toString()
            var msg = contents.toString()
            if (type == JSON) {
                msg = formatJson(msg)
            } else if (type == XML) {
                msg = formatXml(msg)
            }
            if (mLogBorderEnable) {
                val sb = StringBuilder()
                val lines = msg.split((LINE_SEPARATOR).toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                sb.append(TOP_BORDER).append(LINE_SEPARATOR)
                sb.append(LEFT_BORDER).append(head)
                for (line in lines) {
                    sb.append(LEFT_BORDER).append(line).append(LINE_SEPARATOR)
                }
                sb.append(BOTTOM_BORDER).append(LINE_SEPARATOR)
                msg = sb.toString()
                return arrayOf(tag, msg)
            }
            if (mLogInfoEnable) {
                val sb = StringBuilder()
                val lines = msg.split((LINE_SEPARATOR).toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                for (line in lines) {
                    sb.append(line).append(LINE_SEPARATOR)
                }
                msg = sb.toString()
                return arrayOf(tag, head + msg)
            }
            return arrayOf(tag, msg)
        }

        private fun formatJson(json: String): String {
            var ret: String = json
            try {
                if (ret.startsWith("{")) {
                    ret = JSONObject(ret).toString(4)
                } else if (ret.startsWith("[")) {
                    ret = JSONArray(ret).toString(4)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return ret
        }

        private fun formatXml(xml: String): String {
            try {
                val xmlInput = StreamSource(StringReader(xml))
                val xmlOutput = StreamResult(StringWriter())
                val transformer = TransformerFactory.newInstance().newTransformer()
                transformer.setOutputProperty(OutputKeys.INDENT, "yes")
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4")
                transformer.transform(xmlInput, xmlOutput)
                return xmlOutput.writer.toString().replaceFirst((">").toRegex(), ">$LINE_SEPARATOR")
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return xml
        }

        /**
         * 输出log
         *
         * @param type
         * @param tag
         * @param msg
         */
        private fun logOut(type: Int, tag: String, msg: String) {
            val len = msg.length
            val countOfSub = len / MAX_LEN
            if (countOfSub > 0) {
                var index = 0
                var sub: String
                for (i in 0 until countOfSub) {
                    sub = msg.substring(index, index + MAX_LEN)
                    printSubLog(type, tag, sub)
                    index += MAX_LEN
                }
                printSubLog(type, tag, msg.substring(index, len))
            } else {
                printSubLog(type, tag, msg)
            }
        }

        private fun printSubLog(type: Int, tag: String, msg: String) {
            when (type) {
                Log.VERBOSE -> Log.v(tag, msg)
                Log.DEBUG -> Log.d(tag, msg)
                Log.INFO -> Log.i(tag, msg)
                Log.WARN -> Log.w(tag, msg)
                Log.ERROR -> Log.e(tag, msg)
                Log.ASSERT -> Log.wtf(tag, msg)
            }
        }

        private fun isSpace(s: String): Boolean {
            return (0 until s.length).any { Character.isWhitespace(s[it]) }
        }

        /**
         * 设置入口
         * <code>
         * LogTools.getSettings()
         * .setLogLevel(Log.WARN)
         * .setBorderEnable(true)
         * .setLogEnable(true);
         * </code>
         *
         * @return
         */
        fun getSettings(): Settings {
            return Settings()
        }
    }
}