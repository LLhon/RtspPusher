package com.ancda.rtsppusher

import android.content.Context
import android.os.Build
import android.text.TextUtils
import com.ancda.base.BaseApp
import com.ancda.rtsppusher.ui.viewmodel.AppViewModel
import com.ancda.rtsppusher.utils.Utils
import com.ancda.rtsppusher.utils.log.ALog
import com.blankj.utilcode.util.ProcessUtils
import com.github.gzuliyujiang.dialog.DialogColor
import com.github.gzuliyujiang.dialog.DialogConfig
import com.github.gzuliyujiang.dialog.DialogStyle
import com.tencent.bugly.crashreport.CrashReport
import com.tencent.bugly.crashreport.CrashReport.UserStrategy
import com.tencent.mmkv.MMKV


/**
 * author  : LLhon
 * date    : 2024/3/20 16:18.
 * des     :
 */

//全局ViewModel，里面存放了一些基本配置信息等
val appViewModel: AppViewModel by lazy { MyApp.appViewModelInstance }

class MyApp : BaseApp() {

    companion object {
        lateinit var sInstance: MyApp
        lateinit var appViewModelInstance: AppViewModel
        var sSleepDuration = 5 * 60
        var deviceID = ""
        val heartbeatInterval: Long = 300000 //心跳间隔，默认5分钟
        var isNetworkAvailable = true
    }

    override fun onCreate() {
        super.onCreate()
        if (ProcessUtils.isMainProcess()) {
            sInstance = this
            appViewModelInstance = getAppViewModelProvider()[AppViewModel::class.java]
            deviceID = "NV${getDeviceNo()}"

            ALog.init()

            initBugly()
            initDialogStyle()
        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MMKV.initialize(this, filesDir.absolutePath + "/mmkv")
    }

    private fun initDialogStyle() {
        DialogConfig.setDialogStyle(DialogStyle.Three)
        DialogConfig.setDialogColor(
            DialogColor()
                .cancelTextColor(getColor(R.color.black))
                .okTextColor(getColor(R.color.black))
        )
    }

    private fun initBugly() {
        val strategy = UserStrategy(this)
        //设备id
        strategy.setDeviceID(deviceID)
        //设备型号
        strategy.setDeviceModel(Build.MODEL)
        //设置渠道
        strategy.setAppChannel("")
        //App的版本
        strategy.setAppVersion(BuildConfig.VERSION_NAME)
        //App的包名
        strategy.setAppPackageName(packageName)
        strategy.setCrashHandleCallback(object : CrashReport.CrashHandleCallback() {

            /**
             * public static final int CRASHTYPE_JAVA_CRASH = 0; // Java crash
             * public static final int CRASHTYPE_JAVA_CATCH = 1; // Java caught exception
             * public static final int CRASHTYPE_NATIVE = 2; // Native crash
             * public static final int CRASHTYPE_U3D = 3; // Unity error
             * public static final int CRASHTYPE_ANR = 4; // ANR
             * public static final int CRASHTYPE_COCOS2DX_JS = 5; // Cocos JS error
             * public static final int CRASHTYPE_COCOS2DX_LUA = 6; // Cocos Lua error
             */

            /**
             * Crash处理.
             *
             * @param crashType 错误类型：CRASHTYPE_JAVA，CRASHTYPE_NATIVE，CRASHTYPE_U3D ,CRASHTYPE_ANR
             * @param errorType 错误的类型名
             * @param errorMessage 错误的消息
             * @param errorStack 错误的堆栈
             * @return 返回额外的自定义信息上报
             */
            override fun onCrashHandleStart(
                crashType: Int,
                errorType: String?,
                errorMessage: String?,
                errorStack: String?
            ): MutableMap<String, String> {

                when (crashType) {
                    0 -> {
                        ALog.eToFile("CrashReport", "########################## Java crash ##########################")
                    }
                    1 -> {
                        ALog.eToFile("CrashReport", "########################## Java caught exception ##########################")
                    }
                    2 -> {
                        ALog.eToFile("CrashReport", "########################## Native crash ##########################")
                    }
                    4 -> {
                        ALog.eToFile("CrashReport", "########################## ANR ##########################")
                    }
                }
                ALog.eToFile("CrashReport", "【crashType】 $crashType")
                ALog.eToFile("CrashReport", "【errorType】 $errorType")
                ALog.eToFile("CrashReport", "【errorMessage】 $errorMessage")
                ALog.eToFile("CrashReport", "【errorStack】 $errorStack")

                return super.onCrashHandleStart(crashType, errorType, errorMessage, errorStack)
            }

            override fun onCrashHandleStart2GetExtraDatas(
                crashType: Int,
                errorType: String?,
                errorMessage: String?,
                errorStack: String?
            ): ByteArray? {
                return try {
                    "Extra data.".toByteArray(charset("UTF-8"))
                } catch (e: Exception) {
                    null
                }
            }
        })

        //设置为开发设备
        CrashReport.setIsDevelopmentDevice(this, BuildConfig.DEBUG)
        //...在这里设置strategy的属性，在bugly初始化时传入
        CrashReport.setUserId(deviceID)
        CrashReport.initCrashReport(this, "a2f0079c3d", BuildConfig.DEBUG, strategy)
    }

    private fun getDeviceNo(): String {
        var cpuName: String = Utils.getCpuSerial()
        if (!TextUtils.isEmpty(cpuName) && !Utils.isZero(cpuName)) {
            return Utils.getCRC32(cpuName)
        }
        cpuName = Utils.getImei(this)
        if (!TextUtils.isEmpty(cpuName)) {
            return Utils.getCRC32(cpuName)
        }
        cpuName = Utils.getMacAdd(this)
        return if (!TextUtils.isEmpty(cpuName)) {
            Utils.getCRC32(cpuName)
        } else ""
    }
}