package com.tradwang.demomodule.debug

import android.os.Bundle
import com.tradwang.common.base.App
import com.tradwang.common.base.BaseActivity
import com.tradwang.demomodule.R

class MainActivity : BaseActivity() {

    private val firstFragment by lazy { FirstFragment() }
    private val secondFragment by lazy { SecondFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.demo_activity_main)

        supportFragmentManager.beginTransaction().add(R.id.fragment_container, firstFragment).commit()

        App.getHandler().postDelayed({
            supportFragmentManager.beginTransaction().hide(firstFragment).add(R.id.fragment_container, secondFragment).commit()

            println(firstFragment.isVisible)
            println(firstFragment.userVisibleHint)

        }, 2000)
    }
}
