package com.ancda.rtsppusher.base

import android.os.Bundle
import android.os.Handler
import android.view.MotionEvent
import androidx.viewbinding.ViewBinding
import com.ancda.base.activity.BaseVmVbActivity
import com.ancda.base.viewmodel.BaseViewModel
import com.ancda.rtsppusher.MyApp
import com.kingja.loadsir.core.LoadService

/**
 * author  : LLhon
 * date    : 2024/3/21 17:07.
 * des     :
 */
abstract class BaseActivity<VM : BaseViewModel, VB : ViewBinding> : BaseVmVbActivity<VM, VB>() {

    var loadSir: LoadService<Any>? = null
    private val mSleepHandler = Handler()
    //是否开启待机页面功能
    private var mEnableSleepPage = true

    abstract override fun initView(savedInstanceState: Bundle?)

    override fun onResume() {
        super.onResume()
        if (mEnableSleepPage) {
            startSleepTimer()
        }
    }

    override fun onPause() {
        super.onPause()
        if (mEnableSleepPage) {
            stopSleepTimer()
        }
    }

    /**
     * 创建liveData观察者
     */
    override fun createObserver() {}

    /**
     * 打开等待框
     */
    override fun showLoading() {
        showLoading(null)
    }

    /**
     * 打开等待框
     */
    override fun showLoading(message: String?) {
//        showLoadingDialog(this, message)
    }

    /**
     * 关闭等待框
     */
    override fun dismissLoading() {
//        dismissLoadingDialog()
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (mEnableSleepPage) {
            when (ev.action) {
                MotionEvent.ACTION_DOWN -> {
                    stopSleepTimer()
                }
                MotionEvent.ACTION_UP -> {
                    startSleepTimer()
                }
                else -> {
                    stopSleepTimer()
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private val mSleepRunnable = Runnable {
        //显示待机休眠页面
        showSleepPage()
    }

    /**
     * 控制是否开启待机休眠页面功能
     */
    fun enableSleepPage(enableSleepPage: Boolean) {
        mEnableSleepPage = enableSleepPage
    }

    /**
     * 显示待机休眠页面
     */
    open fun showSleepPage() {

    }

    /**
     * 隐藏待机休眠页面
     */
    open fun hideSleepPage() {

    }

    /**
     * 启动待机休眠计时器
     */
    open fun startSleepTimer() {
        stopSleepTimer()
        mSleepHandler.postDelayed(mSleepRunnable, MyApp.sSleepDuration * 1000L)
    }

    open fun stopSleepTimer() {
        mSleepHandler.removeCallbacks(mSleepRunnable)
    }
}