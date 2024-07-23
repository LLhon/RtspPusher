package com.ancda.rtsppusher.ui.preview

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.SurfaceHolder
import com.ancda.base.viewmodel.BaseViewModel
import com.ancda.rtsppusher.base.BaseActivity
import com.ancda.rtsppusher.constant.Constant
import com.ancda.rtsppusher.databinding.ActivityTestPreviewBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.rtsp.RtspMediaSource

/**
 * author  : LLhon
 * date    : 2024/4/30 11:17.
 * des     :
 */
class TestPreviewActivity : BaseActivity<BaseViewModel, ActivityTestPreviewBinding>() {

    private var mExoPlayer: ExoPlayer? = null

    companion object {
        fun launch(context: Context) {
            context.startActivity(Intent(context, TestPreviewActivity::class.java))
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
//        initMediaPlayer()
        initExoPlayer()
    }

    private fun initMediaPlayer() {
        val player = MediaPlayer.create(this, Uri.parse(Constant.RTSP_URL6))
        mBind.playerView.holder.addCallback(object: SurfaceHolder.Callback {
            override fun surfaceCreated(p0: SurfaceHolder) {
                player.setDisplay(p0)
                player.start()
            }

            override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {

            }

            override fun surfaceDestroyed(p0: SurfaceHolder) {
                player.stop()
            }
        })
    }

    private fun initExoPlayer() {
        val mediaSource = RtspMediaSource.Factory().createMediaSource(MediaItem.fromUri(Constant.RTSP_URL6))
        mExoPlayer = ExoPlayer.Builder(this).build()
        mExoPlayer?.apply {
            setMediaSource(mediaSource)
            setVideoSurfaceView(mBind.playerView)
            prepare()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mExoPlayer?.release()
    }
}