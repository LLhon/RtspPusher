package com.ancda.rtsppusher.utils.log

import android.os.Build
import com.ancda.rtsppusher.BuildConfig
import com.ancda.rtsppusher.ext.getLogFileDir
import com.elvishew.xlog.LogConfiguration
import com.elvishew.xlog.LogLevel
import com.elvishew.xlog.Logger
import com.elvishew.xlog.XLog
import com.elvishew.xlog.flattener.ClassicFlattener
import com.elvishew.xlog.printer.AndroidPrinter
import com.elvishew.xlog.printer.Printer
import com.elvishew.xlog.printer.file.FilePrinter
import com.elvishew.xlog.printer.file.backup.NeverBackupStrategy
import com.elvishew.xlog.printer.file.writer.SimpleWriter
import java.io.File


/**
 * author  : LLhon
 * date    : 2023/8/28 13:57.
 * des     : 日志库
 */
object ALog {

    private val androidPrinter: Printer = AndroidPrinter(true) // 通过 android.util.Log 打印日志的打印器

    private val filePrinter: Printer = FilePrinter.Builder(getLogFileDir()) // 指定保存日志文件的路径
        .fileNameGenerator(MyFileNameGenerator(getLogFileDir(), 50 * 1000 * 1000, 10 * 1000 * 1000)) // 指定日志文件名生成器，默认为 ChangelessFileNameGenerator("log")
        .backupStrategy(NeverBackupStrategy()) // 指定日志文件备份策略，默认为 FileSizeBackupStrategy(1024 * 1024)
//        .cleanStrategy(FileLastModifiedCleanStrategy(MAX_TIME)) // 指定日志文件清除策略，默认为 NeverCleanStrategy()
        .flattener(ClassicFlattener()) // 指定日志平铺器，默认为 DefaultFlattener
        .writer(object:SimpleWriter() {
            override fun onNewFileCreated(file: File?) {
                //写入日志文件头的设备信息
                val sb = StringBuilder()
                try {
                    sb.append("==================设备信息=================")
                    sb.append("\n")
                    sb.append("【DEVICE】" + Build.DEVICE)
                    sb.append("\n")
                    sb.append("【DISPLAY】" + Build.DISPLAY)
                    sb.append("\n")
                    sb.append("【MANUFACTURER】" + Build.MANUFACTURER)
                    sb.append("\n")
                    sb.append("【MODEL】" + Build.MODEL)
                    sb.append("\n")
                    sb.append("【App Version_Name】" + BuildConfig.VERSION_NAME)
                    sb.append("\n")
                    sb.append("【App Version_Code】" + BuildConfig.VERSION_CODE)
                    sb.append("\n")
//                    sb.append("] App Flavor:[" + BuildConfig.FLAVOR)
                    sb.append("【App Build_Type】" + BuildConfig.BUILD_TYPE)
                    sb.append("==================设备信息=================")
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
                appendLog(sb.toString())
            }
        }) // 指定日志写入器，默认为 SimpleWriter
        .build()

    fun init() {
        val config = LogConfiguration.Builder()
            .logLevel(if (BuildConfig.DEBUG) LogLevel.ALL else LogLevel.NONE) // 指定日志级别，低于该级别的日志将不会被打印，默认为 LogLevel.ALL
            .tag("Ancda") // 指定 TAG，默认为 "X-LOG"
//            .enableThreadInfo() // 允许打印线程信息，默认禁止
//            .enableStackTrace(2) // 允许打印深度为 2 的调用栈信息，默认禁止
//            .enableBorder() // 允许打印日志边框，默认禁止
//            .jsonFormatter(MyJsonFormatter()) // 指定 JSON 格式化器，默认为 DefaultJsonFormatter
//            .xmlFormatter(MyXmlFormatter()) // 指定 XML 格式化器，默认为 DefaultXmlFormatter
//            .throwableFormatter(MyThrowableFormatter()) // 指定可抛出异常格式化器，默认为 DefaultThrowableFormatter
//            .threadFormatter(MyThreadFormatter()) // 指定线程信息格式化器，默认为 DefaultThreadFormatter
//            .stackTraceFormatter(MyStackTraceFormatter()) // 指定调用栈信息格式化器，默认为 DefaultStackTraceFormatter
//            .borderFormatter(MyBoardFormatter()) // 指定边框格式化器，默认为 DefaultBorderFormatter
//            .addObjectFormatter(
//                AnyClass::class.java,  // 为指定类型添加对象格式化器
//                AnyClassObjectFormatter()
//            ) // 默认使用 Object.toString()
//            .addInterceptor(
//                BlacklistTagsFilterInterceptor( // 添加黑名单 TAG 过滤器
//                    "blacklist1", "blacklist2", "blacklist3"
//                )
//            )
//            .addInterceptor(MyInterceptor()) // 添加一个日志拦截器
            .build()

//        val consolePrinter: Printer = ConsolePrinter() // 通过 System.out 打印日志到控制台的打印器

        XLog.init(
            // 初始化 XLog
            config,  // 指定日志配置，如果不指定，会默认使用 new LogConfiguration.Builder().build()
            androidPrinter,  // 添加任意多的打印器。如果没有添加任何打印器，会默认使用 AndroidPrinter(Android)/ConsolePrinter(java)
        )
    }

    fun v(message: String) {
        XLog.v(message)
    }

    fun v(tag: String, message: String) {
        XLog.tag(tag).v(message)
    }

    fun vToFile(message: String) {
        getLogger().v(message)
    }

    fun vToFile(tag: String, message: String) {
        getLogger().tag(tag).v(message)
    }

    fun d(message: String) {
        XLog.d(message)
    }

    fun d(tag: String, message: String) {
        XLog.tag(tag).d(message)
    }

    fun dToFile(message: String) {
        getLogger().d(message)
    }

    fun dToFile(tag: String, message: String) {
        getLogger().tag(tag).d(message)
    }

    fun i(message: String) {
        XLog.i(message)
    }

    fun i(tag: String, message: String) {
        XLog.tag(tag).i(message)
    }

    fun iToFile(message: String) {
        getLogger().i(message)
    }

    fun iToFile(tag: String, message: String) {
        getLogger().tag(tag).i(message)
    }

    fun w(message: String) {
        XLog.w(message)
    }

    fun w(tag: String, message: String) {
        XLog.tag(tag).w(message)
    }

    fun wToFile(message: String) {
        getLogger().w(message)
    }

    fun wToFile(tag: String, message: String) {
        getLogger().tag(tag).w(message)
    }

    fun e(message: String) {
        XLog.e(message)
    }

    fun e(tag: String, message: String) {
        XLog.tag(tag).e(message)
    }

    fun eToFile(message: String) {
        getLogger().e(message)
    }

    fun eToFile(tag: String, message: String) {
        getLogger().tag(tag).e(message)
    }

    private fun getLogger() : Logger.Builder {
        return if (BuildConfig.DEBUG) {
            XLog.printers(androidPrinter, filePrinter)
                .logLevel(LogLevel.ALL)
        } else {
            XLog.printers(filePrinter)
                .logLevel(LogLevel.ALL)
        }
    }
}