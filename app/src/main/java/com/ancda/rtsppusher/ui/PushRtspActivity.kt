package com.ancda.rtsppusher.ui

import android.app.ActivityManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import com.ancda.rtsppusher.base.BaseActivity
import com.ancda.rtsppusher.constant.Constant
import com.ancda.rtsppusher.databinding.ActivityPushRtspBinding
import com.ancda.rtsppusher.ui.viewmodel.PushRtspViewModel
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.LogCallback
import com.arthenica.ffmpegkit.Statistics
import com.arthenica.ffmpegkit.StatisticsCallback
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * author  : LLhon
 * date    : 2024/4/8 18:52.
 * des     :
 */
class PushRtspActivity : BaseActivity<PushRtspViewModel, ActivityPushRtspBinding>() {

    private val TAG = "PushRtspActivity"
    private val mRtspUrlList = mutableListOf<String>()
    private val mRtmpUrlList = mutableListOf<String>()
    private lateinit var activityManager: ActivityManager
    private var mExecutorService: ExecutorService? = null

    override fun initView(savedInstanceState: Bundle?) {
        mBind.btnVersion.setOnClickListener {

        }
        mBind.btnAvcodec.setOnClickListener {

        }
        mBind.btnPushAll.setOnClickListener {
            startPushAllChannel()
        }
        mBind.btnPush32All.setOnClickListener {
            startPush32Channel()
        }
        mBind.btnPush1.setOnClickListener {
            startPushChannel(Constant.RTSP_URL1, Constant.RTMP_PUSH_URL1)
        }
        mBind.btnPush2.setOnClickListener {
            startPushChannel(Constant.RTSP_URL2, Constant.RTMP_PUSH_URL2)
        }
        mBind.btnPush3.setOnClickListener {
            startPushChannel(Constant.RTSP_URL3, Constant.RTMP_PUSH_URL3)
        }
        mBind.btnPush4.setOnClickListener {

        }
        mBind.btnPush5.setOnClickListener {

        }
        mBind.btnPush6.setOnClickListener {

        }
        mBind.btnPush7.setOnClickListener {

        }
        mBind.btnPush8.setOnClickListener {

        }
        mBind.btnPush9.setOnClickListener {

        }
        mBind.btnPush10.setOnClickListener {

        }
        mBind.btnPush11.setOnClickListener {

        }
        mBind.btnPush12.setOnClickListener {

        }
        mBind.btnPush13.setOnClickListener {

        }
        mBind.btnPush14.setOnClickListener {

        }
        mBind.btnPush15.setOnClickListener {

        }
        mBind.btnPush16.setOnClickListener {

        }
        mBind.btnPush17.setOnClickListener {

        }
        mBind.btnPush18.setOnClickListener {

        }
        mBind.btnPush19.setOnClickListener {

        }
        mBind.btnPush20.setOnClickListener {

        }
        mBind.btnPush21.setOnClickListener {

        }
        mBind.btnPush22.setOnClickListener {

        }
        mBind.btnPush23.setOnClickListener {

        }
        mBind.btnPush24.setOnClickListener {

        }
        mBind.btnPush25.setOnClickListener {
        }
        mBind.btnPush26.setOnClickListener {
        }
        mBind.btnPush27.setOnClickListener {
        }
        mBind.btnPush28.setOnClickListener {
        }
        mBind.btnPush29.setOnClickListener {
        }
        mBind.btnPush30.setOnClickListener {
        }
        mBind.btnPush31.setOnClickListener {
        }
        mBind.btnPush32.setOnClickListener {
        }

        mBind.btnStopPush1.setOnClickListener {
        }
        mBind.btnStopPush2.setOnClickListener {
        }
        mBind.btnStopPush3.setOnClickListener {

        }
        mBind.btnStopPush4.setOnClickListener {

        }
        mBind.btnStopPush5.setOnClickListener {

        }
        mBind.btnStopPush6.setOnClickListener {

        }
        mBind.btnStopPush7.setOnClickListener {

        }
        mBind.btnStopPush8.setOnClickListener {

        }
        mBind.btnStopPush9.setOnClickListener {

        }
        mBind.btnStopPush10.setOnClickListener {

        }
        mBind.btnStopPush11.setOnClickListener {

        }
        mBind.btnStopPush12.setOnClickListener {
        }
        mBind.btnStopPush13.setOnClickListener {

        }
        mBind.btnStopPush14.setOnClickListener {

        }
        mBind.btnStopPush15.setOnClickListener {

        }
        mBind.btnStopPush16.setOnClickListener {

        }
    }

    override fun initData() {
        activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        mExecutorService = Executors.newFixedThreadPool(32)

        mRtspUrlList.add(Constant.RTSP_URL1)
        mRtspUrlList.add(Constant.RTSP_URL2)
        mRtspUrlList.add(Constant.RTSP_URL3)
        mRtspUrlList.add(Constant.RTSP_URL4)
        mRtspUrlList.add(Constant.RTSP_URL5)
        mRtspUrlList.add(Constant.RTSP_URL6)
        mRtspUrlList.add(Constant.RTSP_URL7)
        mRtspUrlList.add(Constant.RTSP_URL8)
        mRtspUrlList.add(Constant.RTSP_URL1)
        mRtspUrlList.add(Constant.RTSP_URL2)
        mRtspUrlList.add(Constant.RTSP_URL3)
        mRtspUrlList.add(Constant.RTSP_URL4)
        mRtspUrlList.add(Constant.RTSP_URL5)
        mRtspUrlList.add(Constant.RTSP_URL6)
        mRtspUrlList.add(Constant.RTSP_URL7)
        mRtspUrlList.add(Constant.RTSP_URL8)
        mRtspUrlList.add(Constant.RTSP_URL1)
        mRtspUrlList.add(Constant.RTSP_URL2)
        mRtspUrlList.add(Constant.RTSP_URL3)
        mRtspUrlList.add(Constant.RTSP_URL4)
        mRtspUrlList.add(Constant.RTSP_URL5)
        mRtspUrlList.add(Constant.RTSP_URL6)
        mRtspUrlList.add(Constant.RTSP_URL7)
        mRtspUrlList.add(Constant.RTSP_URL8)
        mRtspUrlList.add(Constant.RTSP_URL1)
        mRtspUrlList.add(Constant.RTSP_URL2)
        mRtspUrlList.add(Constant.RTSP_URL3)
        mRtspUrlList.add(Constant.RTSP_URL4)
        mRtspUrlList.add(Constant.RTSP_URL5)
        mRtspUrlList.add(Constant.RTSP_URL6)
        mRtspUrlList.add(Constant.RTSP_URL7)
        mRtspUrlList.add(Constant.RTSP_URL8)

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

    override fun onDestroy() {
        super.onDestroy()
        mExecutorService?.shutdown()
    }

    private fun startPushChannel(rtspUrl: String, rtmpUrl: String) {
        FFmpegKit.executeAsync("-i $rtspUrl -c:v copy -c:a copy -f flv $rtmpUrl",
            { session ->
                val state = session.state
                val returnCode = session.returnCode

                // CALLED WHEN SESSION IS EXECUTED
                Log.d(TAG, String.format("FFmpeg process exited with state %s and rc %s.%s", state, returnCode, session.failStackTrace))
            }, object: LogCallback {
                override fun apply(log: com.arthenica.ffmpegkit.Log) {
                    Log.d(TAG, log.toString())
                }
            }, object: StatisticsCallback {
                override fun apply(statistics: Statistics?) {

                }
            }, mExecutorService)
    }

    private fun startPushAllChannel() {

        /**
         * RK3288板子
         * 推32路 实际只推出18路
         * CPU占用率：比较稳定维持在20%左右，系统占用10%
         * 16:25 20%
         * 内存使用情况：
         * 16:25
         * 176,798K: com.ancda.rtsppusher (pid 24818 / activities)
         * 16:35
         * 210,387K: com.ancda.rtsppusher (pid 24818 / activities)
         *
         * 推24路
         * CPU占用率：
         * 15:15 22%
         * 15:30 20%
         * 17:00 21%
         * 18:40 24%
         *
         * 内存使用情况：
         * 15:12
         * 196,691K: com.ancda.rtsppusher (pid 8988 / activities)
         * 15:30
         * 199,563K: com.ancda.rtsppusher (pid 8988 / activities)
         * 17:00
         * 225,915K: com.ancda.rtsppusher (pid 8988 / activities)
         * 18:40
         * 266,343K: com.ancda.rtsppusher (pid 8988 / activities)
         */
        mRtspUrlList.forEachIndexed { index, rtspUrl ->
            FFmpegKit.executeAsync("-i $rtspUrl -c:v copy -c:a copy -f flv ${mRtmpUrlList[index]}",
                { session ->
                    val state = session.state
                    val returnCode = session.returnCode

                    // CALLED WHEN SESSION IS EXECUTED
                    Log.d(TAG, String.format("FFmpeg process exited with state %s and rc %s.%s", state, returnCode, session.failStackTrace))
                }, object: LogCallback {
                override fun apply(log: com.arthenica.ffmpegkit.Log) {
                    Log.d(TAG, log.toString())
                }
            }, object: StatisticsCallback {
                override fun apply(statistics: Statistics?) {

                }
            }, mExecutorService)
        }
    }

    private fun startPush32Channel() {

    }
}