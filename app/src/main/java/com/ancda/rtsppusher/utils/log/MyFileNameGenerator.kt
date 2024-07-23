package com.ancda.rtsppusher.utils.log

import android.text.TextUtils
import com.blankj.utilcode.util.FileUtils
import com.elvishew.xlog.printer.file.naming.FileNameGenerator
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * 按日志文件大小分文件。当总日志文件超过最大限制时删除最早的日志文件
 * 因为框架是将保持日志任务添加到任务队列后排队执行的。所以可以不用考虑多线程安全问题。
 */
class MyFileNameGenerator(
    private val logDir: String,
    private val maxCacheSize: Int,
    private val maxFileSize: Int
) : FileNameGenerator {

    @Volatile
    private var curFileName: String? = null

    @Volatile
    private var curCacheSize: Long = 0 //不包括正在写入的日志文件

    override fun isFileNameChangeable(): Boolean {
        return true
    }

    override fun generateFileName(logLevel: Int, timestamp: Long): String {
        if (TextUtils.isEmpty(curFileName) || !FileUtils.isFileExists(logDir + File.separator + curFileName)
            || !isCanUseFile(curFileName, timestamp)) {  //第一次需要分析缓存目录，初始化必要的数据
            curFileName = null
            checkCacheDir(timestamp)
        }
        if (TextUtils.isEmpty(curFileName)) {
            curFileName = createFileNameForUnique(timestamp, 1)
            curCacheSize = 0
        } else {
            var fileSize: Long = File(logDir + File.separator + curFileName).length()
            while (curCacheSize + fileSize >= maxCacheSize) {  //超过最大缓存限制。需要删除最早的日志文件
                val oldFile = oldFile ?: break
                if (oldFile.name == curFileName) {  //删除当前日志文件
                    if (FileUtils.delete(oldFile)) {
                        curFileName = null
                        fileSize = 0
                    } else {
                        break
                    }
                } else {
                    val deleteSize: Long = oldFile.length()
                    if (FileUtils.delete(oldFile)) {
                        if (deleteSize > 0) {
                            curCacheSize -= deleteSize
                            if (curCacheSize < 0) {
                                curCacheSize = 0
                            }
                        }
                    } else {
                        break
                    }
                }
            }
            if (fileSize >= maxFileSize) { //超过最大单文件大小限制。需要分文件
                curFileName = createFileNameForUnique(timestamp, getIndexFromFileName(curFileName))
                curCacheSize += fileSize
            } else {
                if (TextUtils.isEmpty(curFileName)) {
                    curFileName = createFileNameForUnique(timestamp, 1)
                }
            }
        }
        return curFileName!!
    }

    /**
     * 检测缓存目录。并找到或生成当前可以写入的日志文件，同时计算除了正在写入文件的缓存总大小
     */
    private fun checkCacheDir(timestamp: Long) {
        val files = cacheFileList
        if (files.isEmpty()) {
            curCacheSize = 0
            curFileName = createFileNameForUnique(timestamp, 1)
            return
        }
        var size: Long = 0
        var canUseFile: File? = null

        //将最新的文件作为当前可以写入的文件
//        long lastFileModified = Long.MIN_VALUE;
//        for (int i = 0; i < files.size(); i++) {
//            long fileLastModified = FileUtils.getFileLastModified(files.get(i));
//            if (fileLastModified > lastFileModified) {
//                lastFileModified = fileLastModified;
//                canUseFile = files.get(i);
//            } else {
//                size += FileUtils.getFileSize(files.get(i));
//            }
//        }

        //找到timestamp对应的当天的最后一个文件作为可以写入的文件
        var lastIndex = 0
        for (i in files.indices) {
            val fileIndex = getIndexFromFileName(files[i].name)
            if (isCanUseFile(files[i].name, timestamp) && fileIndex > lastIndex) {
                lastIndex = fileIndex
                canUseFile = files[i]
            } else {
                size += files[i].length()
            }
        }
        curCacheSize = size
        curFileName =
            if (canUseFile != null) canUseFile.name else createFileNameForUnique(timestamp, 1)
    }

    private fun isCanUseFile(fileName: String?, timestamp: Long): Boolean {
        val dataFormat = getDataFormat(timestamp)
        return fileName!!.contains(dataFormat)
    }

    private val oldFile: File?
        /**
         * 获取最早的日志文件
         */
        get() {
            val files = cacheFileList
            if (files.isEmpty()) {
                return null
            }
            var oldFile: File? = null
            var oldFileModified = Long.MAX_VALUE
            for (i in files.indices) {
                val fileLastModified: Long = FileUtils.getFileLastModified(files[i])
                if (fileLastModified < oldFileModified) {
                    oldFileModified = fileLastModified
                    oldFile = files[i]
                }
            }
            return oldFile
        }

    private val cacheFileList: List<File>
        /**
         * 获取缓存文件列表
         */
        get() = FileUtils.listFilesInDirWithFilter(logDir) { pathname ->
            val name = pathname.name
            name.startsWith(FILE_NAME_PREFIX) && name.endsWith(".$FORMAT")
        }

    /**
     * 生成一个新的文件名，并保证新的文件不存在
     */
    private fun createFileNameForUnique(timestamp: Long, index: Int): String {
        var finalIndex = index
        if (finalIndex < 1) {
            finalIndex = 1
        }
        var fileName = formatFileName(timestamp, finalIndex)
        while (FileUtils.isFileExists(logDir + File.separator + fileName)) {
            fileName = formatFileName(timestamp, ++finalIndex)
        }
        return fileName
    }

    private fun getDataFormat(timestamp: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        sdf.timeZone = TimeZone.getDefault()
        return sdf.format(Date(timestamp))
    }

    private fun formatFileName(timestamp: Long, index: Int): String {
        return FILE_NAME_PREFIX + "_" + getDataFormat(timestamp) + "_" + index + "." + FORMAT
    }

    private fun getIndexFromFileName(fileName: String?): Int {
        val i = fileName!!.lastIndexOf("_")
        if (i < 0) {
            return -1
        }
        val i1 = fileName.lastIndexOf(".")
        if (i1 < 0) {
            return -1
        }
        return if (i >= i1) {
            -1
        } else try {
            val index = fileName.substring(i + 1, i1)
            index.toInt()
        } catch (e: NumberFormatException) {
            -1
        }
    }

    companion object {
        private const val FILE_NAME_PREFIX = "rtsppusher"
        private const val FORMAT = "log"
    }
}