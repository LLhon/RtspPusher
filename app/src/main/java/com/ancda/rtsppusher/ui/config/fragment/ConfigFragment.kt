package com.ancda.rtsppusher.ui.config.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.ancda.base.viewmodel.BaseViewModel
import com.ancda.rtsppusher.R
import com.ancda.rtsppusher.base.BaseFragment
import com.ancda.rtsppusher.databinding.FragmentConfigBinding

/**
 * author  : LLhon
 * date    : 2024/6/20 20:13.
 * des     :
 */
class ConfigFragment : BaseFragment<BaseViewModel, FragmentConfigBinding>(), View.OnClickListener {

    private lateinit var mChannelConfigFragment: ChannelConfigFragment
    private var mSystemConfigFragment: SystemConfigFragment? = null
    private var mNetConfigFragment: NetConfigFragment? = null

    override fun initView(savedInstanceState: Bundle?) {

        mChannelConfigFragment = ChannelConfigFragment()
        childFragmentManager.beginTransaction().add(R.id.flContainer, mChannelConfigFragment).commitAllowingStateLoss()

        mBind.clSystemConfig.setOnClickListener(this)
        mBind.clChannelConfig.setOnClickListener(this)
        mBind.clNetConfig.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.clSystemConfig -> {
                if (mSystemConfigFragment == null) {
                    mSystemConfigFragment = SystemConfigFragment()
                    childFragmentManager.beginTransaction().add(R.id.flContainer, mSystemConfigFragment!!).commitAllowingStateLoss()
                    childFragmentManager.beginTransaction().hide(mChannelConfigFragment).commitAllowingStateLoss()
                    if (mNetConfigFragment != null) {
                        childFragmentManager.beginTransaction().hide(mNetConfigFragment!!).commitAllowingStateLoss()
                    }
                } else {
                    childFragmentManager.beginTransaction().show(mSystemConfigFragment!!).commitAllowingStateLoss()
                    childFragmentManager.beginTransaction().hide(mChannelConfigFragment).commitAllowingStateLoss()
                    if (mNetConfigFragment != null) {
                        childFragmentManager.beginTransaction().hide(mNetConfigFragment!!).commitAllowingStateLoss()
                    }
                }
                mBind.clSystemConfig.setBackgroundResource(R.color.colorPrimaryVariant)
                mBind.clNetConfig.setBackgroundResource(R.color.FF5C6CC3)
                mBind.clChannelConfig.setBackgroundResource(R.color.FF5C6CC3)
            }
            R.id.clNetConfig -> {
                if (mNetConfigFragment == null) {
                    mNetConfigFragment = NetConfigFragment()
                    childFragmentManager.beginTransaction().add(R.id.flContainer, mNetConfigFragment!!).commitAllowingStateLoss()
                    childFragmentManager.beginTransaction().hide(mChannelConfigFragment).commitAllowingStateLoss()
                    if (mSystemConfigFragment != null) {
                        childFragmentManager.beginTransaction().hide(mSystemConfigFragment!!).commitAllowingStateLoss()
                    }
                } else {
                    childFragmentManager.beginTransaction().show(mNetConfigFragment!!).commitAllowingStateLoss()
                    if (mSystemConfigFragment != null) {
                        childFragmentManager.beginTransaction().hide(mSystemConfigFragment!!).commitAllowingStateLoss()
                    }
                    childFragmentManager.beginTransaction().hide(mChannelConfigFragment).commitAllowingStateLoss()
                }
                mBind.clNetConfig.setBackgroundResource(R.color.colorPrimaryVariant)
                mBind.clSystemConfig.setBackgroundResource(R.color.FF5C6CC3)
                mBind.clChannelConfig.setBackgroundResource(R.color.FF5C6CC3)
            }
            R.id.clChannelConfig -> {
                childFragmentManager.beginTransaction().show(mChannelConfigFragment).commitAllowingStateLoss()
                if (mNetConfigFragment != null) {
                    childFragmentManager.beginTransaction().hide(mNetConfigFragment!!).commitAllowingStateLoss()
                }
                if (mSystemConfigFragment != null) {
                    childFragmentManager.beginTransaction().hide(mSystemConfigFragment!!).commitAllowingStateLoss()
                }
                mBind.clChannelConfig.setBackgroundResource(R.color.colorPrimaryVariant)
                mBind.clSystemConfig.setBackgroundResource(R.color.FF5C6CC3)
                mBind.clNetConfig.setBackgroundResource(R.color.FF5C6CC3)
            }
        }
    }
}