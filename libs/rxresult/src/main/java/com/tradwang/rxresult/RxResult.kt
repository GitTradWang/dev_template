package com.tradwang.rxresult

import android.app.Activity
import android.content.Intent
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

/**
 * 利用fragment构建不依赖activity的onActivityResult的请求
 */
class RxResult(activity: Activity) {

    private var mRxResultFragment: RxResultFragment

    init {
        mRxResultFragment = getRxResultFragment(activity)
    }

    private fun getRxResultFragment(activity: Activity): RxResultFragment {
        var rxResultFragment: RxResultFragment? = findRxResultFragment(activity)
        val isNewInstance = rxResultFragment == null
        if (isNewInstance) {
            rxResultFragment = RxResultFragment()
            val fragmentManager = activity.fragmentManager
            fragmentManager.beginTransaction().add(rxResultFragment, TAG).commitAllowingStateLoss()
            fragmentManager.executePendingTransactions()
        }
        return rxResultFragment!!
    }

    private fun findRxResultFragment(activity: Activity): RxResultFragment? {
        return activity.fragmentManager.findFragmentByTag(TAG) as RxResultFragment?
    }

    /**
     * 请求
     * @param intent Intent
     */
    fun requestForResult(intent: Intent): Observable<Intents> {
        val publishSubject = PublishSubject.create<Intents>()
        mRxResultFragment.startIntentFromFragment(intent, publishSubject)
        return publishSubject
    }

    companion object {
        internal const val TAG = "RxResult"
    }
}