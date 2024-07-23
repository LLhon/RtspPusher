package com.ancda.rtsppusher.data.bean;

import com.ancda.rtsppusher.onvif.MediaProfile;
import java.net.NetworkInterface;
import java.util.ArrayList;

/**
 * Author ： BlackHao
 * Time : 2018/1/8 13:53
 * Description : 设备信息
 */

public class Device {

    /**
     * 用户名/密码
     */
    private String userName;
    private String psw;

    //IP地址
    private String ipAddress;
    //端口，默认是80，如果修改过就不是了
    private String port = "80";

    /**
     * serviceUrl,uuid 通过广播包搜索设备获取
     */
    private String serviceUrl;
    private String uuid;
    /**
     * 通过getDeviceInformation 获取
     */
    private String firmwareVersion;
    private String manufacturer;
    private String serialNumber;
    private String modle;
    /**
     * getCapabilities
     */
    private String mediaUrl;
    private String ptzUrl;
    private String imageUrl;
    private String eventUrl;
    private String analyticsUrl;
    /**
     * onvif MediaProfile
     */
    private ArrayList<MediaProfile> profiles;
    private NetworkInterface networkInterface;

    //通道号
    private int channelNo;
    //通道名称
    private String channelName = "";
    //连接状态
    private int status;
    //连接失败错误信息
    private String connectErrorMsg = "";


    public Device() {
        this("admin", "admin");
    }

    public Device(String userName, String psw) {
        profiles = new ArrayList<>();
        this.userName = userName;
        this.psw = psw;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPsw() {
        return psw;
    }

    public void setPsw(String psw) {
        this.psw = psw;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModle() {
        return modle;
    }

    public void setModle(String modle) {
        this.modle = modle;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
        //192.168.6.172
        //192.168.6.172:8080
        ipAddress = serviceUrl.substring(serviceUrl.indexOf("//") + 2, serviceUrl.indexOf("/on"));
        String[] ipAndPort = ipAddress.split(":");
        if (ipAndPort.length > 1) {
            ipAddress = ipAndPort[0];
            port = ipAndPort[1];
        }
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getPtzUrl() {
        return ptzUrl;
    }

    public void setPtzUrl(String ptzUrl) {
        this.ptzUrl = ptzUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getEventUrl() {
        return eventUrl;
    }

    public void setEventUrl(String eventUrl) {
        this.eventUrl = eventUrl;
    }

    public String getAnalyticsUrl() {
        return analyticsUrl;
    }

    public void setAnalyticsUrl(String analyticsUrl) {
        this.analyticsUrl = analyticsUrl;
    }

    public ArrayList<MediaProfile> getProfiles() {
        return profiles;
    }

    public void addProfile(MediaProfile profile) {
        this.profiles.add(profile);
    }

    public void addProfiles(ArrayList<MediaProfile> profiles) {
        this.profiles.clear();
        this.profiles.addAll(profiles);
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public NetworkInterface getNetworkInterface() {
        return networkInterface;
    }

    public void setNetworkInterface(NetworkInterface networkInterface) {
        this.networkInterface = networkInterface;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public int getChannelNo() {
        return channelNo;
    }

    public void setChannelNo(int channelNo) {
        this.channelNo = channelNo;
    }

    public String getConnectErrorMsg() {
        return connectErrorMsg;
    }

    public void setConnectErrorMsg(String connectErrorMsg) {
        this.connectErrorMsg = connectErrorMsg;
    }

    @Override
    public String toString() {
        return "Device{" +
                "userName='" + userName + '\'' +
                ", psw='" + psw + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", port='" + port + '\'' +
                ", status=" + status + '\'' +
                ", connectErrorMsg=" + connectErrorMsg + '\'' +
                ", serviceUrl='" + serviceUrl + '\'' +
                ", uuid='" + uuid + '\'' +
                ", firmwareVersion='" + firmwareVersion + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", serialNumber='" + serialNumber + '\'' +
                ", channelNo='" + channelNo + '\'' +
                ", channelName='" + channelName + '\'' +
                ", mediaUrl='" + mediaUrl + '\'' +
                ", ptzUrl='" + ptzUrl + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", eventUrl='" + eventUrl + '\'' +
                ", analyticsUrl='" + analyticsUrl + '\'' +
                ", profiles=" + profiles.toString() +
                '}';
    }
}

