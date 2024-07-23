package com.ancda.rtsppusher.ui.config.fragment

import android.app.TimePickerDialog
import android.content.Context
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.hardware.display.DisplayManager
import android.icu.util.TimeZone
import android.os.Bundle
import android.util.Log
import android.view.Display
import android.view.Gravity
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import com.ancda.base.ext.util.setOnClick
import com.ancda.base.viewmodel.BaseViewModel
import com.ancda.rtsppusher.MyApp
import com.ancda.rtsppusher.R
import com.ancda.rtsppusher.base.BaseFragment
import com.ancda.rtsppusher.constant.MmkvConstant
import com.ancda.rtsppusher.constant.mmkvUtils
import com.ancda.rtsppusher.data.bean.TimeZoneInfo
import com.ancda.rtsppusher.databinding.FragmentBasicConfigBinding
import com.ancda.rtsppusher.ext.dp
import com.ancda.rtsppusher.utils.AdwApiHelper
import com.ancda.rtsppusher.view.TimeZoneListDialog
import com.github.gzuliyujiang.wheelpicker.DatePicker
import com.github.gzuliyujiang.wheelpicker.DatimePicker
import com.github.gzuliyujiang.wheelpicker.TimePicker
import com.github.gzuliyujiang.wheelpicker.annotation.DateMode
import com.github.gzuliyujiang.wheelpicker.annotation.TimeMode
import com.github.gzuliyujiang.wheelpicker.contract.OnDatimePickedListener
import com.github.gzuliyujiang.wheelpicker.entity.DateEntity
import com.github.gzuliyujiang.wheelpicker.entity.DatimeEntity
import com.github.gzuliyujiang.wheelpicker.entity.TimeEntity
import com.github.gzuliyujiang.wheelpicker.widget.DateWheelLayout
import com.github.gzuliyujiang.wheelpicker.widget.DatimeWheelLayout
import com.github.gzuliyujiang.wheelpicker.widget.TimeWheelLayout
import java.util.Calendar


/**
 * author  : LLhon
 * date    : 2024/6/21 17:52.
 * des     :
 */
class BasicConfigFragment : BaseFragment<BaseViewModel, FragmentBasicConfigBinding>(), OnClickListener, DisplayManager.DisplayListener {

    private val TAG = "BasicConfigFragment"
    private val calendar = Calendar.getInstance()
    private val mDisplayManager by lazy { MyApp.sInstance.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager }
    private val mTimeZoneList by lazy { mutableListOf<TimeZoneInfo>() }
    private var mCurTimeZone = TimeZoneInfo()

    override fun initView(savedInstanceState: Bundle?) {

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        mBind.tvDate.text = "$year-${month+1}-$dayOfMonth"
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        mBind.tvTime.text = "$hourOfDay:$minute"

        mDisplayManager.registerDisplayListener(this, null)

        mBind.sbPwd.isChecked = mmkvUtils().getBoolean(MmkvConstant.ENABLE_OPERATION_PWD, true)
        mBind.sbPwd.setOnCheckedChangeListener { _, b ->
            if (b) {
                //开启操作密码
                mmkvUtils().putBoolean(MmkvConstant.ENABLE_OPERATION_PWD, true)
            } else {
                //关闭操作密码
                mmkvUtils().putBoolean(MmkvConstant.ENABLE_OPERATION_PWD, false)
            }
        }

        setOnClick(this, mBind.tvDate, mBind.tvTime, mBind.btnConfirm, mBind.tvTimeZone)
    }

    override fun initData() {
        mCurTimeZone = getCurTimeZone()
        mBind.tvTimeZone.text = mCurTimeZone.name
        getTimeZoneList()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.tvTimeZone -> {
                //时区
                TimeZoneListDialog(mActivity, mTimeZoneList, object: TimeZoneListDialog.OnOptionClickListener {
                    override fun onOptionClick(position: Int) {
                        mCurTimeZone = mTimeZoneList[position]
                        mBind.tvTimeZone.text = mCurTimeZone.name
                    }
                }).setPopupGravity(Gravity.BOTTOM).showPopupWindow(mBind.tvTimeZone)
            }
            R.id.tvDate -> {
//                DatePickerDialog(mActivity,
//                    { _, year, month, dayOfMonth ->
//                        mBind.tvDate.text = "$year-${month+1}-$dayOfMonth"
//
//                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
//                ).show()


                val picker = DatePicker(mActivity)
                val wheelLayout: DateWheelLayout = picker.wheelLayout
                picker.setOnDatePickedListener { year, month, day ->
                    mBind.tvDate.text = "$year-${month}-$day"
                }
                wheelLayout.setDateMode(DateMode.YEAR_MONTH_DAY)
                wheelLayout.setRange(DateEntity.yearOnFuture(-10), DateEntity.yearOnFuture(10), DateEntity.today())
                wheelLayout.setDateLabel("年", "月", "日")
                picker.setWidth(550.dp)
                picker.setGravity(Gravity.CENTER)
                picker.show()
            }
            R.id.tvTime -> {
//                TimePickerDialog(mActivity, R.style.CustomDatePickerTheme,
//                    { _, hourOfDay, minute ->
//                        mBind.tvTime.text = "$hourOfDay:$minute"
//
//                    }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true
//                ).show()

                val picker = TimePicker(mActivity)
                val wheelLayout: TimeWheelLayout = picker.wheelLayout
                picker.setOnTimePickedListener { hour, minute, second ->
                    mBind.tvTime.text = "$hour:${minute}:$second"
                }
                wheelLayout.setDefaultValue(TimeEntity.now())
                wheelLayout.setTimeMode(TimeMode.HOUR_24_HAS_SECOND)
                wheelLayout.setTimeLabel("时", "分", "秒")
                picker.setWidth(550.dp)
                picker.setGravity(Gravity.CENTER)
                picker.show()
            }
            R.id.btnConfirm -> {
                //应用
                val dateArr = mBind.tvDate.text.toString().split("-")
                AdwApiHelper.setDate(dateArr[0].toInt(), dateArr[1].toInt(), dateArr[2].toInt())
                val timeArr = mBind.tvTime.text.toString().split(":")
                AdwApiHelper.setTime(timeArr[0].toInt(), timeArr[1].toInt(), 0)
                //时区
                AdwApiHelper.setTimeZone(mCurTimeZone.id)
            }
        }
    }

    private fun getTimeZoneList() {
        Log.d(TAG, "getTimeZoneInfo()")
        val default = getCurTimeZone()
        TimeZone.getAvailableIDs().forEach {
            val zone = TimeZone.getTimeZone(it)
            val name: String = zone.getDisplayName(false,TimeZone.LONG)
            val offset = "${zone.rawOffset / (1000 * 60 * 60)}"
            val info = TimeZoneInfo(name, it, offset, default.id == it)
            mTimeZoneList.add(info)
        }
    }

    /**
     * 获取当前时区
     */
    private fun getCurTimeZone(): TimeZoneInfo {
        val zone = TimeZone.getDefault()
        val name = zone.displayName
        val offset = "${zone.rawOffset / (1000 * 60 * 60)}"
        val info = TimeZoneInfo(name, zone.id, offset)
        return info
    }


    override fun onDisplayAdded(displayId: Int) {
        Log.d(TAG, "Display added: $displayId")
//        val display: Display? = mDisplayManager.getDisplay(displayId)
//        if (display != null && display.getType() === Display.TYPE_HDMI) {
//            // HDMI display connected, now set the desired resolution
//            setHdmiResolution(display)
//        }
    }

    private fun setHdmiResolution(display: Display) {
        // Example code to set HDMI resolution (you need to replace with actual implementation)
        // This is a simplified example and might not work for all devices
        val modes = display.supportedModes
//        for (mode in modes) {
//            // Choose a mode based on your criteria, e.g., resolution and refresh rate
//            if (mode.physicalWidth == 1920 && mode.physicalHeight == 1080) {
//                display.requestMode(mode.modeId)
//                Log.d(TAG, "Set HDMI resolution to: " + mode.physicalWidth + "x" + mode.physicalHeight)
//                break
//            }
//        }
    }

    override fun onDisplayChanged(displayId: Int) {
        Log.d(TAG, "Display changed: $displayId")
    }

    override fun onDisplayRemoved(displayId: Int) {
        Log.d(TAG, "Display removed: $displayId")
    }

    override fun onDestroy() {
        super.onDestroy()
        mDisplayManager.unregisterDisplayListener(this)
    }
}