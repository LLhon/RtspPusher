package com.ancda.rtsppusher.ui

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.ancda.rtsppusher.R
import com.ancda.rtsppusher.base.BaseActivity
import com.ancda.rtsppusher.data.bean.Device
import com.ancda.rtsppusher.databinding.ActivityAddIpcBinding
import com.ancda.rtsppusher.onvif.OnvifDevice
import com.ancda.rtsppusher.onvif.OnvifListener
import com.ancda.rtsppusher.onvif.OnvifRequest
import com.ancda.rtsppusher.onvif.OnvifResponse
import com.ancda.rtsppusher.onvif.currentDevice
import com.ancda.rtsppusher.ui.adapter.SearchCameraAdapter
import com.ancda.rtsppusher.ui.viewmodel.AddIPCViewModel


/**
 * author  : LLhon
 * date    : 2024/3/25 9:39.
 * des     :
 */
class AddIPCActivity : BaseActivity<AddIPCViewModel, ActivityAddIpcBinding>(), OnvifListener {

    private val TAG = "AddIPCActivity"
    private var mAdapter: SearchCameraAdapter? = null
    private var mMediaPlayer: MediaPlayer? = null
    private var mCameraIndex = 0
    private var toast: Toast? = null

    override fun initView(savedInstanceState: Bundle?) {

        mBind.rv.layoutManager = LinearLayoutManager(this)
        mAdapter = SearchCameraAdapter()
        mAdapter?.run {
            addChildClickViewIds(R.id.tvAdd)

            setOnItemChildClickListener { adapter, view, position ->
                when (view.id) {
                    R.id.tvAdd -> {
                        showInputDialog(position)
                    }
                }
            }
        }
        mBind.rv.adapter = mAdapter

        mBind.btnSearchIpc.setOnClickListener {
            mViewModel.searchIPC()
        }
    }

    override fun initData() {
        mViewModel.searchIPC()

//        playRtsp()
    }

    override fun createObserver() {
        mViewModel.mSearchDeviceData.observe(this) { data ->
//            if (data.isNullOrEmpty()) {
//                mBind.tvResult.isVisible = true
//                mBind.rv.isGone = true
//                return@observe
//            }
            mBind.rv.isVisible = true
            mBind.tvResult.isGone = true

            val device = Device()
            device.ipAddress = "192.168.6.66"
            data.add(device)
            val device2 = Device()
            device2.ipAddress = "192.168.6.2"
            data.add(device2)
            val device3 = Device()
            device3.ipAddress = "192.168.6.172"
            device3.port = "8080"
            data.add(device3)
            val device4 = Device()
            device4.ipAddress = "192.168.6.223"
            data.add(device4)
            val device5 = Device()
            device5.ipAddress = "192.168.6.15"
            data.add(device5)
            val device6 = Device()
            device6.ipAddress = "192.168.6.14"
            data.add(device6)
            val device7 = Device()
            device7.ipAddress = "192.168.6.89"
            device7.port = "8080"
            data.add(device7)

            mAdapter?.setList(data)
        }
    }

    private fun playRtsp() {
        mMediaPlayer = MediaPlayer.create(this, Uri.parse("rtsp://admin:ancda123@192.168.2.215:554/cam/realmonitor?channel=1&subtype=0&unicast=true&proto=Onvif"))
        mBind.previewView.holder.addCallback(object: SurfaceHolder.Callback {
            override fun surfaceCreated(p0: SurfaceHolder) {
                mMediaPlayer?.setDisplay(p0)
                mMediaPlayer?.start()
            }

            override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {

            }

            override fun surfaceDestroyed(p0: SurfaceHolder) {
                mMediaPlayer?.stop()
            }
        })
    }

    private fun showInputDialog(position: Int) {
        InputDialog(this) { account, password ->
            mCameraIndex = position
            connectOnvifCamera(mAdapter!!.data[position].ipAddress, mAdapter!!.data[position].port, account, password)
        }.showPopupWindow()
    }

    private fun connectOnvifCamera(ipAddress: String, port: String, username: String, password: String) {
        Log.d(TAG, "connectOnvifCamera: ipAddress=${ipAddress}, port=${port}, username=${username}, password=${password}")
//        if (currentDevice.isConnected) {
//
//        } else {
//            // Create ONVIF device with user inputs and retrieve camera informations
//            currentDevice = OnvifDevice(ipAddress, username, password)
//            currentDevice.listener = this
//            currentDevice.getServices()
//        }
        // Create ONVIF device with user inputs and retrieve camera informations
        currentDevice = OnvifDevice(ipAddress, port, username, password)
        currentDevice.listener = this
        currentDevice.getDeviceInfo()
    }

    override fun requestPerformed(response: OnvifResponse) {
        Log.d("INFO", response.parsingUIMessage)

        toast?.cancel()

        if (!response.success) {
            Log.e("ERROR", "request failed: ${response.request.type} \n Response: ${response.error}")
            toast = Toast.makeText(this, "⛔️ Request failed: ${response.request.type}", Toast.LENGTH_SHORT)
            toast?.show()
        }
        // if GetServices have been completed, we request the device information
        else if (response.request.type == OnvifRequest.Type.GetServices) {

        }
        // if GetDeviceInformation have been completed, we request the profiles
        else if (response.request.type == OnvifRequest.Type.GetDeviceInformation) {

            toast = Toast.makeText(this, "Device information retrieved 👍", Toast.LENGTH_SHORT)
            toast?.show()
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

            mAdapter!!.data[mCameraIndex].manufacturer = currentDevice.deviceInformation.manufacturerName
            mAdapter!!.notifyItemChanged(mCameraIndex)
        }
        // if GetProfiles have been completed, we request the Stream URI
        else if (response.request.type == OnvifRequest.Type.GetProfiles) {
            val profilesCount = currentDevice.mediaProfiles.count()
            toast = Toast.makeText(this, "$profilesCount profiles retrieved 😎", Toast.LENGTH_SHORT)
            toast?.show()
            //GetProfiles:2
            //2024-03-25 14:24:02.048 BODY                     D  <?xml version="1.0" encoding="utf-8"?><soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://www.w3.org/2003/05/soap-envelope" ><soap:Body><GetStreamUri xmlns="http://www.onvif.org/ver20/media/wsdl"><ProfileToken>MediaProfile00000</ProfileToken><Protocol>RTSP</Protocol></GetStreamUri></soap:Body></soap:Envelope>
            //2024-03-25 14:24:02.048 RESPONSE                 D  Response{protocol=http/1.1, code=401, message=Unauthorized, url=http://192.168.2.215/onvif/media2_service}
            //2024-03-25 14:24:02.048 DIGEST HEADER            D  Digest realm="Login to 0ae5e3a55d6b36d0d35a0f5c751f450a",qop="auth",nonce="Ig7SI9_7uSN5TQQzgvbmEiH7B/np1kWAoBAZYW/eTxyXv/KqH/NNk9cotOlpaLVp", opaque="", stale="false"
            //2024-03-25 14:24:02.052 AUTHORIZATION HEADER     D  Digest username="admin", realm="Login to 0ae5e3a55d6b36d0d35a0f5c751f450a", nonce="Ig7SI9_7uSN5TQQzgvbmEiH7B/np1kWAoBAZYW/eTxyXv/KqH/NNk9cotOlpaLVp", uri="/onvif/media2_service", response="18c97d577b646b79f78c5a49daa9df1d", cnonce="a1b390a149f9085d64598b75f3a9e0f1", nc=00000001, qop="auth"
            //2024-03-25 14:24:02.085 RESULT                   D  true
            //2024-03-25 14:24:02.086 INFO                     D  RTSP URI retrieved.
            Log.d(TAG, "GetProfiles:$profilesCount")

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

            Log.d(TAG, "ipc摄像头的流地址:" + currentDevice.mediaProfiles.toString())

            toast = Toast.makeText(this, "Stream URI retrieved,\nready for the movie 🍿", Toast.LENGTH_SHORT)
            toast?.show()

            mAdapter!!.data[mCameraIndex].status = 1
            mAdapter!!.notifyItemChanged(mCameraIndex)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mMediaPlayer?.release()
    }
}