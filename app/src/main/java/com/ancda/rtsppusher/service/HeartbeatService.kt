package com.ancda.rtsppusher.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import android.util.Log
import com.ancda.rtsppusher.MyApp
import com.ancda.rtsppusher.common.QiniuUpload
import com.ancda.rtsppusher.data.HeartbeatResponse
import com.ancda.rtsppusher.utils.AdwApiHelper
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.Utils
import com.blankj.utilcode.util.ZipUtils
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.net.URLEncoder
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * author  : LLhon
 * date    : 2024/6/27 9:46.
 * des     : 心跳服务
 */
class HeartbeatService : Service() {

    private lateinit var heartbeatThread: HeartbeatThread
    private lateinit var heartbeatLooper: Looper
    private lateinit var heartbeatProcessHandler: HeartbeatProcessHandler
    private var isVersionUpdating = false //是否正在更新版本

    companion object {

        const val TAG = "HeartbeatService"

        fun startService(context: Context) {
            context.startService(Intent(context, HeartbeatService::class.java))
        }

        fun startService(context: Context, isHeartbeat: Boolean) {
            context.startService(Intent(context, HeartbeatService::class.java).putExtra("isHeartbeat", isHeartbeat))
        }
    }

    override fun onBind(p0: Intent?): IBinder?  = null

    override fun onCreate() {
        //心跳处理线程（队列）
        heartbeatThread = HeartbeatThread()
        val ht = HandlerThread("HeartProcess")
        ht.start()
        heartbeatLooper = ht.looper
        heartbeatProcessHandler = HeartbeatProcessHandler(heartbeatLooper)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        var isHeartbeat = false
        if (intent != null) {
            isHeartbeat = intent.getBooleanExtra("isHeartbeat", false)
        }
        if (isHeartbeat) {
            if (heartbeatThread.state == Thread.State.TERMINATED) {
                heartbeatThread.setStop()
                heartbeatThread = HeartbeatThread()
                heartbeatThread.start()
            } else {
                if (!heartbeatThread.isAlive && heartbeatThread.state == Thread.State.NEW) {
                    heartbeatThread.start()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    inner class HeartbeatThread : Thread() {
        private var isStop = false

        override fun run() {
            while (!isStop) {
                try {
                    if (!isVersionUpdating && MyApp.isNetworkAvailable) {
                        pushHeartbeat()
                    }
                    if (!isStop) {
                        sleep(MyApp.heartbeatInterval)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        fun setStop() {
            this.isStop = true
            interrupt()
        }
    }

    private fun pushHeartbeat() {
        Log.d(TAG, "pushHeartbeat...")
        // TODO: 调心跳接口
    }

    inner class HeartbeatProcessHandler(looper: Looper) : Handler(looper) {

        override fun handleMessage(msg: Message) {
            val status: HeartbeatResponse = msg.obj as HeartbeatResponse
            processHeartbeat(status)
        }
    }

    private fun processHeartbeat(status: HeartbeatResponse) {
        if (status.reboot == 1) {
            //重启设备
            AdwApiHelper.reboot()
            return
        }
        if (status.requestlog == 1) {
            //服务器请求上传日志
            // TODO: 调上传日志的接口
            uploadLog()
        }

    }

    private fun uploadLog() {
        var upString = URLEncoder.encode("设备ID :" + MyApp.deviceID, "UTF-8").trimIndent()
        val format = SimpleDateFormat("yyyy-MM-dd-HH:mm:ss", Locale.CHINA)
        upString = "$upString${URLEncoder.encode("上传时间 :" + format.format(Date()), "UTF-8")}".trimIndent()
        val dir = File(Environment.getExternalStorageDirectory().absolutePath + File.separator + "crash")
        if (dir.exists()) {
            val list = dir.list()
            var time: Long = 0
            var newfile: File? = null
            for (i in list.indices) {
                val file = File(dir, list[i])
                if (file.lastModified() > time) {
                    newfile = file
                    time = file.lastModified()
                }
            }
            if (newfile != null) {
                upString = upString + URLEncoder.encode("崩潰日志：${readString3(newfile)}".trimIndent(), "UTF-8") + "\r\n"
            }
        }

        //上传系统日志
        //1.打包成zip文件
        var isSucceed = false
        val srcLogFile = File(Utils.getApp().filesDir.absolutePath + File.separator + "Log")
        val fileKey = System.currentTimeMillis().toString() + "Log.zip"
        val targetLogFile = File(Utils.getApp().externalCacheDir!!.absolutePath + "/" + fileKey)
        if (srcLogFile.exists() && srcLogFile.listFiles().size > 0) {
            try {
                isSucceed = ZipUtils.zipFile(srcLogFile, targetLogFile)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        //2.上传七牛
        if (isSucceed) {
            //上传到七牛
            val qiniuUpload = QiniuUpload()
            val responseInfo = qiniuUpload.uploadForSync(targetLogFile.absolutePath,
                "temp/AttendanceMachine/$fileKey",
                60000
            )
            upString = if (responseInfo != null && responseInfo.isOK) {
                "$upString${URLEncoder.encode("系统日志zip包：https://mediatx.ancda.com/temp/AttendanceMachine/$fileKey", "UTF-8")}".trimIndent()
            } else {
                "$upString${URLEncoder.encode("系统日志zip包上传失败", "UTF-8")}".trimIndent()
            }
            if (targetLogFile.exists()) { //删除旧文件
                targetLogFile.delete()
            }
            if (srcLogFile.exists()) {
                srcLogFile.delete()
            }
        }
        upString = if (FileUtils.isFileExists("/data/anr/traces.txt")) {
            val qiniuUpload = QiniuUpload()
            val key: String = MyApp.deviceID + "/" + System.currentTimeMillis() + "/anr_traces.txt"
            val responseInfo = qiniuUpload.uploadForSync("/data/anr/traces.txt", key, 60000)
            if (responseInfo != null && responseInfo.isOK) {
                "$upString${URLEncoder.encode("anr日志：https://mediatx.ancda.com/$key", "UTF-8")}".trimIndent()
            } else {
                "$upString${URLEncoder.encode("anr日志上传失败", "UTF-8")}".trimIndent()
            }
        } else {
            "$upString${URLEncoder.encode("没发现anr日志", "UTF-8")}".trimIndent()
        }
        val mapValues = HashMap<String, String>()
        mapValues["cardnum"] = MyApp.deviceID
        mapValues["logs"] = upString
        // TODO: 调上传日志接口 
//        doPost(MyEventUrl.getUploadLog() + "?token=" + token, mapValues)
    }

    private fun readString3(file: File): String {
        var str = ""
        str = try {
            val fis = FileInputStream(file)
            // size  为字串的长度 ，这里一次性读完
            val size = fis.available()
            val buffer = ByteArray(size)
            fis.read(buffer)
            fis.close()
            String(buffer, Charset.forName("UTF-8"))
        } catch (e: IOException) {
            "读取出错了:$e"
        }
        return str
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy()")
        heartbeatLooper.quit()
        heartbeatThread.setStop()
    }
}