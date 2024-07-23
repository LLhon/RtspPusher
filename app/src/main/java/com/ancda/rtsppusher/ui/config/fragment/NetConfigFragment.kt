package com.ancda.rtsppusher.ui.config.fragment

import android.os.Bundle
import com.ancda.base.viewmodel.BaseViewModel
import com.ancda.rtsppusher.MyApp
import com.ancda.rtsppusher.base.BaseFragment
import com.ancda.rtsppusher.databinding.FragmentNetBinding
import com.ancda.rtsppusher.utils.AdwApiHelper
import com.ancda.rtsppusher.utils.Utils

/**
 * author  : LLhon
 * date    : 2024/6/20 20:18.
 * des     :
 */
class NetConfigFragment : BaseFragment<BaseViewModel, FragmentNetBinding>() {

    override fun initView(savedInstanceState: Bundle?) {

        mBind.tvMac.text = Utils.getMacAdd(MyApp.sInstance)
        mBind.etIp.setText(AdwApiHelper.getIpAddr())
        mBind.etGateway.setText(AdwApiHelper.getNetGate())
        val dnsStr = AdwApiHelper.getDns()
        //dns
        mBind.etDns.setText(dnsStr.split(",")[0])
        if (dnsStr.split(",").size > 1) {
            mBind.etDns2.setText(dnsStr.split(",")[1])
        }

        mBind.sbAutoIp.setOnCheckedChangeListener { _, b ->
            if (b) {
                //开启自动获取IP
                mBind.sbAutoDns.isChecked = true
                mBind.etIp.setText("")
                mBind.etIp.isEnabled = false
                mBind.etGateway.setText("")
                mBind.etGateway.isEnabled = false
                mBind.etSubNetMask.setText("")
                mBind.etSubNetMask.isEnabled = false
                mBind.etDns.setText("")
                mBind.etDns.isEnabled = false
                mBind.etDns2.setText("")
                mBind.etDns2.isEnabled = false
            } else {
                //设置静态IP
                mBind.sbAutoDns.isChecked = false
                mBind.etIp.isEnabled = true
                mBind.etGateway.isEnabled = true
                mBind.etSubNetMask.isEnabled = true
                mBind.etDns.isEnabled = true
                mBind.etDns2.isEnabled = true
            }
        }
        mBind.sbAutoDns.setOnCheckedChangeListener { _, b ->
            if (b) {
                //自动获取DNS
                mBind.etDns.setText("")
                mBind.etDns.isEnabled = false
                mBind.etDns2.setText("")
                mBind.etDns2.isEnabled = false
            } else {
                //手动设置DNS
                mBind.etDns.isEnabled = true
                mBind.etDns2.isEnabled = true
            }
        }

        mBind.btnConfirm.setOnClickListener {
            //应用
            val ip = mBind.etIp.text.toString()
            val gateway = mBind.etGateway.text.toString()
            val subNetMask = mBind.etSubNetMask.text.toString()
            val dns = mBind.etDns.text.toString()
            val dns2 = mBind.etDns2.text.toString()
            AdwApiHelper.setEthernetMode(!mBind.sbAutoIp.isChecked, ip, subNetMask, dns, gateway, dns2)
        }
    }
}