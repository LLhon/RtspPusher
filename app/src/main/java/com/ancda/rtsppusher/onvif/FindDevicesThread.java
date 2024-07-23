package com.ancda.rtsppusher.onvif;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;


import com.ancda.rtsppusher.data.bean.Device;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * Description : 利用线程搜索局域网内设备
 */

public class FindDevicesThread extends Thread {
    private static String tag = "FindDevicesThread";
    private byte[] sendData;
    private boolean readResult = false;
    //当前设备的网段地址
    private String ipAdress;
    //端口号
    int BROADCAST_PORT = 3702;

    //回调借口
    private FindDevicesListener listener;

    /**
     *
     * @param context
     * @param listener
     */
    public FindDevicesThread(Context context, FindDevicesListener listener) {
        this.listener = listener;
        InputStream fis = null;

        try {
            ipAdress = getBroadcastIp();
        } catch (SocketException e) {
            e.printStackTrace();
            ipAdress = "239.255.255.250";
        }
        Log.d(tag, "broadcastIp=" + ipAdress);

        try {
            //从assets读取文件
            fis = context.getAssets().open("probe.xml");
            sendData = new byte[fis.available()];
            readResult = fis.read(sendData) > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String getBroadcastIp() throws SocketException {
        System.setProperty("java.net.preferIPv4Stack", "true");
        for (Enumeration<NetworkInterface> niEnum = java.net.NetworkInterface
            .getNetworkInterfaces(); niEnum.hasMoreElements(); ) {
            NetworkInterface ni = niEnum.nextElement();
            if (!ni.isLoopback()) {
                for (InterfaceAddress interfaceAddress : ni
                    .getInterfaceAddresses()) {
                    if (interfaceAddress.getBroadcast() != null) {
                        return interfaceAddress.getBroadcast().toString()
                            .substring(1);
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void run() {
        super.run();
        DatagramSocket udpSocket = null;
        DatagramPacket receivePacket;
        DatagramPacket sendPacket;
        //设备列表集合
        ArrayList<Device> devices = new ArrayList<>();
        byte[] by = new byte[1024 * 16];
        if (readResult) {
            try {
                //初始化
                udpSocket = new DatagramSocket(BROADCAST_PORT);
                udpSocket.setSoTimeout(3 * 1000);
                udpSocket.setBroadcast(true);
                //DatagramPacket
                sendPacket = new DatagramPacket(sendData, sendData.length);
                sendPacket.setAddress(InetAddress.getByName(ipAdress));
                sendPacket.setPort(BROADCAST_PORT);
                //发送
                udpSocket.send(sendPacket);
                //接受数据
                receivePacket = new DatagramPacket(by, by.length);
                long startTime = System.currentTimeMillis();
                long endTime = System.currentTimeMillis();
                while (endTime - startTime < 3  * 1000) {
                    udpSocket.receive(receivePacket);
                    String str = new String(receivePacket.getData(), 0, receivePacket.getLength());
                    Log.v(tag, str);
                    Device dev = XmlDecodeUtil.getDeviceInfo(str);
                    if (!TextUtils.isEmpty(dev.getServiceUrl())) {
                        devices.add(dev);
                    }
                    endTime = System.currentTimeMillis();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (udpSocket != null) {
                    udpSocket.close();
                }
            }
        }
        //回调结果
        if (listener != null) {
            listener.searchResult(devices);
        }
    }

    /**
     * Author ： BlackHao
     * Time : 2018/1/9 11:13
     * Description : 搜索设备回调
     */
    public interface FindDevicesListener {
        void searchResult(ArrayList<Device> devices);
    }
}
