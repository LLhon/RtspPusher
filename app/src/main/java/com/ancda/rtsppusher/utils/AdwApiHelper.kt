package com.ancda.rtsppusher.utils

import android.app.ADWApiManager
import android.provider.ContactsContract.CommonDataKinds.StructuredName
import com.ancda.rtsppusher.MyApp
import java.time.Year

/**
 *
 * Created by LLhon on 2024/5/20 17:42.
 */
object AdwApiHelper {

    private val mApi: ADWApiManager by lazy { ADWApiManager(MyApp.sInstance) }

    fun init() {

    }

    /**
     * 设置系统旋转角度
     */
    fun setOrientation(orientation: Int) {
        mApi.SetDeviceOritation(orientation)
    }

    /**
     * 静默安装apk
     */
    fun silentInstall(apkPath: String) {
        mApi.InstallSlientApp(apkPath)
    }

    /**
     * 重启系统
     */
    fun reboot() {
        mApi.Reboot()
    }

    /**
     * 关机系统
     */
    fun shutdown() {
        mApi.ShutDown()
    }

    /**
     * 设置系统时区
     */
    fun setTimeZone(timeZone: String) {
        mApi.SetDeviceTimeZone(timeZone)
    }

    /**
     * 设置系统日期
     */
    fun setDate(year: Int, month: Int, day: Int) {
        mApi.SetDeviceDate(year, month, day)
    }

    /**
     * 设置系统时间
     */
    fun setTime(hour: Int, minute: Int, second: Int) {
        mApi.SetDeviceTime(hour, minute, second)
    }

    /**
     * IP地址
     */
    fun getIpAddr() : String = mApi.GetDeviceIpAddr()

    /**
     * 网关
     */
    fun getNetGate(): String = mApi.GetDeviceNetGate()

    /**
     * DNS
     */
    fun getDns(): String = mApi.GetDeviceDns()

    /**
     * 设置以太网模式
     * @param isStatic: true 静态ip  false 动态获取ip
     */
    fun setEthernetMode(isStatic: Boolean, ip: String, netMask: String, dns: String, netGate: String, dns2: String) {
        mApi.SetDeviceEthernetMode(isStatic, ip, netMask, dns, netGate, dns2)
    }

    /**
     * 定时开关机
     * @param offTime 关机时间，样式：字符串“1900-08-10-08-30-00”代表 1900 年 8 月 10 号 8 点 30 分关机
     * @param onTime 开机时间，样式：字符串“1900-08-10-08-30-00” 代表 1900 年 8 月 10 号 8 点 30 分开机
     * @param enable true ，打开定时开关机；false，关闭定时开关机
     */
    fun setPowerOffOnTime(offTime: String, onTime: String, enable: Boolean) {
        mApi.SetPowerOffOnTime(offTime, onTime, enable)
    }
}