package com.ancda.rtsppusher.ui.config.fragment

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.View.OnClickListener
import com.ancda.base.ext.util.setOnClick
import com.ancda.base.viewmodel.BaseViewModel
import com.ancda.rtsppusher.MyApp
import com.ancda.rtsppusher.R
import com.ancda.rtsppusher.base.BaseFragment
import com.ancda.rtsppusher.constant.MmkvConstant
import com.ancda.rtsppusher.constant.mmkvUtils
import com.ancda.rtsppusher.databinding.FragmentMoreConfigBinding
import com.ancda.rtsppusher.view.OptionListDialog
import com.blankj.utilcode.util.ToastUtils

/**
 * author  : LLhon
 * date    : 2024/6/21 17:56.
 * des     :
 */
class MoreConfigFragment : BaseFragment<BaseViewModel, FragmentMoreConfigBinding>(), OnClickListener {

    private val sleepDurationList by lazy { resources.getStringArray(R.array.ScreenSleepDuration) }
    private var mScreenSleepIndex = 3

    override fun initView(savedInstanceState: Bundle?) {

        mBind.tvDeviceId.text = MyApp.deviceID
        mScreenSleepIndex = mmkvUtils().getInt(MmkvConstant.SCREEN_SLEEP_INDEX, 3)
        setOnClick(this, mBind.flSleepTime, mBind.btnConfirm)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.flSleepTime -> {
                //屏幕保护时间
                mBind.ivDownArrow.setImageResource(R.drawable.ic_up_arrow)
                OptionListDialog(mActivity, sleepDurationList.toMutableList(), mScreenSleepIndex, object: OptionListDialog.OnOptionClickListener {
                    override fun onOptionClick(position: Int) {
                        mScreenSleepIndex = position
                        mBind.tvSleepTime.text = sleepDurationList[position]
                        mBind.ivDownArrow.setImageResource(R.drawable.ic_down_arrow)
                    }
                }).setPopupGravity(Gravity.BOTTOM).showPopupWindow(mBind.flSleepTime)
            }
            R.id.btnConfirm -> {
                //应用
                mmkvUtils().putInt(MmkvConstant.SCREEN_SLEEP_INDEX, mScreenSleepIndex)
                MyApp.sSleepDuration = when (sleepDurationList[mScreenSleepIndex]) {
                    "30秒" -> 30
                    "1分钟" -> 60
                    "3分钟" -> 180
                    "5分钟" -> 300
                    "10分钟" -> 600
                    else -> 300
                }

                ToastUtils.showShort("应用成功!")
            }
        }
    }
}