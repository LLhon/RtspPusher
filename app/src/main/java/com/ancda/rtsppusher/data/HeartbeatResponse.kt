package com.ancda.rtsppusher.data

/**
 * author  : LLhon
 * date    : 2024/6/27 10:22.
 * des     :
 */
data class HeartbeatResponse(
    //1：服务器请求上传日志，0：未请求上传日志
    val requestlog: Int = 0,
    //1 重启  0 不重启
    val reboot: Int = 0,

)
