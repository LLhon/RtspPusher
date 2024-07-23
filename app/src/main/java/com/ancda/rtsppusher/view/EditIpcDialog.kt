package com.ancda.rtsppusher.view

import android.app.Activity
import android.view.Gravity
import android.view.View
import com.ancda.rtsppusher.R
import com.ancda.rtsppusher.data.bean.Device
import com.ancda.rtsppusher.databinding.DialogEditIpcBinding
import com.blankj.utilcode.util.ToastUtils
import razerdp.basepopup.BasePopupWindow

/**
 * author  : LLhon
 * date    : 2024/4/25 10:32.
 * des     :
 */
class EditIpcDialog(private val activity: Activity, private val device: Device, private var listener: OnEditIpcListener? = null) : BasePopupWindow(activity) {

    private lateinit var mBinding: DialogEditIpcBinding

    init {
        setContentView(R.layout.dialog_edit_ipc)
        setOutSideDismiss(false)
    }

    override fun onViewCreated(contentView: View) {
        mBinding = DialogEditIpcBinding.bind(contentView).apply {

            tvChannelNo.text = "D${device.channelNo}"
            etIp.setText(device.ipAddress)
            etPort.setText(device.port)
            tvDeviceChannel.text = device.channelNo.toString()
            // TODO: test
            tvTransport.text = "自动"
            etAccount.setText(device.userName)
            etPassword.setText(device.psw)

            val protocolList = activity.resources.getStringArray(R.array.ProtocolList)
            val selProtocolPosition = 0
            flProtocol.setOnClickListener {
                //选择协议
                ivProtocolArrow.setImageResource(R.drawable.ic_up_arrow)
                OptionListDialog(context, protocolList.toMutableList(), selProtocolPosition, object: OptionListDialog.OnOptionClickListener {
                    override fun onOptionClick(position: Int) {
                        tvProtocol.text = protocolList[position]
                        ivProtocolArrow.setImageResource(R.drawable.ic_down_arrow)
                    }
                }).setPopupGravity(Gravity.BOTTOM).showPopupWindow(flProtocol)
            }
            val channelList = activity.resources.getStringArray(R.array.ChannelList)
            var selChannelPosition = device.channelNo - 1
            flDeviceChannel.setOnClickListener {
                //选择通道
                ivChannelArrow.setImageResource(R.drawable.ic_up_arrow)
                OptionListDialog(activity, channelList.toMutableList(), selChannelPosition, object: OptionListDialog.OnOptionClickListener {
                    override fun onOptionClick(position: Int) {
                        selChannelPosition = position
                        tvDeviceChannel.text = channelList[position]
                        ivChannelArrow.setImageResource(R.drawable.ic_down_arrow)
                    }
                }).setPopupGravity(Gravity.BOTTOM).showPopupWindow(flDeviceChannel)

               /* val popup = ListPopupWindow(activity)
                val adapter = ArrayAdapter(activity, android.R.layout.simple_list_item_1, channelList)
                popup.setAdapter(adapter)
                popup.anchorView = flDeviceChannel
                popup.width = LinearLayout.LayoutParams.WRAP_CONTENT
                popup.height = LinearLayout.LayoutParams.WRAP_CONTENT
                popup.isModal = true
                popup.setOnItemClickListener { adapterView, view, position, id ->
                    tvDeviceChannel.text = channelList[position]
                    ivChannelArrow.setImageResource(R.drawable.ic_down_arrow)
                    popup.dismiss()
                }
                popup.show()*/
            }
            val transportList = activity.resources.getStringArray(R.array.TransportList)
            var selTransportPosition = 0
            flTransport.setOnClickListener {
                //选择传输协议
                ivTransportArrow.setImageResource(R.drawable.ic_up_arrow)
                OptionListDialog(activity, transportList.toMutableList(), selTransportPosition, object: OptionListDialog.OnOptionClickListener {
                    override fun onOptionClick(position: Int) {
                        selTransportPosition = position
                        tvTransport.text = transportList[position]
                        ivTransportArrow.setImageResource(R.drawable.ic_down_arrow)
                    }
                }).setPopupGravity(Gravity.BOTTOM).showPopupWindow(flTransport)
            }

            ivCancel.setOnClickListener { dismiss() }
            tvCancel.setOnClickListener { dismiss() }

            tvOk.setOnClickListener {
                //确定编辑
                val ip = etIp.text.toString()
                val port = etPort.text.toString()
                val deviceChannel = tvDeviceChannel.text.toString()
                val transport = tvTransport.text.toString()
                val account = etAccount.text.toString()
                val password = etPassword.text.toString()

                if (ip.isEmpty()) {
                    ToastUtils.showShort("请填写IP通道地址")
                    return@setOnClickListener
                }
                if (port.isEmpty()) {
                    ToastUtils.showShort("请填写端口号")
                    return@setOnClickListener
                }
                if (account.isEmpty()) {
                    ToastUtils.showShort("请填写用户名")
                    return@setOnClickListener
                }
                if (password.isEmpty()) {
                    ToastUtils.showShort("请填写摄像机密码")
                    return@setOnClickListener
                }
                device.ipAddress = ip
                device.port = port
                device.channelNo = deviceChannel.toInt()
                device.userName = account
                device.psw = password

                listener?.onEditIpc()

                dismiss()
            }
        }
    }

    interface OnEditIpcListener {
        fun onEditIpc()
    }
}