package  com.tradwang.common

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * A lazy property that gets cleaned up when the lifecycleOwner is destroyed.
 *
 * Accessing this variable in a destroyed lifecycleOwner will throw NPE.
 */
class AutoClearedValue<T : Any>(lifecycleOwner: LifecycleOwner) : ReadWriteProperty<Fragment, T> {
    private var _value: T? = null

    init {
        lifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
                _value = null
            }
        })
    }

    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        return _value ?: throw IllegalStateException(
                "should never call auto-cleared-value get when it might not be available"
        )
    }

    override fun setValue(thisRef: Fragment, property: KProperty<*>, value: T) {
        _value = value
    }
}

/**
 * Creates an [AutoClearedValue] associated with this lifecycleOwner.
 */
fun <T : Any> Fragment.autoCleared() = AutoClearedValue<T>(this)

fun <T : Any> AppCompatActivity.autoCleared() = AutoClearedValue<T>(this)