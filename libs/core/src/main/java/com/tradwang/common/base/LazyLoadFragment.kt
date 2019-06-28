package com.tradwang.common.base

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment

/**懒加载Fragment*/
open class LazyLoadFragment : Fragment() {
    private var mIsViewInitiated = false //视图是否初始化
    private var mIsDataInitiated = false //数据是否初始化
    private var mIsVisibleToUser = false //是否视图用户可见

    /**@return 是否强制加载，强制加载时每次可见都会刷新数据*/
    fun needReload(): Boolean = false

    @CallSuper
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        mIsVisibleToUser = isVisibleToUser
        tryLoad()
    }

    @CallSuper
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mIsViewInitiated = true
        tryLoad()
    }

    /**尝试更新数据*/
    private fun tryLoad() {
        preLoad()
    }

    /**预加载*/
    private fun preLoad() {
        if (mIsViewInitiated && mIsVisibleToUser && (!mIsDataInitiated || needReload())) {
            lazyLoad()
            mIsDataInitiated = true
        }
    }

    open fun lazyLoad() {

    }
}