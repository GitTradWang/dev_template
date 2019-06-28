package com.tradwang.commonstudy.ui

import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.android.material.bottomnavigation.LabelVisibilityMode
import com.tradwang.centre.MainRoutes
import com.tradwang.common.base.BaseActivity
import com.tradwang.commonstudy.R
import kotlinx.android.synthetic.main.activity_main.*

@Route(path = MainRoutes.MAIN_ACTIVITY)
class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initData()
        initEvent()
        bnv_main_navigation.labelVisibilityMode=LabelVisibilityMode.LABEL_VISIBILITY_LABELED
    }

    private fun initData() {
    }

    private fun initEvent() {


        bnv_main_navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_home -> main_container.currentItem = 0
                R.id.navigation_category -> main_container.currentItem = 1
                R.id.navigation_chat -> main_container.currentItem = 2
                R.id.navigation_mine -> main_container.currentItem = 3
            }
            return@setOnNavigationItemSelectedListener true
        }

        main_container.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> bnv_main_navigation.selectedItemId = R.id.navigation_home
                    1 -> bnv_main_navigation.selectedItemId = R.id.navigation_category
                    2 -> bnv_main_navigation.selectedItemId = R.id.navigation_chat
                    3 -> bnv_main_navigation.selectedItemId = R.id.navigation_mine
                }
            }
        })
    }
}
