package com.ancda.rtsppusher.data.bean

/**
 * author  : LLhon
 * date    : 2024/6/25 13:54.
 * des     :
 */
data class TimeZoneBean(var id: String?, var gmt: String?, var name: String?)

data class TimeZoneInfo(var name: String = "", val id: String = "", var time: String = "", var selected: Boolean = false)
