package com.ancda.rtsppusher.ext

import android.graphics.drawable.Drawable
import android.text.Html
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.ancda.base.ext.util.dp2px
import com.ancda.base.ext.util.sp2px
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.ResourceUtils
import com.blankj.utilcode.util.Utils


fun getColor(@ColorRes id: Int): Int = ColorUtils.getColor(id)

fun getColorForRes(@ColorRes id: Int): Int = ColorUtils.getColor(id)

fun getDrawable(@DrawableRes id: Int): Drawable = ResourceUtils.getDrawable(id)

fun getDrawableForRes(@DrawableRes id: Int): Drawable = ResourceUtils.getDrawable(id)

fun getString(@StringRes id: Int, vararg args: Any?): String = String.format(Utils.getApp().getString(id), *args)

fun getStringForRes(@StringRes id: Int, vararg args: Any?): String = String.format(Utils.getApp().getString(id), *args)

fun getStringForHtml(@StringRes id: Int, vararg args: Any?): CharSequence = Html.fromHtml(getString(id, *args))

val Float.dp get() = dp2px(this).toFloat()

val Int.dp get() = dp2px(this)

val Float.sp get() = sp2px(this).toFloat()

val Int.sp get() = sp2px(this)
