package com.ancda.rtsppusher.ui.preview.viewmodel

import android.util.Log
import com.ancda.base.viewmodel.BaseViewModel
import com.ancda.rtsppusher.constant.Constant
import com.ancda.rtsppusher.utils.log.ALog
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.LogCallback
import com.arthenica.ffmpegkit.Statistics
import com.arthenica.ffmpegkit.StatisticsCallback
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * author  : LLhon
 * date    : 2024/4/23 10:18.
 * des     :
 */
class PreviewViewModel : BaseViewModel() {

    private val TAG = "PreviewViewModel"
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

    fun testPush16() {
        startPush(0, Constant.RTSP_URL4)
        startPush(1, Constant.RTSP_URL2)
        startPush(2, Constant.RTSP_URL3)
        startPush(3, Constant.RTSP_URL4)
        startPush(4, Constant.RTSP_URL5)
        startPush(5, Constant.RTSP_URL6)
        startPush(6, Constant.RTSP_URL7)
        startPush(7, Constant.RTSP_URL8)
        startPush(8, Constant.RTSP_URL9)
        startPush(9, Constant.RTSP_URL10)
        startPush(10, Constant.RTSP_URL3)
        startPush(11, Constant.RTSP_URL2)
        startPush(12, Constant.RTSP_URL3)
        startPush(13, Constant.RTSP_URL4)
        startPush(14, Constant.RTSP_URL5)
        startPush(15, Constant.RTSP_URL6)
    }

    fun testPush() {
        startPush(0, Constant.RTSP_URL1)
        startPush(1, Constant.RTSP_URL2)
        startPush(2, Constant.RTSP_URL3)
        startPush(3, Constant.RTSP_URL4)
        startPush(4, Constant.RTSP_URL5)
        startPush(5, Constant.RTSP_URL6)
        startPush(6, Constant.RTSP_URL7)
        startPush(7, Constant.RTSP_URL8)
        startPush(8, Constant.RTSP_URL9)
        startPush(9, Constant.RTSP_URL10)
        startPush(10, Constant.RTSP_URL11)
        startPush(11, Constant.RTSP_URL12)
        startPush(12, Constant.RTSP_URL13)
        startPush(13, Constant.RTSP_URL1)
        startPush(14, Constant.RTSP_URL2)
        startPush(15, Constant.RTSP_URL3)
        startPush(16, Constant.RTSP_URL4)
        startPush(17, Constant.RTSP_URL5)
        startPush(18, Constant.RTSP_URL6)
        startPush(19, Constant.RTSP_URL7)
        startPush(20, Constant.RTSP_URL8)
        startPush(21, Constant.RTSP_URL9)
        startPush(22, Constant.RTSP_URL10)
        startPush(23, Constant.RTSP_URL11)
        startPush(24, Constant.RTSP_URL12)
        startPush(25, Constant.RTSP_URL13)
        startPush(26, Constant.RTSP_URL1)
        startPush(27, Constant.RTSP_URL2)
        startPush(28, Constant.RTSP_URL3)
        startPush(29, Constant.RTSP_URL4)
        startPush(30, Constant.RTSP_URL5)
        startPush(31, Constant.RTSP_URL6)
    }

    /**
     * 1、断流时间点
     * 2、摄像头断开时间点
     * 3、开始推流时间点
     * 4、CPU占用率
     * 5、内存使用率
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
                ALog.eToFile(TAG, "FFmpegSessionCompleteCallback: [${curRtspUrl}]")
                ALog.eToFile(TAG, String.format("FFmpeg process exited with state %s and rc %s.%s", state, returnCode, session.failStackTrace))
            }, object: LogCallback {
                override fun apply(log: com.arthenica.ffmpegkit.Log) {
                    Log.d(TAG, log.toString())
                }
            }, object: StatisticsCallback {
                override fun apply(statistics: Statistics?) {

                }
            }, mExecutorService)
    }

    fun release() {
        mExecutorService?.shutdown()
        mExecutorService = null
    }
}