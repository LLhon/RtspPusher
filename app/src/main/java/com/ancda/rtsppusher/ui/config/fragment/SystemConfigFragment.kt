package com.ancda.rtsppusher.ui.config.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.ancda.base.ext.view.setStrokeWidth
import com.ancda.base.viewmodel.BaseViewModel
import com.ancda.rtsppusher.base.BaseFragment
import com.ancda.rtsppusher.databinding.FragmentSystemBinding
import com.ancda.rtsppusher.ext.bindViewPager2
import com.ancda.rtsppusher.ext.init

/**
 * author  : LLhon
 * date    : 2024/6/20 20:17.
 * des     :
 */
class SystemConfigFragment : BaseFragment<BaseViewModel, FragmentSystemBinding>() {

    private var mFragmentList = arrayListOf<Fragment>()

    override fun initView(savedInstanceState: Bundle?) {

        mFragmentList.add(BasicConfigFragment())
        mFragmentList.add(UserConfigFragment())
        mFragmentList.add(MoreConfigFragment())
        val titles = arrayListOf(
            "基本配置",
            "用户配置",
            "更多配置")
        mBind.indicator.bindViewPager2(mBind.viewpager, titles, false, onTiTleViewInitListener = { _, index ->
//            tv.setStrokeWidth(2F)
        })
        mBind.viewpager.init(mActivity, mFragmentList, false)
    }
}