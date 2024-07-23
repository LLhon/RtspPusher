package com.ancda.base.network

/**
 * 作者　: luoxx
 * 时间　: 2023/04/28
 * 描述　: 服务器错误返回数据
 */
data class ApiResponseError(
    /**
     * 错误码
     */
    val code: Int = 0,

    /**
     * 错误原因
     */
    val reason: String = "",

    /**
     * 错误信息描述
     */
    val message: String = "",

    /**
     * 错误原因
     */
    val metadata: Any? = null,
)