package com.ancda.rtsppusher.onvif

import android.annotation.SuppressLint
import android.util.Base64.DEFAULT
import android.util.Base64.encodeToString
import org.apache.commons.codec.digest.MessageDigestAlgorithms
import java.io.UnsupportedEncodingException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by remy on 09/03/2018.
 */
object OnvifXMLBuilder {

    private var utcTime = getUTCTime()
    private var nonce = java.util.UUID.randomUUID().toString()

    /**
     * The header for SOAP 1.2 with digest authentication
     */
    val soapHeader: String
        get() {
            return "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                    "<soap:Envelope " +
                    "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
                    "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " +
                    "xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" >"
        }

    val bodyStart: String
        get() {
            return "<soap:Body>"
        }

    fun getSoapDigest(username: String, password: String, nonce: String, createdTime: String) : String =
            "<soap:Header>" +
            "    <Security xmlns=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\" s:mustUnderstand=\"1\">" +
            "       <UsernameToken>" +
            "           <Username>${username}</Username>" +
            "           <Password Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordDigest\">${password}</Password>" +
            "           <Nonce EncodingType=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-soap-message-security-1.0#Base64Binary\">${nonce}</Nonce>" +
            "           <Created xmlns=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\">${createdTime}</Created>" +
            "       </UsernameToken>" +
            "    </Security>" +
            "</soap:Header>"

    val envelopeEnd: String
        get() = "</soap:Body></soap:Envelope>"

    /**
     * Method to encrypt the password for Digest authentication
     */
    private fun encryptPassword(password: String): String? {
        val beforeEncryption = nonce + utcTime + password
        val encryptedRaw: ByteArray
        try {
            val SHA1 = MessageDigest.getInstance(MessageDigestAlgorithms.SHA_1)
            SHA1.reset()
            SHA1.update(beforeEncryption.toByteArray())
            encryptedRaw = SHA1.digest()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            return null
        }

        var encodedString = toBase64(encryptedRaw)
        encodedString = encodedString.replace("\n", "")
        return encodedString
    }


    /**
     * @param message the message to be encoded
     *
     * @return the encoded string from the message
     */
    private fun toBase64(message: String): String? {
        val data: ByteArray
        try {
            data = message.toByteArray(charset("UTF-8"))
            return toBase64(data)
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

        return null
    }

    private fun toBase64(data: ByteArray): String {
        return encodeToString(data, DEFAULT)
    }

    private fun getUTCTime(): String {
        @SuppressLint("SimpleDateFormat") val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:000'Z'")
        sdf.timeZone = SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC")
        val cal = GregorianCalendar(TimeZone.getTimeZone("UTC"))
        return sdf.format(cal.time)
    }
}
