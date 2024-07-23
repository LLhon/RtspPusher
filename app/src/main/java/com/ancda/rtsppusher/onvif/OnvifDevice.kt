package com.ancda.rtsppusher.onvif


import android.os.AsyncTask
import android.util.Log
import com.ancda.rtsppusher.http.HttpUtil
import com.ancda.rtsppusher.onvif.OnvifDeviceInformation.Companion.deviceInformationCommand
import com.ancda.rtsppusher.onvif.OnvifDeviceInformation.Companion.deviceInformationToString
import com.ancda.rtsppusher.onvif.OnvifDeviceInformation.Companion.parseDeviceInformationResponse
import com.ancda.rtsppusher.onvif.OnvifMediaProfiles.Companion.getProfilesCommand
import com.ancda.rtsppusher.onvif.OnvifMediaStreamURI.Companion.getStreamURICommand
import com.ancda.rtsppusher.onvif.OnvifMediaStreamURI.Companion.parseStreamURIXML
import com.ancda.rtsppusher.onvif.OnvifServices.Companion.servicesCommand
import com.ancda.rtsppusher.onvif.OnvifXMLBuilder.envelopeEnd
import com.ancda.rtsppusher.onvif.OnvifXMLBuilder.soapHeader
import com.ancda.rtsppusher.ui.config.fragment.ChannelConfigFragment
import com.ancda.rtsppusher.utils.UrlUtils
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okio.Buffer
import java.io.IOException
import java.util.concurrent.TimeUnit


@JvmField
var currentDevice = OnvifDevice("", "", "", "")

interface OnvifListener {
    /**
     * Called by OnvifDevice each time a request have been retrieved and parsed.
     * @param response The result of the request also containing the request
     */
    fun requestPerformed(response: OnvifResponse)
}

/**
 * Contains a request for an OnvifDevice
 * @param xmlCommand the xml string to send in the body of the request
 * @param type The type of the request
 */
class OnvifRequest(val xmlCommand: String, val type: Type) {

    enum class Type {
        GetServices,
        GetDeviceInformation,
        GetProfiles,
        GetStreamURI;

        fun namespace(): String {
            when (this) {
                GetServices, GetDeviceInformation -> return "http://www.onvif.org/ver10/device/wsdl"
                GetProfiles, GetStreamURI -> return "http://www.onvif.org/ver10/media/wsdl"
            }
        }
    }
}

/**
 * Paths used to call each differents web services. These paths will be updated
 * by calling getServices.
 */
class OnvifCameraPaths {
    var services = "/onvif/device_service"
    var deviceInformation = "/onvif/device_service"
    var profiles = "/onvif/device_service"
    var streamURI = "/onvif/device_service"
}

/**
 * Contains the response from the Onvif device
 * @param success if the request have been successful
 * @param parsingUIMessage message to be displayed to the user
 * @param result The xml string if the request have been successful
 * @param error The xml string if the request have not been successful
 */
class OnvifResponse(var request: OnvifRequest) {

    var success = false
        private set

    private var message = ""
    var parsingUIMessage = ""

    fun updateResponse(success: Boolean, message: String) {
        this.success = success
        this.message = message
    }

    var result: String? = null
        get() {
            if (success) return message
            else return null
        }

    var error: String? = null
        get() {
            if (!success) return message
            else return null
        }

}

/**
 * @author Remy Virin on 04/03/2018.
 * This class represents an ONVIF device and contains the methods to interact with it
 * (getDeviceInformation, getProfiles and getStreamURI).
 * @param IPAddress The IP address of the camera
 * @param username the username to login on the camera
 * @param password the password to login on the camera
 */
class OnvifDevice(var ipAddress: String, var port: String, var username: String, var password: String) {

    var listener: OnvifListener? = null
    /// We use this variable to know if the connection has been successful (retrieve device information)
    private var isConnected = false

    private var url = "http://$ipAddress"
    val deviceInformation = OnvifDeviceInformation()
    private val paths = OnvifCameraPaths()

    var mediaProfiles: ArrayList<MediaProfile> = arrayListOf()

    fun reset() {
        isConnected = false
        url = "http://$ipAddress"
        mediaProfiles.clear()
    }

    fun getDeviceInfo() {
        ONVIFcommunication().execute()
    }

    /**
     * Communication in Async Task between Android and ONVIF camera
     */
    private inner class ONVIFcommunication : AsyncTask<Void, Void, OnvifResponse>() {

        /**
         * Background process of communication
         *
         * @param params The Onvif Request to execute
         * @return `OnvifResponse`
         */
        override fun doInBackground(vararg params: Void): OnvifResponse {

            var onvifRequest = OnvifRequest(servicesCommand, OnvifRequest.Type.GetServices)
            val result = OnvifResponse(onvifRequest)
            try {

                Log.d("ONVIFcommunication", "GetServices:")
                doPost(onvifRequest, result, true)

                Log.d("ONVIFcommunication", "GetDeviceInformation:")
                onvifRequest = OnvifRequest(deviceInformationCommand, OnvifRequest.Type.GetDeviceInformation)
                doPost(onvifRequest, result, true)

                Log.d("ONVIFcommunication", "GetProfiles:")
                onvifRequest = OnvifRequest(getProfilesCommand(), OnvifRequest.Type.GetProfiles)
                doPost(onvifRequest, result, true)

                Log.d("ONVIFcommunication", "GetStreamURI:")
                mediaProfiles.forEachIndexed { index, mediaProfile ->
                    onvifRequest = OnvifRequest(getStreamURICommand(mediaProfile), OnvifRequest.Type.GetStreamURI)
                    doPost(onvifRequest, result, true, index)
                }

            } catch (e: Exception) {
                result.updateResponse(false, e.message!!)
            }

            return result
        }

        @Throws(Exception::class)
        fun doPost(onvifRequest: OnvifRequest, result: OnvifResponse, needDigest: Boolean, profileIndex: Int = 0) {

            val reqBodyType = MediaType.parse("application/soap+xml; charset=utf-8;")

            val requestContent = if (needDigest) {
                //鉴权
                val digest = Gsoap.getDigest(username, password)
                soapHeader + OnvifXMLBuilder.getSoapDigest(digest.userName, digest.encodePsw, digest.nonce, digest.createdTime) + OnvifXMLBuilder.bodyStart + onvifRequest.xmlCommand + envelopeEnd
            } else {
                soapHeader + OnvifXMLBuilder.bodyStart + onvifRequest.xmlCommand + envelopeEnd
            }
            val reqBody = RequestBody.create(reqBodyType, requestContent)

            /* Request to ONVIF device */
            var request: Request? = null
            try {
                request = Request.Builder()
                    .url(urlForRequest(onvifRequest))
                    .addHeader("Content-Type", "text/xml; charset=utf-8")
                    .post(reqBody)
                    .build()
            } catch (e: IllegalArgumentException) {
                Log.e("ERROR", e.message!!)
                e.printStackTrace()
            }
            //http://192.168.6.94:8080/onvif/device_service
            Log.i("REQUEST URL", urlForRequest(onvifRequest))

            result.request = onvifRequest

            if (request != null) {
                try {
                    /* Response from ONVIF device */
                    var response = HttpUtil.getOkHttpClient().newCall(request).execute()
                    //<?xml version="1.0" encoding="utf-8"?><soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://www.w3.org/2003/05/soap-envelope" ><soap:Body><GetServices xmlns="http://www.onvif.org/ver10/device/wsdl"><IncludeCapability>false</IncludeCapability></GetServices></soap:Body></soap:Envelope>
                    Log.d("REQUEST BODY", bodyToString(request))
                    //Response{protocol=http/1.1, code=401, message=Unauthorized, url=http://192.168.6.94:8080/onvif/device_service}
                    Log.d("HTTP RESPONSE", response.toString())

                    if (response.code() == 401) {
                        Log.d("ONVIFcommunication", "request Authorization")
                        /* Retrieve Digest header */
                        val digestHeader = response.header("WWW-Authenticate")
                        val digestInformation = OnvifDigestInformation(username, password, pathForRequest(onvifRequest), digestHeader ?: "")
                        val authorizationHeader = digestInformation.authorizationHeader
                        if (authorizationHeader != null) {
                            Log.d("AUTHORIZATION HEADER", authorizationHeader)
                        }

                        try {
                            request = Request.Builder().url(urlForRequest(onvifRequest))
                                .addHeader("Content-Type", "text/xml; charset=utf-8")
                                .addHeader("Authorization", authorizationHeader)
                                .post(reqBody)
                                .build()
                        } catch (e: IllegalArgumentException) {
                            Log.e("ERROR", e.message!!)
                            e.printStackTrace()
                        }

                        response = HttpUtil.getOkHttpClient().newCall(request).execute()
                    }

                    if (response.code() != 200) {
                        val responseBody = response.body()!!.string()
                        val message = response.code().toString() + " - " + response.message() + "\n" + responseBody
                        result.updateResponse(false, message)
                    } else {
                        result.updateResponse(true, response.body()!!.string())
                        val uiMessage = parseOnvifResponses(result, profileIndex)
                        result.parsingUIMessage = uiMessage
                    }
                } catch (e: IOException) {
                    result.updateResponse(false, e.message!!)
                    throw Exception(e.message!!)
                }
            }
        }

        /**
         * @return the appropriate URL for calling a web service.
         * Working if the camera is behind a firewall also.
         * @param request the kind of request we're processing.
         */
        fun urlForRequest(request: OnvifRequest): String {
            return url + ":" + port + pathForRequest(request)
        }

        fun pathForRequest(request: OnvifRequest): String {
            when (request.type) {
                OnvifRequest.Type.GetServices -> return paths.services
                OnvifRequest.Type.GetDeviceInformation -> return paths.deviceInformation
                OnvifRequest.Type.GetProfiles -> return paths.profiles
                OnvifRequest.Type.GetStreamURI -> return paths.streamURI
            }
        }


        /**
         * Util function to log the body of a `Request`
         */
        private fun bodyToString(request: Request): String {

            try {
                val copy = request.newBuilder().build()
                val buffer = Buffer()
                copy.body()!!.writeTo(buffer)
                return buffer.readUtf8()
            } catch (e: IOException) {
                return "did not work"
            }
        }

        /**
         * Called when AsyncTask background process is finished
         *
         * @param result the `OnvifResponse`
         */
        override fun onPostExecute(result: OnvifResponse) {
            Log.d("onPostExecute", result.success.toString())

            listener?.requestPerformed(result)
        }
    }

    /**
     * Util method to append the credentials to the rtsp URI
     * Working if the camera is behind a firewall.
     * @param streamURI the URI to modify
     * @return the rtsp URI with the credentials
     */
    private fun appendCredentials(streamURI: String): String {
        val protocol = "rtsp://"
        val uri = streamURI.substring(protocol.length)
        var port: String = ""

        // Retrieve the rtsp port
        val portIndex = uri.indexOf(":")
        if (portIndex > 0) {
            val portEndIndex = uri.indexOf("/")
            port = uri.substring(portIndex, portEndIndex)
        }

        // path and query
        val path = uri.substringAfter('/')

        // We take the URI passed as an input by the user (in case the
        // camera is behind a firewall).
        val ipAddressWithoutPort = ipAddress.substringBefore(":")

        return protocol + username + ":" + password + "@" +
                ipAddressWithoutPort + port + "/" + path
    }

    /**
     * Parsing method for the SOAP XML response
     * @param result `OnvifResponse` to parse
     */
    private fun parseOnvifResponses(result: OnvifResponse, profileIndex: Int = 0): String {
        Log.d("parseOnvifResponses", result.result + "")
        var parsedResult = "Parsing failed"
        if (!result.success) {
            parsedResult = "Communication error trying to get " + result.request + ":\n\n" + result.error

        } else {
            if (result.request.type == OnvifRequest.Type.GetServices) {
                result.result?.let {
                    parsedResult = OnvifServices.parseServicesResponse(it, paths)
                }
            } else if (result.request.type == OnvifRequest.Type.GetDeviceInformation) {
                isConnected = true
                if (parseDeviceInformationResponse(result.result!!, deviceInformation)) {
                    parsedResult = deviceInformationToString(deviceInformation)
                }

            } else if (result.request.type == OnvifRequest.Type.GetProfiles) {
                result.result?.let {
                    val profiles = OnvifMediaProfiles.parseXML(it)
                    mediaProfiles = profiles
                    profiles.forEach { profile ->
                        //profile:name=MainProfile, token=MainProfileToken
                        //profile:name=SubProfile, token=SubProfileToken
                        //profile:name=PROFILE_000, token=PROFILE_000
                        //profile:name=PROFILE_001, token=PROFILE_001
                        Log.d("GetProfiles", "profile:name=${profile.name}, token=${profile.token}")
                    }
                    parsedResult = profiles.count().toString() + " profiles retrieved."
                }

            } else if (result.request.type == OnvifRequest.Type.GetStreamURI) {
                result.result?.let {
                    var streamURI = parseStreamURIXML(it)
                    streamURI = UrlUtils.replaceDuplicatePorts(streamURI)
                    //流地址可能有多个，主码流和子码流
                    Log.d("GetStreamURI", streamURI)
                    mediaProfiles[profileIndex].rtspUrl = appendCredentials(streamURI)
                    parsedResult = "RTSP URI retrieved."
                }
            }
        }

        return parsedResult
    }
}