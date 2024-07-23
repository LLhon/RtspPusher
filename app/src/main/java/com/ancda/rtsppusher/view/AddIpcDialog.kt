package com.ancda.rtsppusher.view

import android.app.Activity
import android.view.Gravity
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.ancda.rtsppusher.MyApp
import com.ancda.rtsppusher.R
import com.ancda.rtsppusher.appViewModel
import com.ancda.rtsppusher.data.bean.Device
import com.ancda.rtsppusher.databinding.DialogAddIpcBinding
import com.ancda.rtsppusher.onvif.FindDevicesThread
import com.ancda.rtsppusher.ui.adapter.SearchIpcAdapter
import com.blankj.utilcode.util.ToastUtils
import razerdp.basepopup.BasePopupWindow

/**
 * author  : LLhon
 * date    : 2024/4/23 16:46.
 * des     :
 */
class AddIpcDialog(private val activity: Activity, private val listener: OnAddIpcListener): BasePopupWindow(activity) {

    private lateinit var mBinding: DialogAddIpcBinding
    private var mAdapter: SearchIpcAdapter? = null

    init {
        setContentView(R.layout.dialog_add_ipc)
        setOutSideDismiss(false)
    }

    override fun onViewCreated(contentView: View) {
        mBinding = DialogAddIpcBinding.bind(contentView).apply {
            rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            mAdapter = SearchIpcAdapter()
            mAdapter?.apply {
                setOnItemClickListener { _, _, position ->
                    setSelPosition(position)
                    updateInfo(data[position])
                }
            }
            rv.adapter = mAdapter

            val protocolList = activity.resources.getStringArray(R.array.ProtocolList)
            var selProtocolPosition = 0
            flProtocol.setOnClickListener {
                //选择协议
                ivProtocolArrow.setImageResource(R.drawable.ic_up_arrow)
                OptionListDialog(activity, protocolList.toMutableList(), selProtocolPosition, object: OptionListDialog.OnOptionClickListener {
                    override fun onOptionClick(position: Int) {
                        tvProtocol.text = protocolList[position]
                        ivProtocolArrow.setImageResource(R.drawable.ic_down_arrow)
                    }
                }).setPopupGravity(Gravity.BOTTOM).showPopupWindow(flProtocol)
            }
            val transportList = activity.resources.getStringArray(R.array.TransportList)
            var selTransportPosition = 0
            flTransport.setOnClickListener {
                //选择传输协议
                ivTransportArrow.setImageResource(R.drawable.ic_up_arrow)
                OptionListDialog(activity, transportList.toMutableList(), selTransportPosition, object: OptionListDialog.OnOptionClickListener {
                    override fun onOptionClick(position: Int) {
                        tvTransport.text = transportList[position]
                        ivTransportArrow.setImageResource(R.drawable.ic_down_arrow)
                    }
                }).setPopupGravity(Gravity.BOTTOM).showPopupWindow(flTransport)
            }

            ivCancel.setOnClickListener { dismiss() }
            tvCancel.setOnClickListener { dismiss() }
            //添加
            tvAdd.setOnClickListener {

                if (!addIPC()) {
                    return@setOnClickListener
                }

                dismiss()
            }
            //继续添加
            tvContinueAdd.setOnClickListener {
                addIPC()
            }

            tvRefresh.setOnClickListener {
                //刷新
                searchIPC()
            }
        }

        searchIPC()
    }

    private fun addIPC(): Boolean {
        val ip = mBinding.etIp.text.toString()
        val port = mBinding.etPort.text.toString()
        val account = mBinding.etAccount.text.toString()
        val password = mBinding.etPassword.text.toString()
        if (ip.isEmpty()) {
            ToastUtils.showShort("请填写IP通道地址")
            return false
        }
        if (port.isEmpty()) {
            ToastUtils.showShort("请填写端口号")
            return false
        }
        if (account.isEmpty()) {
            ToastUtils.showShort("请填写用户名")
            return false
        }
        if (password.isEmpty()) {
            ToastUtils.showShort("请填写摄像机密码")
            return false
        }
        //判断IP地址是否已在添加列表
        var isIpExist = false
        appViewModel.mIpcListData.value?.forEach {
            if (it.ipAddress == ip) {
                isIpExist = true
                return@forEach
            }
        }
        if (isIpExist) {
            //弹框提示已存在
            WarningDialog(activity).showPopupWindow()
            return false
        }

        val device = Device()
        device.ipAddress = ip
        device.port = port
        device.userName = account
        device.psw = password
        listener.onAddIpc(device)

        return true
    }

    /**
     * 搜索同一网段下的摄像头，可以是广播地址
     */
    private fun searchIPC() {
        //搜索ipc摄像头
        FindDevicesThread(MyApp.sInstance) { searchList ->

            //过滤掉已经添加过的ipc
            appViewModel.mIpcListData.value?.forEach { device ->
                val iterator = searchList.iterator()
                while (iterator.hasNext()) {
                    val next = iterator.next()
                    if (next.ipAddress == device.ipAddress) {
                        iterator.remove()
                    }
                }
            }
            if (searchList.isEmpty()) {
                return@FindDevicesThread
            }

            activity.runOnUiThread {

                mAdapter?.setList(searchList)
                updateInfo(mAdapter!!.data[0])
            }
        }.start()
    }

    private fun updateInfo(device: Device) {
        mBinding.etIp.setText(device.ipAddress)
        mBinding.etPort.setText(device.port)
        mBinding.tvTransport.text = "自动"
        mBinding.etAccount.setText("admin")
        mBinding.etPassword.setText("")
    }

    interface OnAddIpcListener {
        fun onAddIpc(device: Device)
    }
}