package com.ancda.rtsppusher.onvif

import android.util.Log
import com.ancda.base.network.NetworkUtil
import com.ancda.rtsppusher.utils.UrlUtils
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException
import java.io.StringReader
import java.net.MalformedURLException
import java.net.URL
import java.util.regex.Matcher
import java.util.regex.Pattern


/**
 * Created by remy on 18/03/2018.
 * This class provide a parsing method for getServices command and the xml command.
 */
class OnvifServices {

    companion object {

        val servicesCommand: String
            get() =
                "<GetServices xmlns=\"http://www.onvif.org/ver10/device/wsdl\">" +
                        "<IncludeCapability>false</IncludeCapability>" +
                        "</GetServices>"

        fun parseServicesResponse(response: String, paths: OnvifCameraPaths): String {

            try {
                val factory = XmlPullParserFactory.newInstance()
                factory.isNamespaceAware = true
                val xpp = factory.newPullParser()
                xpp.setInput(StringReader(response))
                var eventType = xpp.eventType
                while (eventType != XmlPullParser.END_DOCUMENT) {

                    if (eventType == XmlPullParser.START_TAG && xpp.name == "Namespace") {
                        xpp.next()
                        val currentNamespace = xpp.text

                        if (currentNamespace == OnvifRequest.Type.GetDeviceInformation.namespace()) {
                            val uri = retrieveXAddr(xpp)
                            paths.deviceInformation = retrievePath(uri)

                        } else if (currentNamespace == OnvifRequest.Type.GetProfiles.namespace() ||
                                currentNamespace == OnvifRequest.Type.GetStreamURI.namespace()) {
                            val uri = retrieveXAddr(xpp)
                            paths.profiles = retrievePath(uri)
                            paths.streamURI = retrievePath(uri)
                        }
                    }

                    eventType = xpp.next()
                }

            } catch (e: XmlPullParserException) {
                e.printStackTrace()
                return "Error in parsing XML"
            } catch (e: IOException) {
                e.printStackTrace()
                return "IO error"
            }

            return "service URIs retrieved"
        }

        /**
         * Util method to retrieve a path from an URL (without IP address and port)
         * @example:
         * @param uri example input: `http://192.168.1.0:8791/cam/realmonitor?audio=1`
         * @result example output: `cam/realmonitor?audio=1`
         */
        private fun retrievePath(uri: String): String {
            var url: URL
            try {
                // http://192.168.2.215:8080:8080/onvif/device_service
                url = URL(UrlUtils.removeDuplicatePort(uri))
            }
            catch (ex: MalformedURLException) {
                val index = uri.indexOf(':')
                url = URL("http" + uri.drop(index))
            }

            var result = url.path

            if (url.query != null) {
                result += url.query
            }
           return result
        }

        /**
         * Util method for parsing. Retrieve the XAddr from the XmlPullParser given.
         */
        private fun retrieveXAddr(xpp: XmlPullParser): String {

            var result = ""

            var eventType = xpp.eventType
            while (eventType != XmlPullParser.END_DOCUMENT ||
                    (eventType == XmlPullParser.END_TAG && xpp.name == "Service")) {

                if (eventType == XmlPullParser.START_TAG && xpp.name == "XAddr") {
                    xpp.next()
                    result = xpp.text
                    break
                }
                eventType = xpp.next()
            }
            return  result
        }
    }
}
