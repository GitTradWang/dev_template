package com.tradwang.commonstudy.ui.splash

import android.content.Intent
import android.os.Bundle
import com.tradwang.centre.entry.ArticleBean
import com.tradwang.common.base.BaseActivity
import com.tradwang.commonstudy.R
import com.tradwang.commonstudy.ui.MainActivity
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        initData()
        initEvent()
    }

    private fun initEvent() {
        login.setOnClickListener { startActivity(Intent(this@SplashActivity, MainActivity::class.java)) }
    }

    private fun initData() {

    }
}
