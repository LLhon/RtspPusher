package com.ancda.base.ext.util

import android.content.ClipData
import android.provider.Settings
import android.text.Html
import android.text.Spanned
import android.text.TextUtils
import android.view.View
import com.ancda.base.ext.view.clickNoRepeat
import com.blankj.utilcode.util.SizeUtils
import com.blankj.utilcode.util.Utils

/**
 * 获取屏幕宽度
 */
val screenWidth get() = Utils.getApp().resources.displayMetrics.widthPixels

/**
 * 获取屏幕高度
 */
val screenHeight get() = Utils.getApp().resources.displayMetrics.heightPixels

/**
 * 判断是否为空 并传入相关操作
 */
inline fun <reified T> T?.notNull(notNullAction: (T) -> Unit, nullAction: () -> Unit = {}) {
    if (this != null) {
        notNullAction.invoke(this)
    } else {
        nullAction.invoke()
    }
}

/**
 * 判断是否为空 并传入相关操作
 */
/*fun <T> Any?.notNull(f: () -> T, t: () -> T): T {
    return if (this != null) f() else t()
}*/




/**
 * dp值转换为px
 */
fun dp2px(dp: Int): Int = dp2px(dp.toFloat())

/**
 * dp值转换为px
 */
fun dp2px(dp: Float): Int = SizeUtils.dp2px(dp)

/**
 * px值转换成dp
 */
fun px2dp(px: Int): Int = px2dp(px.toFloat())

/**
 * px值转换成dp
 */
fun px2dp(px: Float): Int = SizeUtils.px2dp(px)

/**
 * sp值转换为px
 */
fun sp2px(sp: Int): Int = sp2px(sp.toFloat())

/**
 * sp值转换为px
 */
fun sp2px(sp: Float): Int = SizeUtils.sp2px(sp)

/**
 * px值转换成sp
 */
fun px2sp(px: Int): Int = px2sp(px.toFloat())

/**
 * px值转换成sp
 */
fun px2sp(px: Float): Int = SizeUtils.px2sp(px)

/**
 * 复制文本到粘贴板
 */
fun copyToClipboard(text: String, label: String = "JetpackMvvm") {
    val clipData = ClipData.newPlainText(label, text)
    Utils.getApp().clipboardManager?.setPrimaryClip(clipData)
}

/**
 * 检查是否启用无障碍服务
 */
fun checkAccessibilityServiceEnabled(serviceName: String): Boolean {
    val settingValue = Settings.Secure.getString(Utils.getApp().contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )
    var result = false
    val splitter = TextUtils.SimpleStringSplitter(':')
    while (splitter.hasNext()) {
        if (splitter.next().equals(serviceName, true)) {
            result = true
            break
        }
    }
    return result
}

fun View.setOnClick(vararg idRes: Int?, listener: View.OnClickListener) {
    idRes.forEach {
        it?.let { findViewById<View>(it).setOnClickListener(listener) }
    }
}

/**
 * 设置点击事件
 * @param views 需要设置点击事件的view
 * @param listener 点击触发的方法
 */
fun setOnClickNoInterval(listener: View.OnClickListener, vararg views: View?) {
    views.forEach {
        it?.setOnClickListener { view ->
            listener.onClick(view)
        }
    }
}

/**
 * 设置防止重复点击事件
 * @param views 需要设置点击事件的view集合
 * @param interval 时间间隔 默认0.5秒
 * @param listener 点击触发的方法
 */
fun setOnClick(listener: View.OnClickListener, vararg views: View?, interval: Long = 500) {
    views.forEach {
        it?.clickNoRepeat(interval = interval) { view ->
            listener.onClick(view)
        }
    }
}
/**
 * 设置防止重复点击事件,间隔0.5秒
 * @param views 需要设置点击事件的view集合
 * @param listener 点击触发的方法
 */
fun setOnClick(vararg views: View?, listener: View.OnClickListener) {
    views.forEach {
        it?.clickNoRepeat(interval = 500) { view ->
            listener.onClick(view)
        }
    }
}

fun <T: View> View.findViewByIds(vararg ids: Int): MutableList<T> {
    val views = mutableListOf<T>()
    ids.forEach {
        views.add(findViewById(it))
    }
    return views
}

fun String.toHtml(flag: Int = Html.FROM_HTML_MODE_LEGACY): Spanned {
    return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
        Html.fromHtml(this, flag)
    } else {
        Html.fromHtml(this)
    }
}


