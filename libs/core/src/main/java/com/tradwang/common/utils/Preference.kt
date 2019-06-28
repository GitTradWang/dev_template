package com.tradwang.common.utils

import android.app.Application
import android.content.Context
import java.lang.UnsupportedOperationException
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * kotlin 属性代理方式处理SharePreference
 */
class Preference<T>(val application: Application, var default: T) : ReadWriteProperty<Any?, T> {

    val appName =application.packageName.replace('.','_')

    private val prefs by lazy {
        application.getSharedPreferences(appName, Context.MODE_PRIVATE)
    }

    private var mValue: T? = null

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        if (mValue == null) {
            mValue = findPreference(property.name, default)
        }
        return mValue!!
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        if (mValue != value) {
            putPreference(property.name, value)
        }
        mValue = value
    }

    private fun <U> findPreference(name: String, default: U): U = with(prefs) {
        val res: Any = when (default) {
            is Long -> getLong(name, default)
            is String -> getString(name, default)
            is Int -> getInt(name, default)
            is Boolean -> getBoolean(name, default)
            is Float -> getFloat(name, default)
            else -> {
                throw UnsupportedOperationException("this type not support: $mValue")
            }
        }
        res as U
    }

    private fun putPreference(name: String, value: T) = with(prefs.edit()) {
        when (value) {
            is Long -> putLong(name, value)
            is String -> putString(name, value)
            is Int -> putInt(name, value)
            is Boolean -> putBoolean(name, value)
            is Float -> putFloat(name, value)
            else -> {
                throw UnsupportedOperationException("this type not support: $value")
            }
        }.apply()
    }
}