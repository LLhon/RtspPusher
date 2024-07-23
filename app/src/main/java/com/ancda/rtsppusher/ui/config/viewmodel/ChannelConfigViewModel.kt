package com.ancda.rtsppusher.ui.config.viewmodel

import android.app.ActivityManager
import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.net.ConnectivityManager
import android.os.RemoteException
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.ancda.base.viewmodel.BaseViewModel
import com.ancda.rtsppusher.MyApp
import com.ancda.rtsppusher.appViewModel
import com.ancda.rtsppusher.constant.Constant
import com.ancda.rtsppusher.constant.MmkvConstant
import com.ancda.rtsppusher.constant.mmkvUtils
import com.ancda.rtsppusher.utils.PerformanceTestUtils
import com.ancda.rtsppusher.utils.log.ALog
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.LogCallback
import com.arthenica.ffmpegkit.Statistics
import com.arthenica.ffmpegkit.StatisticsCallback
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * author  : LLhon
 * date    : 2024/4/23 16:00.
 * des     :
 */
class ChannelConfigViewModel : BaseViewModel() {

    private val TAG = "ChannelConfigViewModel"
    private val mRtmpUrlList = mutableListOf<String>()
    private var mExecutorService: ExecutorService? = null

    fun initData() {
        mExecutorService = Executors.newFixedThreadPool(32)
        mRtmpUrlList.add(Constant.RTMP_PUSH_URL1)
        mRtmpUrlList.add(Constant.RTMP_PUSH_URL2)
        mRtmpUrlList.add(Constant.RTMP_PUSH_URL3)
        mRtmpUrlList.add(Constant.RTMP_PUSH_URL4)
        mRtmpUrlList.add(Constant.RTMP_PUSH_URL5)
        mRtmpUrlList.add(Constant.RTMP_PUSH_URL6)
        mRtmpUrlList.add(Constant.RTMP_PUSH_URL7)
        mRtmpUrlList.add(Constant.RTMP_PUSH_URL8)
        mRtmpUrlList.add(Constant.RTMP_PUSH_URL9)
        mRtmpUrlList.add(Constant.RTMP_PUSH_URL10)
        mRtmpUrlList.add(Constant.RTMP_PUSH_URL11)
        mRtmpUrlList.add(Constant.RTMP_PUSH_URL12)
        mRtmpUrlList.add(Constant.RTMP_PUSH_URL13)
        mRtmpUrlList.add(Constant.RTMP_PUSH_URL14)
        mRtmpUrlList.add(Constant.RTMP_PUSH_URL15)
        mRtmpUrlList.add(Constant.RTMP_PUSH_URL16)
        mRtmpUrlList.add(Constant.RTMP_PUSH_URL17)
        mRtmpUrlList.add(Constant.RTMP_PUSH_URL18)
        mRtmpUrlList.add(Constant.RTMP_PUSH_URL19)
        mRtmpUrlList.add(Constant.RTMP_PUSH_URL20)
        mRtmpUrlList.add(Constant.RTMP_PUSH_URL21)
        mRtmpUrlList.add(Constant.RTMP_PUSH_URL22)
        mRtmpUrlList.add(Constant.RTMP_PUSH_URL23)
        mRtmpUrlList.add(Constant.RTMP_PUSH_URL24)
        mRtmpUrlList.add(Constant.RTMP_PUSH_URL25)
        mRtmpUrlList.add(Constant.RTMP_PUSH_URL26)
        mRtmpUrlList.add(Constant.RTMP_PUSH_URL27)
        mRtmpUrlList.add(Constant.RTMP_PUSH_URL28)
        mRtmpUrlList.add(Constant.RTMP_PUSH_URL29)
        mRtmpUrlList.add(Constant.RTMP_PUSH_URL30)
        mRtmpUrlList.add(Constant.RTMP_PUSH_URL31)
        mRtmpUrlList.add(Constant.RTMP_PUSH_URL32)
    }

    /**
     * 设备在凌晨3点定时开关机后重新恢复推流
     */
    fun resumePush() {
        if (mmkvUtils().getBoolean(MmkvConstant.IS_RESUME_PUSH_STREAM, false)) {
            val deviceList = appViewModel.mIpcListData.value
            startPush(0, deviceList!![0].profiles[0].rtspUrl)
            startPush(1, deviceList[1].profiles[0].rtspUrl)
            startPush(2, deviceList[2].profiles[0].rtspUrl)
            startPush(3, deviceList[3].profiles[0].rtspUrl)
            startPush(4, deviceList[4].profiles[0].rtspUrl)
            if (deviceList[5].profiles.size > 1) {
                startPush(5, deviceList[5].profiles[1].rtspUrl)
            }
            startPush(6, deviceList[6].profiles[0].rtspUrl)
            startPush(7, deviceList[7].profiles[0].rtspUrl)
            if (deviceList[8].profiles.size > 1) {
                startPush(8, deviceList[8].profiles[1].rtspUrl)
            }
            if (deviceList[9].profiles.size > 1) {
                startPush(9, deviceList[9].profiles[1].rtspUrl)
            }
            startPush(10, deviceList[10].profiles[0].rtspUrl)
            startPush(11, deviceList[11].profiles[0].rtspUrl)
            startPush(12, deviceList[12].profiles[0].rtspUrl)
            if (deviceList[13].profiles.size > 1) {
                startPush(13, deviceList[13].profiles[1].rtspUrl)
            }
//            startPush(14, deviceList[14].profiles[1].rtspUrl)
            if (deviceList[15].profiles.size > 1) {
                startPush(15, deviceList[15].profiles[1].rtspUrl)
            }
            startPush(16, deviceList[16].profiles[0].rtspUrl)
            if (deviceList[17].profiles.size > 1) {
                startPush(17, deviceList[17].profiles[1].rtspUrl)
            }
        }
    }

    /**
     * 无法推流
     * rtsp://admin:admin@192.168.6.89:554/12 子码流
     * rtsp://admin:admin@192.168.6.219:554/11
     * rtsp://admin:admin@192.168.6.88:554/12 子码流
     * rtsp://admin:admin@192.168.6.91:554/12 子码流
     *
     */

    fun startPush(position: Int, rtspUrl: String) {
        ALog.dToFile(TAG, "startPush: position=${position}, rtspUrl=${rtspUrl}, rtmpUrl=${mRtmpUrlList[position]}")
        FFmpegKit.executeAsync("-i $rtspUrl -c:v copy -c:a copy -f flv ${mRtmpUrlList[position]}",
            /**
             *  CALLED WHEN SESSION IS EXECUTED
             */
            { session ->
                val state = session.state
                val returnCode = session.returnCode
                val curRtspUrl = rtspUrl

                //FFmpeg process exited with state COMPLETED and rc -541478725.null
                ALog.eToFile(TAG, "----------------------------------推流断开------------------------------------")
                ALog.eToFile(TAG, "FFmpegSessionCompleteCallback: [${curRtspUrl}]")
                ALog.eToFile(TAG, String.format("FFmpeg process exited with state %s and rc %s.%s", state, returnCode, session.failStackTrace))
                ALog.eToFile(TAG, "-----------------------------------------------------------------------------")

                //todo 重推逻辑
                //两种情况：1、本身就是推不成功  2、本身能推但因为一些外在因素导致突然断开
//                startPush()

            }, object: LogCallback {
                override fun apply(log: com.arthenica.ffmpegkit.Log) {
                    Log.d(TAG, log.toString())
                }
            }, object: StatisticsCallback {
                override fun apply(statistics: Statistics?) {

                }
            }, mExecutorService)
    }

    fun performanceTest(activity: AppCompatActivity) {
        ALog.dToFile("Performance", "======================== 性能监控 ========================")
        val map = LinkedHashMap<String, String>()
        val r = Runtime.getRuntime()
        val Mb = 1024 * 1024
        map["【app可分配的最大内存上限】"] = (r.maxMemory() / Mb).toString() + "M"
        map["【app当前分配的内存总量】"] = (r.totalMemory() / Mb).toString() + "M"
        map["【app当前空闲内存】"] = (r.freeMemory() / Mb).toString() + "M"
        map["【app当前已使用内存】"] = ((r.totalMemory() - r.freeMemory()) / Mb).toString() + "M"

        val activityManager = activity.getSystemService(AppCompatActivity.ACTIVITY_SERVICE) as ActivityManager
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

    /**
     * 网络流量使用统计
     */
    private fun getNetworkUsage(activity: AppCompatActivity) {
        val networkStatsManager = activity.getSystemService(AppCompatActivity.NETWORK_STATS_SERVICE) as NetworkStatsManager
        var networkStats: NetworkStats? = null
        //从过去1天开始计算
        val startTimeMillis = System.currentTimeMillis() - 1000 * 60 * 60 * 24
        val endTimeMillis = System.currentTimeMillis()
        try {
            networkStats = networkStatsManager.querySummary(ConnectivityManager.TYPE_ETHERNET, "", startTimeMillis, endTimeMillis)
        } catch (e: RemoteException) {
            e.printStackTrace()
        } finally {
            networkStats?.close()
        }
        val bucket = NetworkStats.Bucket()
        //获取第一个bucket
        networkStats?.getNextBucket(bucket)
        do {
            // 获取应用程序UID
            val uid = bucket.uid
            // 获取应用程序名称
            val packageName = activity.packageManager.getNameForUid(uid)
            // 获取应用程序消耗的数据量
            val rxBytes = bucket.rxBytes
            val txBytes = bucket.txBytes
            val totalBytes = bucket.rxBytes + bucket.txBytes

            ALog.dToFile("NetworkStats", "【带宽流量数据统计】App: " + packageName + "  uid: " + uid +
                    " rxBytes: " + rxBytes + ", txBytes: " + txBytes + ", Total bytes: " + totalBytes)
        } while (networkStats?.getNextBucket(bucket) == true) // 获取下一个Bucket，直到没有更多Bucket为止
    }

    fun release() {
        mExecutorService?.shutdown()
        mExecutorService = null
    }
}