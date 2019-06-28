package com.tradwang.common

import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.AlphaAnimation
import android.view.animation.Animation

/**
 * ProjectName   bianla_android
 * PackageName  com.bianla.commonlibrary
 * @author  tradwang
 * @since   18-8-28   下午8:30.
 * @email  wangxiaojun@bianla.cn
 * @version  0.1
 * @describe  TODO
 */
fun View.animVisibility(visibility: Int, duration: Long) {
    var start = 0f
    var end = 0f

    if (visibility != View.VISIBLE && visibility != View.GONE && visibility != View.INVISIBLE) {
        return
    }

    if (this.visibility == visibility) {
        return
    }

    if (visibility == View.VISIBLE) {
        end = 1f
        this.visibility = visibility
    } else
        if (visibility == View.GONE || visibility == View.INVISIBLE) {
            start = 1f
            this.visibility = visibility
        }
    val animation = AlphaAnimation(start, end)
    animation.duration = duration
    animation.fillAfter = true
    animation.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationRepeat(animation: Animation?) {}

        override fun onAnimationEnd(animation: Animation?) {
            this@animVisibility.clearAnimation()
        }

        override fun onAnimationStart(animation: Animation?) {}
    })
    this.animation = animation
    animation.start()
}

/**
 * Extension method to remove the required boilerplate for running code after a view has been
 * inflated and measured.
 *
 * @author Antonio Leiva
 * @see <a href="https://antonioleiva.com/kotlin-ongloballayoutlistener/>Kotlin recipes: OnGlobalLayoutListener</a>
 */
inline fun <T : View> T.afterMeasured(crossinline f: T.() -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            if (measuredWidth > 0 && measuredHeight > 0) {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                f()
            }
        }
    })
}

/**
 * Extension method to get ClickableSpan.
 * e.g.
 * val loginLink = getClickableSpan(application.getColorCompat(R.color.colorAccent), { })
 */
fun View.doOnLayout(onLayout: (View) -> Boolean) {
    addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
        override fun onLayoutChange(view: View, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
            if (onLayout(view)) {
                view.removeOnLayoutChangeListener(this)
            }
        }
    })
}