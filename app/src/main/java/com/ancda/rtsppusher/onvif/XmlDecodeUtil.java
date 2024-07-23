package com.ancda.rtsppusher.onvif;

import android.util.Xml;


import com.ancda.rtsppusher.data.bean.Device;
import org.xmlpull.v1.XmlPullParser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Author ： BlackHao
 * Time : 2018/1/8 15:54
 * Description : xml 解析
 */

public class XmlDecodeUtil {
    /**
     * 获取设备信息
     */
    public static Device getDeviceInfo(String xml) throws Exception {
        Device device = new Device();
        XmlPullParser parser = Xml.newPullParser();
        InputStream input = new ByteArrayInputStream(xml.getBytes());
        parser.setInput(input, "UTF-8");
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    //serviceUrl
                    if (parser.getName().equals("XAddrs")) {
                        String addrs = parser.nextText();
                        String[] strs = addrs.split(" ");
                        String url = strs[0];
                        device.setServiceUrl(url);
                    }
                    if (parser.getName().equals("MessageID")) {
                        device.setUuid(parser.nextText());
                    }
                    break;
                case XmlPullParser.END_TAG:
                    break;
                default:
                    break;
            }
            eventType = parser.next();
        }
        return device;
    }

    /**
     * 解析 xml数据，获取 MediaUrl,PtzUrl
     *
     * @param xml    需要解析的数据
     * @param device 对应的device
     */
    public static void getCapabilitiesUrl(String xml, Device device) throws Exception {
        XmlPullParser parser = Xml.newPullParser();
        InputStream input = new ByteArrayInputStream(xml.getBytes());
        parser.setInput(input, "UTF-8");
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    if (parser.getName().equals("Media")) {
                        parser.nextTag();
                        if (parser.getName() != null && parser.getName().equals("XAddr")) {
                            device.setMediaUrl(parser.nextText());
                        }
                    } else if (parser.getName().equals("PTZ")) {
                        parser.nextTag();
                        if (parser.getName() != null && parser.getName().equals("XAddr")) {
                            device.setPtzUrl(parser.nextText());
                        }
                    } else if (parser.getName().equals("Events")) {
                        parser.nextTag();
                        if (parser.getName() != null && parser.getName().equals("XAddr")) {
                            device.setEventUrl(parser.nextText());
                        }
                    } else if (parser.getName().equals("Analytics")) {
                        parser.nextTag();
                        if (parser.getName() != null && parser.getName().equals("XAddr")) {
                            device.setAnalyticsUrl(parser.nextText());
                        }
                    } else if (parser.getName().equals("Imaging")) {
                        parser.nextTag();
                        if (parser.getName() != null && parser.getName().equals("XAddr")) {
                            device.setImageUrl(parser.nextText());
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    break;
                default:
                    break;
            }
            eventType = parser.next();
        }
    }

    /**
     * 解析 xml数据，获取 MediaUrl,PtzUrl
     *
     * @param xml    需要解析的数据
     * @param device 对应的device
     */
    public static void getDeviceInformation(String xml, Device device) throws Exception {
        XmlPullParser parser = Xml.newPullParser();
        InputStream input = new ByteArrayInputStream(xml.getBytes());
        parser.setInput(input, "UTF-8");
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    if (parser.getName().equals("FirmwareVersion")) {
                        String firmwareVersion = parser.nextText();
                        device.setFirmwareVersion(firmwareVersion);
                    } else if (parser.getName().equals("SerialNumber")) {
                        String srialNumber = parser.nextText();
                        device.setSerialNumber(srialNumber);
                    } else if (parser.getName().equals("Manufacturer")) {
                        String manufacturer = parser.nextText();
                        device.setManufacturer(manufacturer);
                    } else if (parser.getName().equals("Model")) {
                        String model = parser.nextText();
                        device.setModle(model);
                    }
                    break;
                case XmlPullParser.END_TAG:
                    break;
                default:
                    break;
            }
            eventType = parser.next();
        }
    }

    /**
     * 获取 RTSP URL
     */
    public static String getStreamUri(String xml) throws Exception {
        String mediaUrl = "";
        XmlPullParser parser = Xml.newPullParser();
        InputStream input = new ByteArrayInputStream(xml.getBytes());
        parser.setInput(input, "UTF-8");
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    //serviceUrl
                    if (parser.getName().equals("Uri")) {
                        mediaUrl = parser.nextText();
                    }
                    break;
                case XmlPullParser.END_TAG:
                    break;
                default:
                    break;
            }
            eventType = parser.next();
        }
        return mediaUrl;
    }

    /**
     * 获取 截图uri
     */
    public static String getSnapshotUri(String xml) throws Exception {
        String mediaUrl = "";
        XmlPullParser parser = Xml.newPullParser();
        InputStream input = new ByteArrayInputStream(xml.getBytes());
        parser.setInput(input, "UTF-8");
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    //serviceUrl
                    if (parser.getName().equals("Uri")) {
                        mediaUrl = parser.nextText();
                    }
                    break;
                case XmlPullParser.END_TAG:
                    break;
                default:
                    break;
            }
            eventType = parser.next();
        }
        return mediaUrl;
    }

    public static String getUploadUri(String xml) throws Exception {
        String UploadUri = "";
        XmlPullParser parser = Xml.newPullParser();
        InputStream input = new ByteArrayInputStream(xml.getBytes());
        parser.setInput(input, "UTF-8");
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    //serviceUrl
                    if (parser.getName().equals("UploadUri")) {
                        UploadUri = parser.nextText();
                    }
                    break;
                case XmlPullParser.END_TAG:
                    break;
                default:
                    break;
            }
            eventType = parser.next();
        }
        return UploadUri;
    }

}
