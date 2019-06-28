package com.tradwang.common.base

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.ViewGroup
import android.webkit.WebView
import com.tradwang.common.R
import com.tradwang.common.utils.log

class WebActivity : BaseAgentWebActivity() {

    private var mUrl = ""

    companion object {
        private const val EXTRA_URL = "extra_url"
        fun startWebViewActivity(context: Context?, url: String?) {
            context?.let {
                val intent = Intent(it, WebActivity::class.java)
                intent.putExtra(EXTRA_URL, url)
                it.startActivity(intent)
            }
        }
    }

    override fun getAgentWebParent(): ViewGroup {
        return this.findViewById(R.id.ll_container)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initIntent()
        setContentView(R.layout.activity_web)
        agentWeb.agentWebSettings.webSettings.useWideViewPort = true
        initData()
        initEvent()
    }

    private fun initIntent() {
        mUrl = intent.getStringExtra(EXTRA_URL)
        mUrl.log()
    }

    private fun initData() {

    }

    private fun initEvent() {

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (agentWeb != null && agentWeb.handleKeyEvent(keyCode, event)) {
            true
        } else super.onKeyDown(keyCode, event)
    }

    override fun setTitle(view: WebView, title: String) {

    }

    override fun getIndicatorColor(): Int {
        return Color.parseColor("#00ff00")
    }

    override fun getIndicatorHeight(): Int {
        return 1
    }

    override fun getUrl(): String? {
        return mUrl
    }
}
