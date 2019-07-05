package com.tradwang.rxresult

import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import io.reactivex.subjects.PublishSubject

/**
 * ProjectName   bianla_android
 * PackageName  com.bianla.commonlibrary.utils
 * @author  tradwang
 * @since   18-11-9   下午12:47.
 * @email  wangxiaojun@bianla.cn
 * @version  0.1
 * @describe  利用fragment构建不依赖activity的onActivityResult的请求
 */
internal  class RxResultFragment : Fragment() {

    private var mPublishSubject: PublishSubject<Intents>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE) {
            mPublishSubject?.onNext(Intents(resultCode, data))
            mPublishSubject?.onComplete()
        }
    }

    fun startIntentFromFragment(intent: Intent, publishSubject: PublishSubject<Intents>) {
        this.mPublishSubject = publishSubject
        this.startActivityForResult(intent, REQUEST_CODE)
    }

    companion object {
        const val REQUEST_CODE = 1114
    }
}