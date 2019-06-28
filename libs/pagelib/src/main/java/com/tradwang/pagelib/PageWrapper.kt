package com.tradwang.pagelib

import android.app.Activity
import android.content.Context
import android.os.Looper
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.FrameLayout
import java.security.InvalidParameterException

/**
 * ProjectName   CommonStudy
 * PackageName  com.tradwang.pagelib
 * @author  tradwang
 * @since   18-10-15   上午10:46.
 * @email  wangxiaojun@bianla.cn
 * @version  0.11
 * @describe  带有异常错误的代理页面类 简单使用
 * @sample <code> private val pageWrapper by lazy { PageWrapper.createBuilder(activity).errorRetry { retry() }.create() } </code>
 * 主要思路是用FrameLayout动态替换需要代理的View然后添加各种需要显示的页面动态显示或者隐藏
 * [Activity]  当 传入Activity时会以android.R.id.content这个frameLayout作为根布局
 * [Fragment] 当传入的是Fragment时 会以Fragment的父控件作为根布局
 * [View] 同Fragment 会以父控件作为根布局
 */
class PageWrapper private constructor(
        private val mContent: View?,
        private val mError: View?,
        private val mEmpty: View?,
        private val mLoading: View?,
        private val mCustom: View? = null) {

    /**默认的页面类型[State.CONTENT_TYPE]*/
    private var mCurrentState = State.CONTENT_TYPE

    /**[viewState]  页面的状态*/
    private fun changeState(viewState: State) {
        mCurrentState = viewState

        animVisibility(mError, if (viewState == State.ERROR_TYPE) View.VISIBLE else View.GONE, 300)
        animVisibility(mEmpty, if (viewState == State.EMPTY_TYPE) View.VISIBLE else View.GONE, 300)
        animVisibility(mCustom, if (viewState == State.CUSTOM_TYPE) View.VISIBLE else View.GONE, 300)
        animVisibility(mLoading, if (viewState == State.LOADING_TYPE) View.VISIBLE else View.GONE, 300)
        animVisibility(mContent, if (viewState == State.CONTENT_TYPE) View.VISIBLE else View.GONE, 300)

    }

    /**显示要展示的页面[viewState]  页面类型的枚举[State]*/
    private fun showView(viewState: State) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            changeState(viewState)
        } else {
            this.mContent?.post { changeState(viewState) }
        }
    }

    fun showError() {
        showView(State.ERROR_TYPE)
    }

    fun showEmpty() {
        showView(State.EMPTY_TYPE)
    }

    fun showCustom() {
        showView(State.CUSTOM_TYPE)
    }

    fun showLoading() {
        showView(State.LOADING_TYPE)
    }

    fun showContent() {
        showView(State.CONTENT_TYPE)
    }

    /**页面的状态类*/
    enum class State {
        /**[State.EMPTY_TYPE] 空数据页面*/
        EMPTY_TYPE,
        /**[State.LOADING_TYPE] 加载中的页面*/
        LOADING_TYPE,
        /**[State.ERROR_TYPE] 数据加载失败的页面*/
        ERROR_TYPE,
        /**[State.CONTENT_TYPE] 要代理的页面*/
        CONTENT_TYPE,
        /**[State.CUSTOM_TYPE] 自定义页面*/
        CUSTOM_TYPE
    }

    class PageBuilder(private val any: Any) {

        private var mContext: Context = when (any) {
            is androidx.fragment.app.Fragment -> any.requireContext()
            is Activity -> any
            is View -> any.context
            else -> throw InvalidParameterException("are you OK ...")  //emm...
        }

        private var mInflater: LayoutInflater = LayoutInflater.from(mContext)
        private var mRootView: FrameLayout = FrameLayout(mContext)

        private var mContent: View? = null

        private var mError: View? = null
        private var mEmpty: View? = null
        private var mCustom: View? = null
        private var mLoading: View? = null
        private var mRetryListener: (() -> Unit)? = null

        /**自定义错误布局[layoutId]Layout资源文件默认点击错误页面的跟布局重试*/
        fun customError(@LayoutRes layoutId: Int): PageBuilder {
            mError = mInflater.inflate(layoutId, mRootView, false)
            mError?.setOnClickListener { mRetryListener?.invoke() }
            return this
        }

        /**自定义空布局[layoutId]Layout资源文件*/
        fun customEmpty(@LayoutRes layoutId: Int): PageBuilder {
            mEmpty = mInflater.inflate(layoutId, mRootView, false)
            return this
        }

        /**自定义加载布局[layoutId]Layout资源文件*/
        fun customLoading(@LayoutRes layoutId: Int): PageBuilder {
            mLoading = mInflater.inflate(layoutId, mRootView, false)
            return this
        }

        /**错误重试[retryListener] 错误重试的回调*/
        fun errorRetry(retryListener: (() -> Unit)? = null): PageBuilder {
            mRetryListener = retryListener
            return this
        }

        /**自定义布局[view] 因为一般自定义[View]都会有自己的控件和事件要做所以传递VIew对象*/
        fun customView(view: View): PageBuilder {
            mLoading = view
            return this
        }

        fun create(): PageWrapper {
            initPage()
            checkAndDefault()
            val pageWrapper = PageWrapper(mContent, mError, mEmpty, mLoading, mCustom)
            pageWrapper.showContent()
            return pageWrapper
        }

        private fun initPage() {

            var viewGroup: ViewGroup? = null
            when (any) {
                is Activity -> {
                    viewGroup = any.findViewById(android.R.id.content)
                }
                is androidx.fragment.app.Fragment -> {
                    viewGroup = any.view?.parent as ViewGroup?
                }
                is View -> {
                    viewGroup = any.parent as ViewGroup?
                }
            }
            viewGroup ?: return //emm... 布局为空了就不用玩了...

            var index = 0
            val childCount = viewGroup.childCount

            val oldContent: View = any as? View ?: viewGroup.getChildAt(0)

            if (any is View) {
                for (i in 0 until childCount) {
                    if (viewGroup.getChildAt(i) === oldContent) {
                        index = i
                        break
                    }
                }
            }
            mContent = oldContent //之前的布局
            viewGroup.removeView(oldContent) //父布局移除原先的布局
            mRootView.addView(oldContent) //添加之前的布局
            viewGroup.addView(mRootView, index, oldContent.layoutParams) //加入替代的布局
        }

        /**校验参数完整性,若不完整的话设置默认属性*/
        private fun checkAndDefault() {
            if (mError == null) {
                mError = mInflater.inflate(R.layout.page_default_error, mRootView, false)
                mError?.setOnClickListener { mRetryListener?.invoke() }
            }
            if (mLoading == null) {
                mLoading = mInflater.inflate(R.layout.page_default_loading, mRootView, false)
            }
            if (mEmpty == null) {
                mEmpty = mInflater.inflate(R.layout.page_default_empty, mRootView, false)
            }
            mError?.let { mRootView.addView(it) }
            mLoading?.let { mRootView.addView(it) }
            mEmpty?.let { mRootView.addView(it) }
            mCustom?.let { mRootView.addView(it) }
        }
    }

    companion object {
        /**
         *创建
         * @param any 请填写 Activity Fragment 或者View
         * @return PageBuilder
         */
        fun createBuilder(any: Any): PageBuilder {
            return PageBuilder(any)
        }
    }

    private fun animVisibility(view: View?, visibility: Int, duration: Long) {

        view ?: return

        var start = 0f
        var end = 0f

        if (visibility != View.VISIBLE && visibility != View.GONE && visibility != View.INVISIBLE) {
            return
        }

        if (view.visibility == visibility) {
            return
        }

        if (visibility == View.VISIBLE) {
            end = 1f
            view.visibility = visibility
        } else
            if (visibility == View.GONE || visibility == View.INVISIBLE) {
                start = 1f
                view.visibility = visibility
            }
        val animation = AlphaAnimation(start, end)
        animation.duration = duration
        animation.fillAfter = true
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                view.clearAnimation()
            }

            override fun onAnimationStart(animation: Animation?) {}
        })
        view.animation = animation
        animation.start()
    }
}