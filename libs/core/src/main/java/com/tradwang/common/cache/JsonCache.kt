package com.tradwang.common.cache

import android.util.Base64
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tradwang.common.base.App
import com.tradwang.common.utils.log
import java.io.File
import java.lang.ref.WeakReference
import java.math.BigInteger
import java.nio.charset.Charset
import java.security.MessageDigest
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * ProjectName   CommonStudy
 * @author  tradwang
 * @since   18-11-5   下午2:03.
 * @email  wangxiaojun@bianla.cn
 * @version  0.1
 * @describe
 * @param T 需要缓存的类型
 * @property typeToken 用于gson解析
 * @property name 缓存名称若null则用属性名缓存
 * @property path  缓存的路径
 * @property weakMemoryCache 缓存
 * @property gson gson
 * @constructor
 */
class JsonCache<T>(private val typeToken: TypeToken<T>, private val path: String? = null, private val name: String? = null) : ReadWriteProperty<Any?, T?> {

    private var weakMemoryCache: WeakReference<T>? = null

    private val gson by lazy { Gson() }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T? {
        return if (name == null) {
            getCache(property.name)
        } else {
            getCache(name)
        }
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
        if (name == null) {
            setCache(property.name, value)
        } else {
            setCache(name, value)
        }
    }

    /**
     * 获取缓存数据
     * @param name 缓存名称
     * @return T? 数据源
     */
    @Synchronized
    private fun getCache(name: String): T? {

        var value = weakMemoryCache?.get()

        if (value != null) {
            return value
        } else {
            value = getByDisk(name)
            if (value != null) {
                weakMemoryCache = WeakReference(value)
            }
        }
        return value
    }

    /**
     * 保存缓存
     * @param name 缓存名称
     * @param value T? 数据源
     */
    @Synchronized
    private fun setCache(name: String, value: T?) {
        weakMemoryCache = null
        if (value != null) {
            weakMemoryCache = WeakReference(value)
            setToDisk(name, value)
        }
    }

    /**
     * 缓存到磁盘上
     * @param name String
     * @param value T?
     */
    @Synchronized
    private fun setToDisk(name: String, value: T?) {
        if (value != null) {
            try {
                encodeByMD5(name)?.let {
                    val bufferedWriter = getCacheFile(it).bufferedWriter()
                    bufferedWriter.write(encodeByBase64(gson.toJson(value)))
                    bufferedWriter.flush()
                    bufferedWriter.close()
                }
            } catch (e: Exception) {
                e.toString().log()
            }
        }
    }

    /**
     * 从磁盘上获取数据
     * @param name  缓存的名字
     * @return T? 获取到的数据
     */
    @Synchronized
    private fun getByDisk(name: String): T? {
        return try {
            val encodeByMD5 = encodeByMD5(name)
            return if (encodeByMD5 == null) {
                null
            } else {
                val file = getCacheFile(encodeByMD5)
                if (file.exists()) {
                    gson.fromJson<T>(decodeByBase64(file.readText()), typeToken.type)
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            e.toString().log()
            null
        }
    }

    /**
     * 用md5的算法进行加密
     */
    private fun encodeByMD5(plainText: String): String? {
        return try {
            val secretBytes = MessageDigest.getInstance("md5").digest(plainText.toByteArray())
            // 16进制数字
            var md5code = BigInteger(1, secretBytes).toString(16)
            // 如果生成数字未满32位，需要前面补0
            for (i in 0 until 32 - md5code.length) {
                md5code = "0$md5code"
            }
            return md5code.toUpperCase()
        } catch (e: Exception) {
            e.toString().log()
            null
        }
    }

    /**
     * base64加密
     */
    private fun encodeByBase64(plainText: String): String {
        return String(Base64.encode(plainText.toByteArray(Charset.defaultCharset()), Base64.URL_SAFE), Charset.defaultCharset())
    }

    /**
     * base64解密
     */
    private fun decodeByBase64(plainText: String): String {
        return String(Base64.decode(plainText, Base64.URL_SAFE), Charset.defaultCharset());
    }

    /**
     * 获取缓存的路径
     * @param fileName 缓存文件名
     * @return 缓存的File对象
     */
    private fun getCacheFile(fileName: String): File {
        return if (path == null) {
            getDefaultCacheFile(fileName)
        } else {
            return if (path.trim().endsWith("/", true)) {
                File("$path$fileName")
            } else {
                File("$path/$fileName")
            }
        }
    }

    /**
     * 获取默认的缓存路径
     * @param fileName 缓存文件名
     * @return 缓存的File对象
     */
    private fun getDefaultCacheFile(fileName: String): File {
        val filesDir = File(filesPath)
        if (!filesDir.exists()) {
            filesDir.mkdirs()
        }
        return File(filesDir, fileName)
    }

    companion object {

        private var filesPath: String

        init {
            val filesDir = File(App.getInstance().cacheDir, "cache")
            if (!filesDir.exists()) {
                filesDir.mkdirs()
            }
            filesPath = filesDir.absolutePath
        }

        private fun mkDir() {
            val filesDir = File(App.getInstance().cacheDir, "cache")
            if (!filesDir.exists()) {
                filesDir.mkdirs()
            }
            filesPath = filesDir.absolutePath
        }

        @Synchronized
        fun cleanDefaultCache() {
            File(filesPath).deleteRecursively()
            mkDir()
        }
    }
}