package com.ancda.rtsppusher.ext

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

/**
 * Description: Flow常用的一些方法
 */

/**
 * 倒计时
 */
fun countDownCoroutines(
    total: Int,
    scope: CoroutineScope,
    onTick: ((Int) -> Unit)? = null,
    onStart: (() -> Unit)? = null,
    onFinish: (() -> Unit)? = null,
): Job {
    return flow {
        for (i in total downTo 0) {
            emit(i)
            delay(1000)
        }
    }.flowOn(Dispatchers.Main)
        .onStart { onStart?.invoke() }
        .onCompletion { onFinish?.invoke() }
        .onEach { onTick?.invoke(it) }
        .launchIn(scope)
}

/**
 * 轮询器，根据生命周期执行
 * @param interval 每次轮询的间隔时间 单位-毫秒
 * @param immediately 立即执行
 */
fun loopCoroutines(
    lifecycle: Lifecycle,
    interval: Long,
    immediately: Boolean = true,
    onStart: (() -> Unit)? = null,
    onFinish: (() -> Unit)? = null,
    onTick: (() -> Unit)? = null,
): Job {
    return flow {
        while (true) {
            if (immediately) {
                emit(null)
                delay(interval)
            } else {
                delay(interval)
                emit(null)
            }
        }
    }.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
        .flowOn(Dispatchers.Main)
        .onStart { onStart?.invoke() }
        .onCompletion { onFinish?.invoke() }
        .onEach { onTick?.invoke() }
        .launchIn(lifecycle.coroutineScope)
}