package com.ancda.base.ext.view

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.view.View
import android.view.ViewTreeObserver
import android.view.ViewTreeObserver.OnGlobalFocusChangeListener
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.ImageView
import android.widget.TextView

/**
 * 设置view显示
 */
fun View?.visible() {
    this?.visibility = View.VISIBLE
}


/**
 * 设置view占位隐藏
 */
fun View?.invisible() {
    this?.visibility = View.INVISIBLE
}

/**
 * 根据条件设置view显示隐藏 为true 显示，为false 隐藏
 */
fun View?.visibleOrGone(flag:Boolean) {
    this?.visibility = if(flag){
        View.VISIBLE
    }else{
        View.GONE
    }
}

/**
 * 根据条件设置view显示隐藏 为true 显示，为false 隐藏
 */
fun View?.visibleOrInvisible(flag:Boolean) {
    this?.visibility = if(flag){
        View.VISIBLE
    }else{
        View.INVISIBLE
    }
}

/**
 * 设置view隐藏
 */
fun View?.gone() {
    this?.visibility = View.GONE
}

fun setVisibles(vararg views: View?) {
    views.forEach {
        it?.visibility = View.VISIBLE
    }
}

fun setVisibleOrGone(flag:Boolean, vararg views: View?) {
    views.forEach {
        it?.visibility = if(flag){
            View.VISIBLE
        } else{
            View.GONE
        }
    }
}

fun setVisibleOrInvisible(flag:Boolean, vararg views: View) {
    views.forEach {
        it.visibility = if(flag){
            View.VISIBLE
        }else{
            View.INVISIBLE
        }
    }
}

fun setGone(vararg views: View?) {
    views.forEach {
        it?.visibility = View.GONE
    }
}

fun setInvisible(vararg views: View?) {
    views.forEach {
        it?.visibility = View.INVISIBLE
    }
}

fun TextView.thinFont(isThinFont: Boolean = true) {
    typeface = Typeface.create(if (isThinFont) "sans-serif-light" else "sans-serif", Typeface.NORMAL)
}

fun TextView.setStrokeWidth(strokeWidth: Int) {
    setStrokeWidth(strokeWidth.toFloat())
}

fun TextView.setStrokeWidth(strokeWidth: Float) {
    thinFont()
    paint.style = Paint.Style.FILL_AND_STROKE
    paint.strokeWidth = strokeWidth
    paint.isFakeBoldText = false
}

fun setStrokeWidth(strokeWidth: Float, vararg tvs: TextView) {
    tvs.forEach {
        it.setStrokeWidth(strokeWidth)
    }
}

/**
 * 将view转为bitmap
 */
@Deprecated("use View.drawToBitmap()")
fun View.toBitmap(scale: Float = 1f, config: Bitmap.Config = Bitmap.Config.ARGB_8888): Bitmap? {
    if (this is ImageView) {
        if (drawable is BitmapDrawable) return (drawable as BitmapDrawable).bitmap
    }
    this.clearFocus()
    val bitmap = createBitmapSafely(
        (width * scale).toInt(),
        (height * scale).toInt(),
        config,
        1
    )
    if (bitmap != null) {
        Canvas().run {
            setBitmap(bitmap)
            save()
            drawColor(Color.WHITE)
            scale(scale, scale)
            this@toBitmap.draw(this)
            restore()
            setBitmap(null)
        }
    }
    return bitmap
}

fun createBitmapSafely(width: Int, height: Int, config: Bitmap.Config, retryCount: Int): Bitmap? {
    try {
        return Bitmap.createBitmap(width, height, config)
    } catch (e: OutOfMemoryError) {
        e.printStackTrace()
        if (retryCount > 0) {
            System.gc()
            return createBitmapSafely(width, height, config, retryCount - 1)
        }
        return null
    }
}



var lastClickTime = 0L
/**
 * 防止重复点击事件 默认0.5秒内不可重复点击
 * @param interval 时间间隔 默认0.5秒
 * @param listener 执行方法
 */
fun View.clickNoRepeat(interval: Long = 500, listener: View.OnClickListener) {
    setOnClickListener {
        val currentTime = System.currentTimeMillis()
        if (lastClickTime != 0L && (currentTime - lastClickTime < interval)) {
            return@setOnClickListener
        }
        lastClickTime = currentTime
        listener.onClick(it)
    }
}


fun Any?.notNull(notNullAction:(value:Any) ->Unit,nullAction1:() ->Unit){
    if(this!=null){
        notNullAction.invoke(this)
    }else{
        nullAction1.invoke()
    }
}

// 测量宽高
inline fun View.afterMeasure(crossinline block: (view: View) -> Unit) {
    if (measuredWidth > 0 && measuredHeight > 0) {
        block(this)
    } else {
        viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                this@afterMeasure.viewTreeObserver.removeOnGlobalLayoutListener(this)
                block(this@afterMeasure)
            }
        })
    }
}
