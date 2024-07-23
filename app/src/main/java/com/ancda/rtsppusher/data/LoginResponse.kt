package com.ancda.rtsppusher.data

/**
 * author  : LLhon
 * date    : 2024/6/25 15:18.
 * des     :
 */
data class LoginResponse(
    val result: String,
    val data: List<DataModel>
)

data class DataModel(
    val sessionid: String,
    val isbind: String,
    val schoolid: String
)