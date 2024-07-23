package com.ancda.rtsppusher.common

import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import com.ancda.rtsppusher.http.apiService
import com.qiniu.android.http.ResponseInfo
import com.qiniu.android.storage.Configuration
import com.qiniu.android.storage.Recorder
import com.qiniu.android.storage.UpCancellationSignal
import com.qiniu.android.storage.UpCompletionHandler
import com.qiniu.android.storage.UploadManager
import com.qiniu.android.storage.UploadOptions
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.ref.SoftReference
import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock
import javax.crypto.KeyGenerator

/**
 * 七牛上传类。每个上传任务（key）必须初始化独立的QiniuUpload对象，不能再主线程调用同步上传方法
 * 注意：重用uploadManager。一般地，只需要创建一个uploadManager对象，内部包含一个线程池。所有的上传线程由七牛sdk管理。
 * 可以修改下面的静态常量以适应不同的需求。
 */
class QiniuUpload {

    companion object {
        const val CHUNKSIZE = 256 * 1024 //分片上传时，每片的大小。 默认256K
        const val THRESHHOLD = 512 * 1024 // 启用分片上传阀值。默认512K
        const val CONNECTTIMEOUT = 10 // 链接超时。默认10秒
        const val RESPONSETIMEOUT = 10 // 服务器响应超时。默认60秒
    }

    //断点记录文件保存的文件夹路径
    var RECORDERDIRPATH = ""
    private var uploadManager: UploadManager? = null
    private var token: String? = null
    //用于线程的休眠。以等待七牛返回的结果
    private val lock = Object()
    private var syncResult: ResponseInfo? = null
    private var isCancelled = false
    private var srCallback: SoftReference<UploadCallback>? = null
    private var handler: Handler? = null
    private var curKeyForSync: String? = ""
    private var keyLock: ReadWriteLock? = null

    init {
        if (uploadManager == null) {
            val recorder: Recorder? = null // recorder分片上传时，已上传片记录器。默认null
            val keyGen: KeyGenerator? = null // keyGen 分片上传时，生成标识符，用于片记录器区分是那个文件的上传记录
            //            try {
//                File path = FileUtils.getDiskCacheDir(context, "qiniu");
//                if (!TextUtils.isEmpty(path.getAbsolutePath())) {
//                    recorder = new FileRecorder(path.getAbsolutePath());
//                }
//            } catch (Exception e) {
//                Log.w("QiniuUpload", "断点记录文件保存的文件夹路径不可用");
//            }
//            if (recorder != null) {
//                //默认使用key的url_safe_base64编码字符串作为断点记录文件的文件名
//                //避免记录文件冲突（特别是key指定为null时），也可自定义文件名(下方为默认实现)：
//                keyGen = new KeyGenerator() {
//                    public String gen(String key, File file) {
//                        // 不必使用url_safe_base64转换，uploadManager内部会处理
//                        // 该返回值可替换为基于key、文件内容、上下文的其它信息生成的文件名
//                        return key + "_._" + new StringBuffer(file.getAbsolutePath()).reverse();
//                    }
//                };
//            }
            val config = Configuration.Builder()
                .chunkSize(CHUNKSIZE)
                .putThreshhold(THRESHHOLD)
                .connectTimeout(CONNECTTIMEOUT)
                .responseTimeout(RESPONSETIMEOUT) //                    .recorder(recorder, keyGen)
                //                    .zone(Zone.zone0) // 设置区域，指定不同区域的上传域名、备用域名、备用IP。
                .build()
            uploadManager = UploadManager(config)
        }
        keyLock = ReentrantReadWriteLock()
    }

    fun upload(filePath: String?, key: String?, callback: UploadCallback?) {
        if (uploadManager == null || TextUtils.isEmpty(filePath) || TextUtils.isEmpty(key)) {
            throw RuntimeException("Please check filePath or key")
        }
        srCallback = if (callback != null) {
            SoftReference(callback)
        } else {
            null
        }
        isCancelled = false
        if (TextUtils.isEmpty(token)) {
            //调接口获取token
//            AndroidEventManager.getInstance().pushEventEx(
//                MyEventCode.EventCode_GetToken,
//                this, filePath, key
//            )
            // TODO: 调接口获取上传token 
            return
        }
        uploadManager!!.put(
            filePath,
            key,
            token,
            upCompletionHandler,
            uploadOptions
        )
    }

    fun uploadForSync(filePath: String?, key: String?, timeout: Long): ResponseInfo? {
        return uploadForSync(filePath, key, timeout, null)
    }

    /**
     * @return  token获取不到是返回null，否则返回七牛相关信息
     */
    @Synchronized
    fun uploadForSync(filePath: String?, key: String?, timeout: Long, callback: UploadCallback.ProgressCallback?): ResponseInfo? {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw RuntimeException("Can not run on the main thread")
        }
        require(!(uploadManager == null || TextUtils.isEmpty(filePath) || TextUtils.isEmpty(key))) { "Please check filePath or key" }
        if (!TextUtils.isEmpty(curKeyForSync)) {
            throw RuntimeException("The last task was not completed")
        }
        srCallback = if (callback != null) {
            SoftReference(callback)
        } else {
            null
        }
        isCancelled = false
        if (TextUtils.isEmpty(token)) {
//            val event: Event = AndroidEventManager.getInstance().runEvent(
//                MyEventCode.EventCode_GetToken,
//                this
//            )
//            if (event.isSuccess()) {
//                token = "" + event.getReturnParamAtIndex(0)
//            } else {
//                return null
//            }
            // TODO: 调接口获取上传token 
            GlobalScope.launch {
                val response = apiService.getQiniuToken()
            }
        }
        syncResult = null
        if (handler == null) {
            handler = Handler(Looper.getMainLooper())
        }
        try {
            keyLock!!.writeLock().lock()
            curKeyForSync = key
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            keyLock!!.writeLock().unlock()
        }
        uploadManager!!.put(
            filePath,
            key,
            token,
            syncUpCompletionHandler,
            uploadOptions
        )
        handler!!.removeCallbacks(timeoutRun)
        handler!!.postDelayed(timeoutRun, timeout)
        try {
            synchronized(lock) {
                lock.wait()
            }
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        try {
            keyLock!!.writeLock().lock()
            curKeyForSync = null
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            keyLock!!.writeLock().unlock()
        }
        return syncResult
    }

    /**
     * 上传结束回调
     */
    private val upCompletionHandler =
        UpCompletionHandler { key, info, res -> //res包含hash、key等信息，具体字段取决于上传策略的设置
            if (srCallback != null) {
                val uploadCallback = srCallback!!.get()
                if (uploadCallback != null && uploadCallback is UploadCallback.ResultCallback) {
                    uploadCallback.result(key, info, res)
                }
            }
        }

    /**
     * 同步上传时的上传结束回调
     */
    private val syncUpCompletionHandler =
        UpCompletionHandler { key, info, res ->
            //res包含hash、key等信息，具体字段取决于上传策略的设置
            if (keyLock!!.readLock().tryLock()) {
                try {
                    if (TextUtils.isEmpty(curKeyForSync) || curKeyForSync != key) {
                        return@UpCompletionHandler
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                } finally {
                    keyLock!!.readLock().unlock()
                }
                handler!!.removeCallbacks(timeoutRun)
                if (info.statusCode == 401) { // token过期了
                    token = null
                }
                try {
                    synchronized(lock) {
                        syncResult = info
                        lock.notify()
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        }

    private val timeoutRun = Runnable {
        try {
            cancell()
            synchronized(lock) {
                syncResult = null
                lock.notify()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 上传选项，包含上传进度回调函数、取消选项
     */
    private val uploadOptions = UploadOptions(null, null, false,
        { key, percent ->
            if (srCallback != null) {
                val uploadCallback = srCallback!!.get()
                if (uploadCallback != null && uploadCallback is UploadCallback.ProgressCallback) {
                    uploadCallback.progress(key, percent)
                }
            }
        },
        object : UpCancellationSignal {
            override fun isCancelled(): Boolean {
                return isCancelled
            }
        })

    fun cancell() {
        isCancelled = true
    }


}