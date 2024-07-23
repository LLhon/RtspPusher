package com.ancda.rtsppusher.ui

import android.os.Bundle
import androidx.media3.exoplayer.ExoPlayer
import com.ancda.base.viewmodel.BaseViewModel
import com.ancda.rtsppusher.base.BaseActivity
import com.ancda.rtsppusher.databinding.ActivityPreview16Binding

/**
 * author  : LLhon
 * date    : 2024/4/11 11:07.
 * des     :
 */
class Preview16Activity : BaseActivity<BaseViewModel, ActivityPreview16Binding>() {

    private val RTSP_URL = "rtsp://admin:ancda123@192.168.2.215:554/cam/realmonitor?channel=1&subtype=0&unicast=true&proto=Onvif" //能推
    private val RTSP_URL2 = "rtsp://admin:admin@192.168.6.89:554/11" //推不了
    private val RTSP_URL3 = "rtsp://admin:ancda123@192.168.6.66:554/Streaming/Channels/1" //能推
    private val RTSP_URL4 = "rtsp://admin:ancda123@192.168.6.2:554" //能推
    private val RTSP_URL5 = "rtsp://admin:ancda123@192.168.6.13:554" //能推
    private val RTSP_URL6 = "rtsp://admin:ancda123@192.168.6.223:554" //能推
    private val RTSP_URL7 = "rtsp://admin:admin@192.168.6.172:554/11" //能推
    private val RTSP_URL8 = "rtsp://admin:Admin123@192.168.6.14:554/live/0/MAIN" //能推

    private lateinit var mPlayer: ExoPlayer
    private val mRtspUrlList = mutableListOf<String>()
//    private val mRtspPlayerList = mutableListOf<RtspPlayer>()
//    private val mRtspViewList = mutableListOf<VLCVideoLayout>()
//    private val mRtspViewList = mutableListOf<SurfaceView>()

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initData() {
        mRtspUrlList.add(RTSP_URL)
        mRtspUrlList.add(RTSP_URL2)
        mRtspUrlList.add(RTSP_URL3)
        mRtspUrlList.add(RTSP_URL4)
//        mRtspUrlList.add(RTSP_URL5)
//        mRtspUrlList.add(RTSP_URL6)
//        mRtspUrlList.add(RTSP_URL7)
//        mRtspUrlList.add(RTSP_URL8)
//        mRtspUrlList.add(RTSP_URL)
//        mRtspUrlList.add(RTSP_URL2)
//        mRtspUrlList.add(RTSP_URL3)
//        mRtspUrlList.add(RTSP_URL4)
//        mRtspUrlList.add(RTSP_URL5)
//        mRtspUrlList.add(RTSP_URL6)
//        mRtspUrlList.add(RTSP_URL7)
//        mRtspUrlList.add(RTSP_URL8)

//        mRtspViewList.add(mBind.previewView1)
//        mRtspViewList.add(mBind.previewView2)
//        mRtspViewList.add(mBind.previewView3)
//        mRtspViewList.add(mBind.previewView4)
//        mRtspViewList.add(mBind.previewView5)
//        mRtspViewList.add(mBind.previewView6)
//        mRtspViewList.add(mBind.previewView7)
//        mRtspViewList.add(mBind.previewView8)
//        mRtspViewList.add(mBind.previewView9)
//        mRtspViewList.add(mBind.previewView10)
//        mRtspViewList.add(mBind.previewView11)
//        mRtspViewList.add(mBind.previewView12)
//        mRtspViewList.add(mBind.previewView13)
//        mRtspViewList.add(mBind.previewView14)
//        mRtspViewList.add(mBind.previewView15)
//        mRtspViewList.add(mBind.previewView16)


        initVlcPlayer()

//        initMediaPlayer()
    }

    override fun onStop() {
        super.onStop()
//        mRtspPlayerList.forEach {
//            it.stopPlayer()
//        }
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseVlcPlayer()
        releaseMediaPlayer()
        releaseExoPlayer()
    }

    private fun initExoPlayer() {
//        val mediaSource = RtspMediaSource.Factory().createMediaSource(MediaItem.fromUri(RTSP_URL))
//        mExoPlayer = ExoPlayer.Builder(this).build()
//        mExoPlayer?.apply {
//            setMediaSource(mediaSource)
//            setVideoSurfaceView(mBind.previewView)
//            prepare()
//        }
    }

    private fun releaseExoPlayer() {
        mPlayer.release()
    }

    private fun releaseVlcPlayer() {

    }

    private fun initMediaPlayer() {
//        for (i in 0 until mRtspUrlList.size) {
//            val player = MediaPlayer.create(this, Uri.parse(mRtspUrlList[i]))
//            mRtspPlayerList.add(player)
//            mRtspViewList[i].holder.addCallback(object: SurfaceHolder.Callback {
//                override fun surfaceCreated(p0: SurfaceHolder) {
//                    mRtspPlayerList[i].setDisplay(p0)
//                    mRtspPlayerList[i].start()
//                }
//
//                override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
//
//                }
//
//                override fun surfaceDestroyed(p0: SurfaceHolder) {
//                    mRtspPlayerList[i].stop()
//                }
//            })
//        }
    }

    private fun releaseMediaPlayer() {

    }

    /**
     * vlc播放器：
     *      播8路rtsp流 cpu占用率：50% 内存使用率：341MB
     *      播16路rtsp流 闪退
     */
    private fun initVlcPlayer() {
//        for (i in 0 until mRtspUrlList.size) {
//            val player = RtspPlayer(mRtspUrlList[i], mRtspViewList[i], this)
//            mRtspPlayerList.add(player)
//        }
    }
}