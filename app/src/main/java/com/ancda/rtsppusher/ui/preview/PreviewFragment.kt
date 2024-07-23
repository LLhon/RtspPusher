package com.ancda.rtsppusher.ui.preview

import android.os.Bundle
import android.util.Log
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.ancda.rtsppusher.appViewModel
import com.ancda.rtsppusher.base.BaseFragment
import com.ancda.rtsppusher.databinding.FragmentPreviewBinding
import com.ancda.rtsppusher.player.SampleListener
import com.ancda.rtsppusher.ui.adapter.IpcPreviewAdapter
import com.ancda.rtsppusher.ui.preview.viewmodel.PreviewViewModel
import com.blankj.utilcode.util.ToastUtils
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.model.VideoOptionModel
import com.shuyu.gsyvideoplayer.player.IjkPlayerManager
import com.shuyu.gsyvideoplayer.player.PlayerFactory
import com.shuyu.gsyvideoplayer.player.SystemPlayerManager
import tv.danmaku.ijk.media.exo2.Exo2PlayerManager
import tv.danmaku.ijk.media.player.IjkMediaPlayer


/**
 * author  : LLhon
 * date    : 2024/4/22 19:09.
 * des     :
 */
class PreviewFragment : BaseFragment<PreviewViewModel, FragmentPreviewBinding>() {

    private var mAdapter: IpcPreviewAdapter? = null
    private var mCurPlayPosition = 0
//    private var mRtspPlayer: RtspPlayer? = null

    companion object {
        const val TAG = "PreviewFragment"
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBind.rvIpc.layoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false)
        mAdapter = IpcPreviewAdapter()
        mAdapter?.run {
            setOnItemClickListener { _, _, position ->
                mAdapter?.mSelIndex = position
                mAdapter?.notifyDataSetChanged()
                mCurPlayPosition = position
                // TODO: test
                playCamera(mAdapter!!.data[position].profiles[0].rtspUrl)
            }
        }
        mBind.rvIpc.adapter = mAdapter

//        mBind.playerView.player = ExoPlayer.Builder(mActivity).build()

        mBind.btnPush16Stream.setOnClickListener {
            //推16路流
//            mViewModel.testPush16()
//            ToastUtils.showShort("推流成功")
        }
        mBind.btnPushStream.setOnClickListener {
            //启动推流
//            mViewModel.testPush()
//            ToastUtils.showShort("推流成功")
        }
    }

    override fun initData() {
        Log.d(TAG, "initData")

    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
//        mBind.playerView.onResume()
    }

    override fun onPause() {
        super.onPause()
//        mBind.playerView.onPause()
    }

    override fun lazyLoadData() {
        Log.d(TAG, "lazyLoadData")
        //        mRtspPlayer = RtspPlayer(mBind.playerView, mActivity)
        initGSYVideoPlayer()

        mAdapter?.apply {
            setList(appViewModel.mIpcListData.value)
            if (data.size > 0) {
                // TODO: test
                playCamera(data[mCurPlayPosition].profiles[0].rtspUrl)
            }
        }

//        mViewModel.initData()
    }

    override fun createObserver() {
        appViewModel.mIpcListData.observe(this) {
            mAdapter?.setList(it)

            //默认预览第一个摄像头
            mAdapter?.apply {
                if (data.size > 0) {
                    // TODO: test
                    playCamera(data[mCurPlayPosition].profiles[0].rtspUrl)
                }
            }
        }
    }

    private fun initGSYVideoPlayer() {

        //切换内核
        PlayerFactory.setPlayManager(IjkPlayerManager::class.java)

        /***************rtsp 配置****************/
        val list = ArrayList<VideoOptionModel>()
        val option = VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "allowed_media_types", "video")
        val option2 = VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "timeout", 20000)
        val option3 = VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "rtsp_transport", "tcp") //优先用tcp
        val option4 = VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "rtsp_flags", "prefer_tcp")
        //关闭播放器缓冲，这个必须关闭，否则会出现播放一段时间后，一直卡主，控制台打印 FFP_MSG_BUFFERING_START
        val option5 = VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "packet-buffering", 0)
        val option6 = VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "buffer_size", 1316) //
        val option7 = VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "infbuf", 1) // 无限读
        val option8 = VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzedmaxduration", 100)//分析码流时长:默认1024*1000
        val option9 = VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "probesize", 10240) //
        val option10 = VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "flush_packets", 1) //
        list.add(option)
        list.add(option2)
        list.add(option3)
        list.add(option4)
        list.add(option5)
        list.add(option6)
        list.add(option7)
        list.add(option8)
        list.add(option9)
        list.add(option10)
        GSYVideoManager.instance().optionModelList = list
        /***************rtsp 配置****************/
    }

    private fun playCamera(rtspUrl: String?) {
        Log.d(TAG, "playCamera: $rtspUrl")

        mBind.videoPlayer.apply {
            setUp(rtspUrl?:"", false, "")
            startPlayLogic()

            setVideoAllCallBack(object : SampleListener() {
                //开始加载
                override fun onStartPrepared(url: String?, vararg objects: Any?) {
                    Log.i(TAG, "onStartPrepared")
                    mBind.flPlayState.isVisible = true
                    mBind.pbLoadingState.isVisible = true
                    mBind.tvErrorState.isGone = true
                }

                //加载成功
                override fun onPrepared(url: String?, vararg objects: Any?) {
                    Log.i(TAG, "onPrepared")
                    mBind.flPlayState.isGone = true

                    //静音
                    GSYVideoManager.instance().isNeedMute = true
                }

                //播放错误
                override fun onPlayError(url: String?, vararg objects: Any?) {
                    Log.e(TAG, "onPlayError")
                    mBind.flPlayState.isVisible = true
                    mBind.pbLoadingState.isGone = true
                    mBind.tvErrorState.isVisible = true
                }
            })
        }

//        mBind.playerView.player?.run {
//            stop()
//            // Build the media item.
//            val mediaItem = MediaItem.fromUri(rtspUrl ?: "")
//            // Set the media item to be played.
//            setMediaItem(mediaItem)
//            // Prepare the player.
//            prepare()
//            // Start the playback.
//            play()
//
//            addListener(object: Player.Listener {
//                override fun onPlayerError(error: PlaybackException) {
//                    super.onPlayerError(error)
//                    mCurPlayPosition = -1
//                }
//            })
//        }

//        mRtspPlayer?.play(rtspUrl?:"")
//        mRtspPlayer?.setEventListener {
//            when (it.type) {
//                MediaPlayer.Event.Buffering -> {
//                    Log.i("RtspPlayer", "MediaPlayer.Event.Buffering")
////                    mBind.flPlayState.isVisible = true
////                    mBind.pbLoadingState.isVisible = true
////                    mBind.tvErrorState.isGone = true
//                }
//                MediaPlayer.Event.Playing -> {
//                    Log.i("RtspPlayer", "MediaPlayer.Event.Playing")
//                    mBind.flPlayState.isGone = true
//                }
//                MediaPlayer.Event.Stopped -> {
//                    Log.e("RtspPlayer", "MediaPlayer.Event.Stopped")
//                }
//                MediaPlayer.Event.EncounteredError -> {
//                    Log.e("RtspPlayer", "MediaPlayer.Event.EncounteredError")
////                    mBind.flPlayState.isVisible = true
////                    mBind.pbLoadingState.isGone = true
////                    mBind.tvErrorState.isVisible = true
//                }
//            }
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        mBind.playerView.player?.release()
//        mRtspPlayer?.stop()
        mBind.videoPlayer.release()
    }

    override fun onDestroy() {
        super.onDestroy()
        mBind.videoPlayer.setVideoAllCallBack(null)
        GSYVideoManager.releaseAllVideos()
        mViewModel.release()
    }
}