package com.tradwang.common.base

import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.app.KeyguardManager
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.text.TextUtils
import android.util.Log
import com.alibaba.android.arouter.launcher.ARouter
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import com.tradwang.common.BuildConfig
import com.tradwang.common.R
import com.tradwang.common.utils.KLog
import java.util.*


/**
 *  Project Name : CommonStudy
 *  Package Name : com.tradwang.common.base
 *  @since 2018/2/9 15: 27
 *  @author : TradWang
 *  @email : trad_wang@sina.com
 *  @version :
 *  @describe :
 */
abstract class App : Application(), AppLifeCallBack {

    override fun onCreate() {
        super.onCreate()

        KLog.getSettings().setBorderEnable(true).setInfoEnable(true).setLogLevel(Log.VERBOSE).setLogEnable(BuildConfig.DEBUG)

        sInstance = this
        sHandler = Handler(Looper.getMainLooper())
        sActivityStack = Stack()

        initRefreshLayout()
        registerActivityLifeCallback()
        initARouter()
    }

    /**
     *设置全局刷新样式
     */
    private fun initRefreshLayout() {
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, layout ->
            layout.setPrimaryColorsId(R.color.white, android.R.color.black)//全局设置主题颜色
            return@setDefaultRefreshFooterCreator ClassicsFooter(context)
        }

        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, layout ->
            layout.setPrimaryColorsId(R.color.white, android.R.color.black)//全局设置主题颜色
            return@setDefaultRefreshHeaderCreator ClassicsHeader(context)
        }
    }

    /**
     *注册ActivityLifeCallback
     */
    private fun registerActivityLifeCallback() {
        registerActivityLifecycleCallbacks(ActivityLifeCallBack())
    }

    /**
     *初始化aRouter
     */
    private fun initARouter() {
        if (BuildConfig.DEBUG) {
            ARouter.openLog()
            ARouter.openDebug()
        }
        ARouter.init(this)
    }

    companion object {
        private var mTotalActivity = 0
        private lateinit var sInstance: App
        private lateinit var sHandler: Handler
        private lateinit var sActivityStack: Stack<Activity>

        /**
         *获取单例
         */
        fun getInstance(): App {
            return sInstance
        }

        /**
         *获取UI进程Handler
         */
        fun getHandler(): Handler {
            return sHandler
        }

        /**
         * 发送到主线程运行
         */
        fun runOnUiThread(runnable: Runnable) {
            runnable.let { getHandler().post(runnable) }
        }

        fun runDelayed(run: () -> Unit) {
            getHandler().postDelayed({ run.invoke() }, 1200)
        }

        /**
         * @return 是否是主线程
         */
        fun isMainProcess(): Boolean {
            val pid = android.os.Process.myPid()
            val activityManager = sInstance.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
            activityManager ?: return false
            for (appProcess in activityManager.runningAppProcesses) {
                if (appProcess.pid == pid) {
                    return sInstance.applicationInfo.packageName == appProcess.processName
                }
            }
            return false
        }

        /**
         * 判断App是否在后台台
         *
         * @return true  在后台, false  不在后台
         */
        fun appIsBackground(): Boolean {

            val activityManager = sInstance.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
            val keyguardManager = sInstance.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager?

            if (keyguardManager != null && activityManager != null) {
                val appProcesses = activityManager.runningAppProcesses
                for (appProcess in appProcesses) {
                    if (TextUtils.equals(appProcess.processName, sInstance.packageName)) {
                        val isBackground = appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE
                        val isLockedState = keyguardManager.inKeyguardRestrictedInputMode()
                        return isBackground || isLockedState
                    }
                }
                return false
            } else {
                return false
            }
        }

        /**
         * 判断App是否在前台
         *
         * @return true  在前台, false  不在前台
         */
        fun appIsForeground(packageName: String): Boolean {

            val activityManager = sInstance.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
            val keyguardManager = sInstance.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager?

            if (keyguardManager != null && activityManager != null) {
                val appProcesses = activityManager.runningAppProcesses
                for (appProcess in appProcesses) {
                    if (TextUtils.equals(appProcess.processName, packageName)) {
                        return appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                    }
                }
                return false
            } else {
                return false
            }
        }

        /**
         * 判断app 是否在运行
         *
         * @return rue  正在运行 , false  没有运行
         */
        fun isAppRunning(): Boolean {
            val activityManager = sInstance.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
            return if (activityManager != null) {
                val runningAppProcesses = activityManager.runningAppProcesses
                runningAppProcesses.any { TextUtils.equals(it.processName, sInstance.packageName) }
            } else {
                false
            }
        }


        /**
         * 判断app 是否在运行
         *
         * @return rue  正在运行 , false  没有运行
         */
        fun isAppRunning(packageName: String): Boolean {
            val activityManager = sInstance.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
            return if (activityManager != null) {
                val runningAppProcesses = activityManager.runningAppProcesses
                runningAppProcesses.any { TextUtils.equals(it.processName, packageName) }
            } else {
                false
            }
        }

        /**
         * 唤醒后台Activity到前台
         */
        fun awakeBackgroundApp(activity: Activity?) {
            if (activity == null) {
                return
            }
            val activityManager = activity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
            if (activityManager != null) {
                val runningAppProcesses = activityManager.runningAppProcesses
                for (runningAppProcess in runningAppProcesses) {
                    if (TextUtils.equals(runningAppProcess.processName, activity.packageName)) {
                        activityManager.moveTaskToFront(activity.taskId, ActivityManager.MOVE_TASK_WITH_HOME)
                        return
                    }
                }
            }
        }

        /**
         * @return 屏幕是否亮起
         */
        fun isScreenOn(): Boolean {
            val manager = sInstance.getSystemService(Context.POWER_SERVICE) as PowerManager?
            manager ?: return false
            return manager.isInteractive
        }

        /**
         * 是否屏幕锁定
         */
        fun isLocked(): Boolean {
            val manager = sInstance.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager?
            return manager != null && manager.isKeyguardLocked
        }

        /**
         * 唤醒屏幕
         */
        fun wakeUpScreen() {
            val manager = sInstance.getSystemService(Context.POWER_SERVICE) as PowerManager?
            if (manager != null) {
                if (!isScreenOn()) {
                    // 获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
                    val wakeLock = manager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright")
                    wakeLock.acquire(10000)
                    wakeLock.release()
                }
            }
        }

        /**
         * 获取当前显示的Activity
         *
         * @return Activity
         */
        fun getCurrentActivity(): Activity? {
            return if (sActivityStack.isEmpty()) {
                null
            } else sActivityStack.peek()
        }

        fun getActivityByClass(clazz: Class<*>?): Activity? {
            if (clazz == null) {
                return null
            }
            val activityListIterator = sActivityStack.listIterator()
            while (activityListIterator.hasNext()) {
                val activity = activityListIterator.next()
                if (activity == null) {
                    activityListIterator.remove()
                    continue
                }
                if (clazz == activity.javaClass) {
                    return activity
                }
            }
            return null
        }

        /**
         * 关闭当前的Activity
         */
        fun finishCurrentActivity() {
            if (sActivityStack.isEmpty()) {
                return
            }
            sActivityStack.pop()?.finish()
        }

        /**
         * 结束某个Activity
         *
         * @param clazz Activity.class
         */
        fun finishActivity(clazz: Class<*>) {
            val iterator = sActivityStack.listIterator()
            while (iterator.hasNext()) {

                val activity = iterator.next()

                if (activity == null) {
                    iterator.remove()
                    continue
                }
                if (activity.javaClass == clazz) {
                    iterator.remove()
                    activity.finish()
                    break
                }
            }
        }


        /**
         * 结束所有Activity 除了其中一个...
         *
         * @param clazz Activity.class
         */
        fun finishActivityExcept(clazz: Class<*>?) {

            if (clazz == null) {
                return
            }

            val iterator = sActivityStack.listIterator()
            while (iterator.hasNext()) {

                val activity = iterator.next()

                if (activity == null) {
                    iterator.remove()
                    continue
                }
                if (activity.javaClass != clazz) {
                    iterator.remove()
                    activity.finish()
                }
            }
        }

        /**
         * 任务栈是否包含目标activity
         *
         * @param clazz 目标activity
         * @return 是否包含
         */
        fun hasTargetActivity(clazz: Class<*>): Boolean {
            return sActivityStack.any { it != null && it.javaClass == clazz }
        }

        /**
         * 关闭所有的activity
         */
        fun finishAllActivity() {
            val listIterator = sActivityStack.listIterator()
            while (listIterator.hasNext()) {
                val activity = listIterator.next()
                if (activity == null) {
                    listIterator.remove()
                    continue
                }
                activity.finish()
                listIterator.remove()
            }
        }

    }

    inner class ActivityLifeCallBack : ActivityLifecycleCallbacks {

        override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
            Log.w("APP", "onActivityCreated--> ${activity?.javaClass?.simpleName}  <--")
            sActivityStack.push(activity)
        }

        override fun onActivityStarted(activity: Activity?) {

        }

        override fun onActivityResumed(activity: Activity?) {
            mTotalActivity += mTotalActivity
            if (mTotalActivity == 1) { //App可见
                synchronized(this@App) {
                    this@App.onAppResumed()
                }
            }
        }

        override fun onActivityPaused(activity: Activity?) {
            mTotalActivity -= mTotalActivity
            if (mTotalActivity == 0) { //App不可见
                synchronized(this@App) {
                    this@App.onAppPaused()
                }
            }
        }

        override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {

        }

        override fun onActivityStopped(activity: Activity?) {

        }

        override fun onActivityDestroyed(activity: Activity?) {
            sActivityStack.remove(activity)
            Log.w("APP", "onActivityDestroyed--> ${activity?.javaClass?.simpleName}  <--")
        }
    }
}