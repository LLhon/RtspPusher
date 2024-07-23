package com.ancda.rtsppusher.view

import android.R.attr.text
import android.content.Context
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.ancda.rtsppusher.R
import com.blankj.utilcode.util.ToastUtils


/**
 * author  : LLhon
 * date    : 2023/11/13 16:47.
 * des     : 待机休眠页面
 */
class ScreenSleepView : ConstraintLayout {

    private lateinit var mRootView: View

    constructor(context: Context) : super(context) {
        initialize(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initialize(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initialize(context)
    }

    private fun initialize(context: Context) {
        mRootView = LayoutInflater.from(context).inflate(R.layout.view_screen_sleep, this)
        val etPassword = mRootView.findViewById<EditText>(R.id.etPassword)
        val tvLogin = mRootView.findViewById<TextView>(R.id.tvLogin)
        val tvTitle = mRootView.findViewById<TextView>(R.id.tvTitle)
        val title = "欢迎登录"
        val textShader: Shader =  LinearGradient(0f, 0f, tvTitle.paint.measureText(title), 0f,
            context.getColor(R.color.teal_200), context.getColor(R.color.FF6C80EB), Shader.TileMode.CLAMP)
        // 应用Shader到Paint
        tvTitle.paint.setShader(textShader)
        tvTitle.text = title

        tvLogin.setOnClickListener {
            if (etPassword.text.toString() == "admin") {
                etPassword.setText("")
                setVisible(false)
            } else {
                ToastUtils.showShort("密码错误!")
            }
        }
    }

    fun setVisible(visible: Boolean) {
        mRootView.isVisible = visible
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return true
    }
}