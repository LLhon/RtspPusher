//package com.ancda.rtsppusher.player
//
//import android.content.Context
//import android.net.Uri
//import android.util.Log
//import org.videolan.libvlc.LibVLC
//import org.videolan.libvlc.LibVLCFactory
//import org.videolan.libvlc.Media
//import org.videolan.libvlc.MediaPlayer
//import org.videolan.libvlc.util.VLCVideoLayout
//
///**
// * author  : LLhon date    : 2024/4/7 16:04. des     :
// */
//class RtspPlayer(playerView: VLCVideoLayout?, context: Context?) {
//
//    //ExoPlayer player;
//    var media: Media? = null
//    var mediaPlayer: MediaPlayer
//    var libVlc: LibVLC
//
//    init {
//        val options = ArrayList<String>()
////        options.add("--rtsp-tcp") //强制rtsp-tcp，加快加载视频速度
//        libVlc = LibVLC(context, options)
//        mediaPlayer = MediaPlayer(libVlc)
//        mediaPlayer.attachViews(playerView!!, null, false, false)
//
////        MediaSource mediaSource =
////                new RtspMediaSource.Factory()
////                        .createMediaSource(MediaItem.fromUri(url));
////        player = new ExoPlayer.Builder(context)
////                .setMediaSourceFactory(new RtspMediaSource.Factory().setDebugLoggingEnabled(true))
////                .build();
////        playerView.setPlayer(player);
////        player.setMediaSource(mediaSource);
////        player.prepare();
////        player.play();
//
//
////        Thread t = new Thread() {
////            public void run() {
////
////            }
////        };
////        t.start();
//    }
//
//    fun play(url: String) {
//        mediaPlayer.stop()
//        media = Media(libVlc, Uri.parse(url))
//        media?.apply {
//            setHWDecoderEnabled(true, false)
////            addOption(":network-caching=600")
//            addOption(":no-audio")
//            addOption(":quiet")
////            addOption(":codec=mediacodec,iomx,all")
//            mediaPlayer.media = media
//            media?.release()
//        }
//        mediaPlayer.play()
//    }
//
//    fun setEventListener(listener: MediaPlayer.EventListener) {
//        mediaPlayer.setEventListener(listener)
//    }
//
//    fun stop() {
//        mediaPlayer.stop()
//        mediaPlayer.vlcVout.detachViews()
//        mediaPlayer.release()
//        libVlc.release()
//        Log.i("INFO", "Play stopped")
//    }
//}
