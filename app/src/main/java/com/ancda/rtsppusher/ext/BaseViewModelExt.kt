package com.ancda.rtsppusher.ext

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ancda.base.activity.BaseVmActivity
import com.ancda.base.ext.util.loge
import com.ancda.base.fragment.BaseVmFragment
import com.ancda.base.network.AppException
import com.ancda.base.network.BaseResponse
import com.ancda.base.viewmodel.BaseViewModel
import com.ancda.rtsppusher.R
import com.ancda.rtsppusher.http.ExceptionHandle
import com.blankj.utilcode.util.ToastUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 描述　:BaseViewModel请求协程封装
 */



/**
 * 过滤服务器结果，失败抛异常
 * @param block 请求体方法，必须要用suspend关键字修饰
 * @param success 成功回调
 * @param error 失败回调 可不传
 * @param isShowDialog 是否显示加载框
 * @param loadingMessage 加载框提示内容
 */
fun <T> BaseViewModel.request(
    block: suspend () -> T,
    success: (T) -> Unit? = {},
    error: (AppException) -> Unit? = {},
    isShowDialog: Boolean = true,
    loadingMessage: String = getString(R.string.dialog_content_loading),
): Job {
    //如果需要弹窗 通知Activity/fragment弹窗
    return viewModelScope.launch {
        runCatching {
            if (isShowDialog) loadingChange.showDialog.postValue(loadingMessage)
            //请求体
            block()
        }.onSuccess {
            //网络请求成功 关闭弹窗
            loadingChange.dismissDialog.postValue(false)
            runCatching {
                //校验请求结果码是否正确，不正确会抛出异常走下面的onFailure
//                executeResponse(it) { t -> success(t) }
                success(it)
            }.onFailure { e ->
                //打印错误消息
                e.message?.loge()
                //打印错误栈信息
                e.printStackTrace()
                //失败回调
                error(ExceptionHandle.handleException(e))
            }
        }.onFailure {
            //网络请求异常 关闭弹窗
            loadingChange.dismissDialog.postValue(false)
            //打印错误消息
            it.message?.loge()
            //打印错误栈信息
            it.printStackTrace()
            //失败回调
            error(ExceptionHandle.handleException(it))
        }
    }
}

/**
 *  不过滤请求结果
 * @param block 请求体 必须要用suspend关键字修饰
 * @param success 成功回调
 * @param error 失败回调 可不给
 * @param isShowDialog 是否显示加载框
 * @param loadingMessage 加载框提示内容
 */
fun <T> BaseViewModel.requestNoCheck(
    block: suspend () -> T,
    isShowDialog: Boolean = true,
    loadingMessage: String = getString(R.string.dialog_content_loading),
    success: (T) -> Unit,
    error: (AppException) -> Unit = {},
): Job {
    //如果需要弹窗 通知Activity/fragment弹窗
    if (isShowDialog) loadingChange.showDialog.postValue(loadingMessage)
    return viewModelScope.launch {
        runCatching {
            //请求体
            block()
        }.onSuccess {
            //网络请求成功 关闭弹窗
            loadingChange.dismissDialog.postValue(false)
            //成功回调
            success(it)
        }.onFailure {
            //网络请求异常 关闭弹窗
            loadingChange.dismissDialog.postValue(false)
            //打印错误消息
            it.message?.loge()
            //打印错误栈信息
            it.printStackTrace()
            //失败回调
            error(ExceptionHandle.handleException(it))
        }
    }
}

/**
 * 请求结果过滤，判断请求服务器请求结果是否成功，不成功则会抛出异常
 */
suspend fun <T> executeResponse(
    response: BaseResponse<T>,
    success: suspend CoroutineScope.(T) -> Unit
) {
    coroutineScope {
        when {
            response.isSuccess() -> {
                success(response.getResponseData())
            }
            else -> {
                throw AppException(
                    response.getResponseCode(),
                    response.getResponseMsg(),
                    response.getResponseMsg()
                )
            }
        }
    }
}



/**
 * 过滤服务器结果，失败抛异常
 * @param block 请求体方法，必须要用suspend关键字修饰
 * @param onSuccess 成功回调 可不传
 * @param onError 失败回调 可不传,不传默认toast提示错误信息
 * @param showDialog 是否显示加载框
 * @param loadingMessage 加载框提示内容
 * @param showToast toast提示(默认提示)
 */
fun <T> BaseViewModel.http(
    block: suspend () -> T,
    showDialog: Boolean = false,
    loadingMessage: String = getString(R.string.dialog_content_loading),
    showToast: Boolean = true,
    onError: ((AppException) -> Any?)? = null,
    onSuccess: ((T) -> Unit)? = null,
): Job {
    //如果需要弹窗 通知Activity/fragment弹窗
    return viewModelScope.launch {
        runCatching {
            if (showDialog) loadingChange.showDialog.postValue(loadingMessage)
            //请求体
            block()
        }.onSuccess {
            //网络请求成功 关闭弹窗
            if (showDialog) loadingChange.dismissDialog.postValue(false)
            onSuccess?.invoke(it)
        }.onFailure {
            //网络请求异常 关闭弹窗
            loadingChange.dismissDialog.postValue(false)
            //打印错误消息
            it.message?.loge()
            //打印错误栈信息
            it.printStackTrace()
            val exception = ExceptionHandle.handleException(it)
            if (showToast) ToastUtils.showLong(exception.errorMsg)
            onError?.invoke(exception)
        }
    }
}

/**
 * 过滤服务器结果，失败抛异常
 * @param block 请求体方法，必须要用suspend关键字修饰
 * @param liveData 绑定数据
 * @param onError 失败回调 可不传,不传默认toast提示错误信息
 * @param showDialog 是否显示加载框
 * @param loadingMessage 加载框提示内容
 * @param showToast toast提示(默认提示)
 */
fun <T> BaseViewModel.http(
    block: suspend () -> T,
    liveData: MutableLiveData<T>,
    showDialog: Boolean = false,
    loadingMessage: String = getString(R.string.dialog_content_loading),
    showToast: Boolean = true,
    onError: ((AppException) -> Any?)? = null,
): Job {
    //如果需要弹窗 通知Activity/fragment弹窗
    return viewModelScope.launch {
        runCatching {
            if (showDialog) loadingChange.showDialog.postValue(loadingMessage)
            //请求体
            block()
        }.onSuccess {
            //网络请求成功 关闭弹窗
            if (showDialog) loadingChange.dismissDialog.postValue(false)
            liveData.postValue(it)
        }.onFailure {
            //网络请求异常 关闭弹窗
            loadingChange.dismissDialog.postValue(false)
            //打印错误消息
            it.message?.loge()
            //打印错误栈信息
            it.printStackTrace()
            val exception = ExceptionHandle.handleException(it)
            if (showToast) ToastUtils.showLong(exception.errorMsg)
            onError?.invoke(exception)
        }
    }
}

/**
 *  调用携程
 * @param block 操作耗时操作任务
 */
fun BaseViewModel.launch(block: () -> Unit) {
    launch(block = block, onSuccess = {})
}


/**
 *  调用携程
 * @param block 操作耗时操作任务
 * @param onSuccess 成功回调
 * @param onError 失败回调 可不给
 * @param onComplete 完成回调 可不给
 * @param onPrepare 准备回调
 */
fun <T> BaseViewModel.launch(
    block: () -> T,
    onSuccess: (T) -> Unit = {},
    onError: (Throwable) -> Unit = {},
    onComplete: () -> Unit = {},
    onPrepare: () -> Unit = {}
) {
    viewModelScope.launch {
        kotlin.runCatching {
            onPrepare.invoke()
            withContext(Dispatchers.IO) {
                block()
            }
        }.onSuccess {
            onSuccess(it)
            onComplete()
        }.onFailure {
            it.printStackTrace()
            onError(it)
            onComplete()
        }
    }
}
