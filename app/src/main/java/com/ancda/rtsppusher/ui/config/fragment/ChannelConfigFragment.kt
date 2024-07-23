package com.ancda.rtsppusher.ui.config.fragment

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.ancda.base.ext.util.setOnClick
import com.ancda.rtsppusher.R
import com.ancda.rtsppusher.appViewModel
import com.ancda.rtsppusher.data.bean.Device
import com.ancda.rtsppusher.databinding.FragmentChannelConfigBinding
import com.ancda.rtsppusher.onvif.OnvifDevice
import com.ancda.rtsppusher.onvif.OnvifListener
import com.ancda.rtsppusher.onvif.OnvifRequest
import com.ancda.rtsppusher.onvif.OnvifResponse
import com.ancda.rtsppusher.onvif.currentDevice
import com.ancda.rtsppusher.base.BaseFragment
import com.ancda.rtsppusher.constant.MmkvConstant
import com.ancda.rtsppusher.constant.mmkvUtils
import com.ancda.rtsppusher.constant.putMmkvObject
import com.ancda.rtsppusher.ext.loopCoroutines
import com.ancda.rtsppusher.ui.config.adapter.ChannelListAdapter
import com.ancda.rtsppusher.ui.config.viewmodel.ChannelConfigViewModel
import com.ancda.rtsppusher.utils.log.ALog
import com.ancda.rtsppusher.view.AddIpcDialog
import com.ancda.rtsppusher.view.DelIpcDialog
import com.ancda.rtsppusher.view.EditIpcDialog
import com.ancda.rtsppusher.view.OptionListDialog
import com.blankj.utilcode.util.ToastUtils
import kotlinx.coroutines.Job

/**
 * author  : LLhon
 * date    : 2024/4/23 11:06.
 * des     : 通道配置页面
 */
class ChannelConfigFragment : BaseFragment<ChannelConfigViewModel, FragmentChannelConfigBinding>(), View.OnClickListener, OnvifListener {

    //检查摄像头在线状态的定时器
    private var mCheckIpcStatueTimeJob: Job? = null
    private var mPerformanceMonitorJob: Job? = null
    private lateinit var mOnvifDevice: OnvifDevice

    companion object {
        const val TAG = "ChannelConfigFragment"
    }

    private lateinit var mAdapter: ChannelListAdapter

    override fun initView(savedInstanceState: Bundle?) {
        Log.d(TAG, "initView()")
        mBind.rv.layoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false)
        mAdapter = ChannelListAdapter()
        mAdapter.run {
            addChildClickViewIds(R.id.ivEdit, R.id.ivDel, R.id.tvPush)

            setOnItemChildClickListener { _, view, position ->
                when (view.id) {
                    R.id.ivEdit -> {
                        //编辑IP通道
                        EditIpcDialog(mActivity, data[position], object: EditIpcDialog.OnEditIpcListener {
                            override fun onEditIpc() {
                                Log.d("ChannelConfigFragment", "onEditIpc: position=$position, device=${data[position]}")
                                val device = data[position]
                                notifyItemChanged(position)
                                //同步到全局列表
                                appViewModel.mIpcListData.value = mAdapter.data
                                //然后连接ipc
                                val onvifDevice = OnvifDevice(device.ipAddress, device.port, device.userName, device.psw)
                                onvifDevice.listener = object: OnvifListener {

                                    override fun requestPerformed(response: OnvifResponse) {
                                        if (!response.success) {
                                            Log.e("EditIpcDialog", "⛔ request failed: ${response.request.type} Response: ${response.error}")
                                            ALog.eToFile("EditIpcDialog", "摄像头[${device.ipAddress}]连接失败!!! request failed: ${response.request.type} Response: ${response.error}")
                                            //更新摄像头在线状态，离线
                                            device.status = 0
                                            device.connectErrorMsg = "网络不可达!"
                                            notifyItemChanged(position)

                                            //同步到全局列表
                                            appViewModel.mIpcListData.value = mAdapter.data
                                            //todo 同步到服务端
                                            putMmkvObject(MmkvConstant.IPC_LIST, appViewModel.mIpcListData.value)
                                        }
                                        // if GetServices have been completed, we request the device information
                                        else if (response.request.type == OnvifRequest.Type.GetServices) {
                                            Log.i("EditIpcDialog", "request device information")

                                        }
                                        // if GetDeviceInformation have been completed, we request the profiles
                                        else if (response.request.type == OnvifRequest.Type.GetDeviceInformation) {
                                            Log.i("EditIpcDialog", "Device information retrieved 👍")
                                            updateDeviceInfo(position, onvifDevice)
                                        }
                                        else if (response.request.type == OnvifRequest.Type.GetProfiles) {
                                            updateDeviceInfo(position, onvifDevice)
                                        }
                                        else if (response.request.type == OnvifRequest.Type.GetStreamURI) {
                                            updateDeviceInfo(position, onvifDevice)
                                        }
                                    }
                                }
                                onvifDevice.getDeviceInfo()
                            }
                        }).showPopupWindow()
                    }

                    R.id.ivDel -> {
                        //删除IP通道
                        DelIpcDialog(mActivity, object : DelIpcDialog.OnDelIpcListener {
                            override fun onDelIpc() {
                                mAdapter.removeAt(position)
                                //同步到全局列表
                                appViewModel.mIpcListData.value = mAdapter.data
                                //todo 同步到服务端
                                putMmkvObject(MmkvConstant.IPC_LIST, appViewModel.mIpcListData.value)
                            }
                        }).showPopupWindow()
                    }
                    R.id.tvPush -> {
                        //todo 开始推流，需要指定是主码流还是子码流
//                        mViewModel.startPush(position, data[position].profiles[0].rtspUrl)
//                        ToastUtils.showShort("推流成功")

                        if (data[position].profiles.isNotEmpty()) {
                            if (data[position].profiles.size > 1) {
                                val optionData = arrayListOf("主码流", "子码流")
                                OptionListDialog(mActivity, optionData, 0, object: OptionListDialog.OnOptionClickListener {
                                    override fun onOptionClick(pos: Int) {
                                        mViewModel.startPush(position, data[position].profiles[pos].rtspUrl)
                                        ToastUtils.showShort("推流成功")
                                    }
                                }).setPopupGravity(Gravity.TOP).showPopupWindow(view)
                            } else {
                                mViewModel.startPush(position, data[position].profiles[0].rtspUrl)
                                ToastUtils.showShort("推流成功")
                            }

                            mmkvUtils().putBoolean(MmkvConstant.IS_RESUME_PUSH_STREAM, true)
                        }
                    }
                }
            }
        }
        mBind.rv.adapter = mAdapter

        setOnClick(this, mBind.tvCustomAdd)
    }

    override fun initData() {
        Log.d(TAG, "initData()")
    }

    override fun lazyLoadData() {
        Log.d(TAG, "lazyLoadData()")
        mAdapter.setList(appViewModel.mIpcListData.value)

        mViewModel.initData()

        mOnvifDevice = OnvifDevice("", "", "", "")

        //定时检查摄像头在线状态
        mCheckIpcStatueTimeJob = loopCoroutines(lifecycle, 30 * 1000, false) {
            checkIpcStatus()
        }

        //性能监控
        mPerformanceMonitorJob = loopCoroutines(lifecycle, 30 * 60 * 1000, false) {
            mViewModel.performanceTest(mActivity)
        }

//        mViewModel.resumePush()
    }

    override fun createObserver() {

    }

    override fun onDestroy() {
        super.onDestroy()
        mCheckIpcStatueTimeJob?.cancel()
        mPerformanceMonitorJob?.cancel()
        mViewModel.release()
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.tvCustomAdd -> {
                //自定义添加
                AddIpcDialog(mActivity, object: AddIpcDialog.OnAddIpcListener {
                    override fun onAddIpc(device: Device) {
                        // TODO: 补充逻辑
                        //先判断是否存在缺少的连续自然数字的通道号
                        //如果存在，需要把添加的插入进去
//                        if (mAdapter.data.size != mAdapter.data[mAdapter.data.size - 1].channelNo) {
//                            val channelNoList = arrayListOf<Int>()
//                            mAdapter.data.forEach {
//                                channelNoList.add(it.channelNo)
//                            }
//                            channelNoList.sort()
//                            val missChannelNoList = arrayListOf<Int>()
//
//                        }

                        //先添加到列表
                        device.channelNo = if (mAdapter.data.isEmpty()) mAdapter.data.size + 1 else mAdapter.data[mAdapter.data.size - 1].channelNo + 1
                        mAdapter.addData(device)
                        //同步到全局列表
                        appViewModel.mIpcListData.value = mAdapter.data
                        //然后连接ipc
                        connectOnvifCamera(device.ipAddress, device.port, device.userName, device.psw)
                    }
                }).showPopupWindow()
            }
        }
    }

    private fun connectOnvifCamera(ipAddress: String, port: String, username: String, password: String) {
        ALog.dToFile(TAG, "connectOnvifCamera: ipAddress=${ipAddress}, port=${port}, username=${username}, password=${password}")
        // Create ONVIF device with user inputs and retrieve camera informations
        currentDevice = OnvifDevice(ipAddress, port, username, password)
        currentDevice.listener = this
        currentDevice.getDeviceInfo()
    }

    override fun requestPerformed(response: OnvifResponse) {
        Log.d("requestPerformed", response.parsingUIMessage)

        if (!response.success) {
            Log.e("requestPerformed", "⛔ request failed: ${response.request.type} Response: ${response.error}")
            ALog.eToFile("requestPerformed", "摄像头[${currentDevice.ipAddress}]连接失败!!! request failed: ${response.request.type} Response: ${response.error}")
            mAdapter.data[mAdapter.data.size - 1].status = 0
            mAdapter.data[mAdapter.data.size - 1].connectErrorMsg = "网络不可达!"
            mAdapter.notifyItemChanged(mAdapter.data.size - 1)

            //同步到全局列表
            appViewModel.mIpcListData.value = mAdapter.data
            //todo 同步到服务端
            putMmkvObject(MmkvConstant.IPC_LIST, appViewModel.mIpcListData.value)
        }
        // if GetServices have been completed, we request the device information
        else if (response.request.type == OnvifRequest.Type.GetServices) {

        }
        // if GetDeviceInformation have been completed, we request the profiles
        else if (response.request.type == OnvifRequest.Type.GetDeviceInformation) {

            Log.i(TAG, "Device information retrieved 👍")
            //2024-03-25 14:24:01.889 MainActivity             D  GetDeviceInformation:Device information:
            //                                                    Manufacturer: Dahua
            //                                                    Model: DH-P40A2-WT
            //                                                    FirmwareVersion: 2.800.0000000.34.R, Build Date 2021-07-08
            //                                                    SerialNumber: 7E0A6B2PAG5E39B
            //2024-03-25 14:24:01.922 BODY                     D  <?xml version="1.0" encoding="utf-8"?><soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://www.w3.org/2003/05/soap-envelope" ><soap:Body><GetProfiles xmlns="http://www.onvif.org/ver10/media/wsdl"/></soap:Body></soap:Envelope>
            //2024-03-25 14:24:01.923 RESPONSE                 D  Response{protocol=http/1.1, code=401, message=Unauthorized, url=http://192.168.2.215/onvif/media2_service}
            //2024-03-25 14:24:01.923 DIGEST HEADER            D  Digest realm="Login to 0ae5e3a55d6b36d0d35a0f5c751f450a",qop="auth",nonce="xLD0t71uLsrD79BuhFBjZZYkYXr_yr4U29J4Y2QEY1wjOYR/NU96Y5o4OfW61qcO", opaque="", stale="false"
            //2024-03-25 14:24:01.926 AUTHORIZATION HEADER     D  Digest username="admin", realm="Login to 0ae5e3a55d6b36d0d35a0f5c751f450a", nonce="xLD0t71uLsrD79BuhFBjZZYkYXr_yr4U29J4Y2QEY1wjOYR/NU96Y5o4OfW61qcO", uri="/onvif/media2_service", response="98809a032729e4abfbe3e600c5c76501", cnonce="a1b390a149f9085d64598b75f3a9e0f1", nc=00000001, qop="auth"
            //2024-03-25 14:24:01.996 RESULT                   D  true
            //2024-03-25 14:24:01.996 INFO                     D  2 profiles retrieved.
            Log.d(TAG, "GetDeviceInformation:" + response.parsingUIMessage)

            updateDeviceInfo(mAdapter.data.size - 1, currentDevice)
        }
        // if GetProfiles have been completed, we request the Stream URI
        else if (response.request.type == OnvifRequest.Type.GetProfiles) {
            val profilesCount = currentDevice.mediaProfiles.count()
            Log.i(TAG, "$profilesCount profiles retrieved 😎")
            //GetProfiles:2
            //2024-03-25 14:24:02.048 BODY                     D  <?xml version="1.0" encoding="utf-8"?><soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://www.w3.org/2003/05/soap-envelope" ><soap:Body><GetStreamUri xmlns="http://www.onvif.org/ver20/media/wsdl"><ProfileToken>MediaProfile00000</ProfileToken><Protocol>RTSP</Protocol></GetStreamUri></soap:Body></soap:Envelope>
            //2024-03-25 14:24:02.048 RESPONSE                 D  Response{protocol=http/1.1, code=401, message=Unauthorized, url=http://192.168.2.215/onvif/media2_service}
            //2024-03-25 14:24:02.048 DIGEST HEADER            D  Digest realm="Login to 0ae5e3a55d6b36d0d35a0f5c751f450a",qop="auth",nonce="Ig7SI9_7uSN5TQQzgvbmEiH7B/np1kWAoBAZYW/eTxyXv/KqH/NNk9cotOlpaLVp", opaque="", stale="false"
            //2024-03-25 14:24:02.052 AUTHORIZATION HEADER     D  Digest username="admin", realm="Login to 0ae5e3a55d6b36d0d35a0f5c751f450a", nonce="Ig7SI9_7uSN5TQQzgvbmEiH7B/np1kWAoBAZYW/eTxyXv/KqH/NNk9cotOlpaLVp", uri="/onvif/media2_service", response="18c97d577b646b79f78c5a49daa9df1d", cnonce="a1b390a149f9085d64598b75f3a9e0f1", nc=00000001, qop="auth"
            //2024-03-25 14:24:02.085 RESULT                   D  true
            //2024-03-25 14:24:02.086 INFO                     D  RTSP URI retrieved.
            Log.d(TAG, "GetProfiles:$profilesCount")

            updateDeviceInfo(mAdapter.data.size - 1, currentDevice)
        }
        // if GetStreamURI have been completed, we're ready to play the video
        else if (response.request.type == OnvifRequest.Type.GetStreamURI) {

//            mBind.tvInfo.text = mBind.tvInfo.text.toString() + "\n" + "ipc连接成功，可以开始预览了"

            //大华192.168.2.215
            //rtsp://admin:ancda123@192.168.2.215:554/cam/realmonitor?channel=1&subtype=0&unicast=true&proto=Onvif
            //海康威视192.168.6.66
            //ONVIF integrate function is disabled. 摄像头未启用onvif功能，可以访问ip地址进入管理后台开启onvif功能，而且必须设置一个onvif用户，用户类型为管理员
            //rtsp://admin:ancda123@192.168.6.66/Streaming/Channels/101?transportmode=unicast&profile=Profile_1
            //海康威视192.168.6.2
            //rtsp://admin:ancda123@192.168.6.2:554/Streaming/Channels/101?transportmode=unicast&profile=Profile_1
            //Optional Action Not Implemented：摄像头接入的onvif版本问题，GetProfiles需要使用这个地址http://www.onvif.org/ver10/media/wsdl
            //海康威视192.168.6.13
            //rtsp://admin:ancda123@192.168.6.13:554/Streaming/Channels/101?transportmode=unicast&profile=Profile_1
            //Optional Action Not Implemented：摄像头接入的onvif版本问题，GetProfiles需要使用这个地址http://www.onvif.org/ver10/media/wsdl
            //海康威视192.168.6.223
            //rtsp://admin:ancda123@192.168.6.223:554/Streaming/Channels/101?transportmode=unicast&profile=Profile_1
            //Optional Action Not Implemented：摄像头接入的onvif版本问题，GetProfiles需要使用这个地址http://www.onvif.org/ver10/media/wsdl

            //
            //IPCAM 192.168.6.172
            //Error 404: Not Found File  检查下摄像头的HTTP端口是否改了，默认是80，如果改了请求地址需要带上端口http://192.168.89:8080/onvif/device_service
            //request failed: GetDeviceInformation Response: 401 - Unauthorized  检查下登录的账号密码是否正确，以及是否关闭了onvif的权限校验
            //request failed: GetStreamURI Response: 400 - Bad Request Method 'GetStreamUri' not implemented: method name or namespace not recognized 可能是新老版本的原因，请求的body数据格式可能不兼容，从这里排查
            //request failed: GetStreamURI Response: 400 - End of file or no input: Operation interrupted or timed out 可能是新老版本的原因，请求的body数据格式可能不兼容，从这里排查
            //IPCAM 192.168.6.89
            //rtsp://admin:admin@192.168.6.89:554/11

            //ONVIF_IPNC 192.168.6.14
            //rtsp://admin:Admin123@192.168.6.14/live/0/MAIN

            Log.i(TAG, "Stream URI retrieved, ready for the movie 🍿")

            updateDeviceInfo(mAdapter.data.size - 1, currentDevice)
        }
    }

    private fun updateDeviceInfo(position: Int, onvifDevice: OnvifDevice) {
        mAdapter.data[position].run {
            manufacturer = onvifDevice.deviceInformation.manufacturerName
            firmwareVersion = onvifDevice.deviceInformation.fwVersion
            modle = onvifDevice.deviceInformation.modelName
            serialNumber = onvifDevice.deviceInformation.serialNumber
            addProfiles(onvifDevice.mediaProfiles)
            status = 1
        }
        mAdapter.notifyItemChanged(position)

        //同步到全局列表
        appViewModel.mIpcListData.value = mAdapter.data
        //todo 同步到服务端
        putMmkvObject(MmkvConstant.IPC_LIST, appViewModel.mIpcListData.value)
    }

    /**
     * 检查摄像头在线状态
     */
    private fun checkIpcStatus() {
        if (mAdapter.data.size == 0) {
            return
        }
        Log.d(TAG, "========检查摄像头在线状态========")
        var position = 0
        var device = mAdapter.data[position]
        ALog.iToFile(TAG, "连接摄像头:${device.ipAddress}")
        val onvifDevice = OnvifDevice(device.ipAddress, device.port, device.userName, device.psw)
//        mOnvifDevice.ipAddress = device.ipAddress
//        mOnvifDevice.port = device.port
//        mOnvifDevice.username = device.userName
//        mOnvifDevice.password = device.psw
//        mOnvifDevice.reset()
        onvifDevice.listener = object: OnvifListener {

            override fun requestPerformed(response: OnvifResponse) {
                if (!response.success) {
                    Log.e(TAG, "⛔ request failed: ${response.request.type} Response: ${response.error}")
                    ALog.eToFile(TAG, "摄像头[${onvifDevice.ipAddress}]连接失败!!! request failed: ${response.request.type} Response: ${response.error}")
                    //更新摄像头在线状态，离线
                    device.status = 0
                    device.connectErrorMsg = "网络不可达!"
                    mAdapter.notifyItemChanged(position)
                    //同步到全局列表
                    appViewModel.mIpcListData.value = mAdapter.data
                    //todo 同步到服务端
                    putMmkvObject(MmkvConstant.IPC_LIST, appViewModel.mIpcListData.value)

                    //继续连接下一个摄像头
                    position++
                    if (position < mAdapter.data.size) {
                        device = mAdapter.data[position]

                        ALog.iToFile(TAG, "连接摄像头:${device.ipAddress}")
                        onvifDevice.ipAddress = device.ipAddress
                        onvifDevice.port = device.port
                        onvifDevice.username = device.userName
                        onvifDevice.password = device.psw
                        onvifDevice.reset()

                        onvifDevice.getDeviceInfo()
                    }
                }
                // if GetServices have been completed, we request the device information
                else if (response.request.type == OnvifRequest.Type.GetServices) {
                    Log.i(TAG, "request device information")

                }
                // if GetDeviceInformation have been completed, we request the profiles
                else if (response.request.type == OnvifRequest.Type.GetDeviceInformation) {
                    Log.i(TAG, "Device information retrieved 👍")
                    //更新摄像头在线状态，在线
                    device.status = 1
                    device.connectErrorMsg = ""
                    mAdapter.notifyItemChanged(position)
                    //同步到全局列表
                    appViewModel.mIpcListData.value = mAdapter.data
                    //todo 同步到服务端
                    putMmkvObject(MmkvConstant.IPC_LIST, appViewModel.mIpcListData.value)

                    //继续连接下一个摄像头
                    position++
                    if (position < mAdapter.data.size) {
                        device = mAdapter.data[position]

                        ALog.iToFile(TAG, "连接摄像头:${device.ipAddress}")
                        onvifDevice.ipAddress = device.ipAddress
                        onvifDevice.port = device.port
                        onvifDevice.username = device.userName
                        onvifDevice.password = device.psw
                        onvifDevice.reset()

                        onvifDevice.getDeviceInfo()
                    }
                }
                // if GetProfiles have been completed, we request the Stream URI
                else if (response.request.type == OnvifRequest.Type.GetProfiles) {
                    val profilesCount = onvifDevice.mediaProfiles.count()
                    Log.i(TAG, "$profilesCount profiles retrieved 😎")
                    //GetProfiles:2
                    //2024-03-25 14:24:02.048 BODY                     D  <?xml version="1.0" encoding="utf-8"?><soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://www.w3.org/2003/05/soap-envelope" ><soap:Body><GetStreamUri xmlns="http://www.onvif.org/ver20/media/wsdl"><ProfileToken>MediaProfile00000</ProfileToken><Protocol>RTSP</Protocol></GetStreamUri></soap:Body></soap:Envelope>
                    //2024-03-25 14:24:02.048 RESPONSE                 D  Response{protocol=http/1.1, code=401, message=Unauthorized, url=http://192.168.2.215/onvif/media2_service}
                    //2024-03-25 14:24:02.048 DIGEST HEADER            D  Digest realm="Login to 0ae5e3a55d6b36d0d35a0f5c751f450a",qop="auth",nonce="Ig7SI9_7uSN5TQQzgvbmEiH7B/np1kWAoBAZYW/eTxyXv/KqH/NNk9cotOlpaLVp", opaque="", stale="false"
                    //2024-03-25 14:24:02.052 AUTHORIZATION HEADER     D  Digest username="admin", realm="Login to 0ae5e3a55d6b36d0d35a0f5c751f450a", nonce="Ig7SI9_7uSN5TQQzgvbmEiH7B/np1kWAoBAZYW/eTxyXv/KqH/NNk9cotOlpaLVp", uri="/onvif/media2_service", response="18c97d577b646b79f78c5a49daa9df1d", cnonce="a1b390a149f9085d64598b75f3a9e0f1", nc=00000001, qop="auth"
                    //2024-03-25 14:24:02.085 RESULT                   D  true
                    //2024-03-25 14:24:02.086 INFO                     D  RTSP URI retrieved.
                    Log.d(TAG, "GetProfiles:$profilesCount")

                    //更新摄像头在线状态，在线
                    device.status = 1
                    device.connectErrorMsg = ""
                    mAdapter.notifyItemChanged(position)
                    //同步到全局列表
                    appViewModel.mIpcListData.value = mAdapter.data
                    //todo 同步到服务端
                    putMmkvObject(MmkvConstant.IPC_LIST, appViewModel.mIpcListData.value)

                    //继续连接下一个摄像头
                    position++
                    if (position < mAdapter.data.size) {
                        device = mAdapter.data[position]

                        ALog.iToFile(TAG, "连接摄像头:${device.ipAddress}")
                        onvifDevice.ipAddress = device.ipAddress
                        onvifDevice.port = device.port
                        onvifDevice.username = device.userName
                        onvifDevice.password = device.psw
                        onvifDevice.reset()

                        onvifDevice.getDeviceInfo()
                    }
                }
                // if GetStreamURI have been completed, we're ready to play the video
                else if (response.request.type == OnvifRequest.Type.GetStreamURI) {
                    Log.i(TAG, "Stream URI retrieved, ready for the movie 🍿")

                    Log.e("LLhon", "position=$position")
                    //更新摄像头在线状态，在线
                    device.status = 1
                    device.connectErrorMsg = ""
                    mAdapter.notifyItemChanged(position)
                    //同步到全局列表
                    appViewModel.mIpcListData.value = mAdapter.data
                    //todo 同步到服务端
                    putMmkvObject(MmkvConstant.IPC_LIST, appViewModel.mIpcListData.value)

                    //继续连接下一个摄像头
                    position++
                    if (position < mAdapter.data.size) {
                        device = mAdapter.data[position]

                        ALog.iToFile(TAG, "连接摄像头:${device.ipAddress}")
                        onvifDevice.ipAddress = device.ipAddress
                        onvifDevice.port = device.port
                        onvifDevice.username = device.userName
                        onvifDevice.password = device.psw
                        onvifDevice.reset()

                        onvifDevice.getDeviceInfo()
                    }
                }
            }
        }
        onvifDevice.getDeviceInfo()
    }
}