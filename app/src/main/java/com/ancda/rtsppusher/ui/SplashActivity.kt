package com.ancda.rtsppusher.ui

import android.app.ActivityManager
import android.content.Intent
import android.os.Bundle
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.ancda.base.viewmodel.BaseViewModel
import com.ancda.rtsppusher.MyApp
import com.ancda.rtsppusher.R
import com.ancda.rtsppusher.base.BaseActivity
import com.ancda.rtsppusher.databinding.ActivitySplashBinding
import com.ancda.rtsppusher.ui.viewmodel.SplashViewModel
import com.ancda.rtsppusher.utils.log.ALog


/**
 * author  : LLhon
 * date    : 2024/5/7 14:33.
 * des     : 启动页
 */
class SplashActivity : BaseActivity<SplashViewModel, ActivitySplashBinding>() {

    override fun initView(savedInstanceState: Bundle?) {

        ALog.eToFile("SplashActivity", "=========================================================")

        mBind.tvDeviceId.text = String.format(getString(R.string.machine_bind_device_id), MyApp.deviceID)

        mBind.spinkitView.postDelayed({
            //显示设备未绑定页面
            mBind.clStartup.isGone = true
            mBind.clUnAuthLayout.isVisible = true
        }, 1500)

        mBind.tvStartConfig.setOnClickListener {
            //进入主页
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }


    }


}