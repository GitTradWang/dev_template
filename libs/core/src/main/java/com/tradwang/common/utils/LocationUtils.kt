package com.tradwang.common.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import androidx.core.app.ActivityCompat
import java.io.IOException
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/*
 * 注意
 * 1、Android6.0动态权限,
 * 2、Geocoder获取地理位置信息是一个后台的耗时操作，
 * 为了不阻塞主线程，强力建议在使用Geocoder获取地理位置信息时采用异步线程的方式来请求服务，这样不会造成主线程的阻塞。
 * */
class LocationUtils private constructor(private val mContext: Context) {

    private var locationManager: LocationManager? = null
    private var executorService: ExecutorService? = null


    private val locationListener = object : LocationListener {

        // Provider被enable时触发此函数，比如GPS被打开
        override fun onProviderEnabled(provider: String) {
            requestLocation(provider)
        }

        // Provider被disable时触发此函数，比如GPS被关闭
        override fun onProviderDisabled(provider: String) {

        }

        //当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
        override fun onLocationChanged(location: Location) {
            showLocation(location)
        }

        // Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
        override fun onStatusChanged(s: String, i: Int, bundle: Bundle) {

        }
    }

    init {
        executorService = Executors.newSingleThreadExecutor()
        getLocation()
    }

    private fun getLocation() {

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        //1.获取位置管理器
        locationManager = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val criteria = Criteria()
        criteria.accuracy = Criteria.ACCURACY_COARSE
        //设置不需要获取海拔方向数据
        criteria.isAltitudeRequired = false
        criteria.isBearingRequired = false
        criteria.isCostAllowed = true
        //要求低耗电
        criteria.powerRequirement = Criteria.POWER_LOW
        val provider = locationManager!!.getBestProvider(criteria, false)
        val location = locationManager!!.getLastKnownLocation(provider)
        if (location != null) {
            //第一次获得设备的位置
            showLocation(location)
        }
        if (location == null) {
            requestLocation(provider)
        }
    }

    private fun requestLocation(provider: String) {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        locationManager!!.requestLocationUpdates(provider, 6000, 2f, locationListener)
    }

    //获取经纬度
    private fun showLocation(location: Location?) {
        if (location == null) {
            return
        }
        val latitude = location.latitude//纬度
        val longitude = location.longitude//经度
        getAddress(latitude, longitude)
    }

    private fun getAddress(latitude: Double, longitude: Double) {
        executorService!!.execute {
            //Geocoder通过经纬度获取具体信息
            val gc = Geocoder(mContext, Locale.getDefault())
            try {
                val locationList = gc.getFromLocation(latitude, longitude, 1)

                if (locationList != null) {
                    val address = locationList[0]

                    val countryName = address.countryName//国家
                    val countryCode = address.countryCode
                    val adminArea = address.adminArea//省
                    val locality = address.locality//市
                    val subAdminArea = address.subAdminArea//区
                    val featureName = address.featureName//街道

                    val currentPosition = ("latitude is " + latitude//

                            + "\n" + "longitude is " + longitude//

                            + "\n" + "countryName is " + countryName//null

                            + "\n" + "countryCode is " + countryCode//

                            + "\n" + "adminArea is " + adminArea//

                            + "\n" + "locality is " + locality//

                            + "\n" + "subAdminArea is " + subAdminArea//

                            + "\n" + "featureName is " + featureName)//

                    println(currentPosition)

                    removeLocationUpdatesListener()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun removeLocationUpdatesListener() {
        if (locationManager != null) {
            uniqueInstance = null
            executorService = null
            locationManager!!.removeUpdates(locationListener)
        }
    }

    companion object {

        @Volatile
        @SuppressLint("StaticFieldLeak")
        private var uniqueInstance: LocationUtils? = null

        fun getInstance(context: Context): LocationUtils? {
            if (uniqueInstance == null) {
                synchronized(LocationUtils::class.java) {
                    if (uniqueInstance == null) {
                        uniqueInstance = LocationUtils(context)
                    }
                }
            }
            return uniqueInstance
        }
    }
}