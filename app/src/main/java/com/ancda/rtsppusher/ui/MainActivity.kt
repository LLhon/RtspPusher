package com.ancda.rtsppusher.ui

import android.app.ActivityManager
import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.RemoteException
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.ancda.base.ext.util.setOnClick
import com.ancda.rtsppusher.MyApp
import com.ancda.rtsppusher.R
import com.ancda.rtsppusher.base.BaseActivity
import com.ancda.rtsppusher.constant.MmkvConstant
import com.ancda.rtsppusher.constant.mmkvUtils
import com.ancda.rtsppusher.databinding.ActivityMainBinding
import com.ancda.rtsppusher.service.HeartbeatService
import com.ancda.rtsppusher.ui.config.fragment.ChannelConfigFragment
import com.ancda.rtsppusher.ui.config.fragment.ConfigFragment
import com.ancda.rtsppusher.ui.preview.PreviewFragment
import com.ancda.rtsppusher.ui.preview.TestPreviewActivity
import com.ancda.rtsppusher.ui.viewmodel.MainViewModel
import com.ancda.rtsppusher.utils.Md5Utils
import com.ancda.rtsppusher.utils.PerformanceTestUtils
import com.ancda.rtsppusher.utils.log.ALog
import com.ancda.rtsppusher.view.AboutDeviceDialog
import com.ancda.rtsppusher.view.ShutdownDialog
import kotlinx.coroutines.launch
import razerdp.util.PopupUiUtils
import razerdp.util.PopupUtils
import java.util.Calendar
import java.util.TimeZone


/**
 * author  : LLhon
 * date    : 2024/3/20 16:57.
 * des     :
 */
class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>(), OnClickListener {

    private val TAG = "MainActivity"
    private lateinit var mConfigFragment: ConfigFragment
    private var mPreviewFragment: PreviewFragment? = null
    private var mHeartbeatService: Intent? = null

    override fun initView(savedInstanceState: Bundle?) {
        Log.d(TAG, "initView")
        mConfigFragment = ConfigFragment()
        supportFragmentManager.beginTransaction().add(R.id.flContainer, mConfigFragment).commitAllowingStateLoss()

        mBind.fab.setOnClickListener {
//            startActivity(Intent(this, AddIPCActivity::class.java))
            TestPreviewActivity.launch(this)
        }
        mBind.fabPush.setOnClickListener {
            startActivity(Intent(this, PushRtspActivity::class.java))
        }

        setOnClick(this, mBind.previewTab, mBind.configTab, mBind.ivShutdown, mBind.ivAbout)
    }

    override fun initData() {
        Log.d(TAG, "initData")
        lifecycleScope.launch {
            mViewModel.readIpcList()
        }

        mViewModel.initConfig()

        mHeartbeatService = Intent(this, HeartbeatService::class.java).putExtra("isHeartbeat", true)
        startService(mHeartbeatService)
//        HeartbeatService.startService(this, true)
        performanceTest()
    }

    override fun createObserver() {

    }

    override fun showSleepPage() {
        if (mmkvUtils().getBoolean(MmkvConstant.ENABLE_OPERATION_PWD, true)) {
            mBind.screenSleepView.setVisible(true)
        }

        //关闭所有的popupwindow

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.previewTab -> {
                //预览tab
                if (mPreviewFragment == null) {
                    mPreviewFragment = PreviewFragment()
                    supportFragmentManager.beginTransaction().add(R.id.flContainer, mPreviewFragment!!).commitAllowingStateLoss()
                    supportFragmentManager.beginTransaction().hide(mConfigFragment).commitAllowingStateLoss()
                } else {
                    supportFragmentManager.beginTransaction().show(mPreviewFragment!!).commitAllowingStateLoss()
                    supportFragmentManager.beginTransaction().hide(mConfigFragment).commitAllowingStateLoss()
                }
                mBind.previewTab.setBackgroundResource(R.color.colorPrimaryVariant)
                mBind.configTab.setBackgroundResource(R.color.colorPrimary)
            }
            R.id.configTab -> {
                //配置config
                supportFragmentManager.beginTransaction().show(mConfigFragment).commitAllowingStateLoss()
                if (mPreviewFragment != null) {
                    supportFragmentManager.beginTransaction().hide(mPreviewFragment!!).commitAllowingStateLoss()
                }
                mBind.previewTab.setBackgroundResource(R.color.colorPrimary)
                mBind.configTab.setBackgroundResource(R.color.colorPrimaryVariant)
            }
            R.id.ivAbout -> {
                //关于设备
                AboutDeviceDialog(this).showPopupWindow()
            }
            R.id.ivShutdown -> {
                //关机
                ShutdownDialog(this).showPopupWindow()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(mHeartbeatService)
    }

    private fun performanceTest() {
        ALog.dToFile("Performance", "======================== 性能监控 ========================")
        val map = LinkedHashMap<String, String>()
        val r = Runtime.getRuntime()
        val Mb = 1024 * 1024
        map["【app最大可用内存】"] = (r.maxMemory() / Mb).toString() + "M"
        map["【app当前可用内存】"] = (r.totalMemory() / Mb).toString() + "M"
        map["【app当前空闲内存】"] = (r.freeMemory() / Mb).toString() + "M"
        map["【app当前已使用内存】"] = ((r.totalMemory() - r.freeMemory()) / Mb).toString() + "M"

        val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        val availableMemory = memoryInfo.availMem
        val totalMemory = memoryInfo.totalMem
        val lowMemory = memoryInfo.lowMemory
        map["【系统剩余内存】"] = (availableMemory / Mb).toString() + "M"
        map["【系统总内存】"] = (totalMemory / Mb).toString() + "M"
        map["【是否处于低内存状态】"] = lowMemory.toString()

        for (key in map.keys) {
            ALog.dToFile("Performance", "${key}${map[key]}")
        }

        val cpu = PerformanceTestUtils.topCpu()
        ALog.dToFile("Performance", "【CPU使用率】${cpu}%")
        val heapSize = PerformanceTestUtils.talHeapSize()
        ALog.dToFile("Performance", "【内存使用量】${heapSize}B")


        PerformanceTestUtils.getWifiFlow()

        ALog.dToFile("Performance", "======================== 性能监控 ========================")
    }
}