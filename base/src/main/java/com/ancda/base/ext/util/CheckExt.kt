package com.ancda.base.ext.util


/**
 * Author: luoxx
 * Date: 2023/4/4
 * Description: 用于检查的工具
 */

fun Any.checkNotNull(any: Any?, error: String): Any {
    if (any == null) {
        throw NullPointerException(error)
    }
    return any
}