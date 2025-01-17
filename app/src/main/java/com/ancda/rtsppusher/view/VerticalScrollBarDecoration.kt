package com.ancda.rtsppusher.view

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.recyclerview.widget.RecyclerView

/**
 * author  : LLhon
 * date    : 2024/4/25 15:50.
 * des     : 自定义滚动条
 */
class VerticalScrollBarDecoration : RecyclerView.ItemDecoration() {

    private val paint = Paint()

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        //总高度
        val scrollBarHeight = 295f
        //指示器高度
        val indicatorHeight = 90f
        //总宽度
        val width = 10f
        //距离右侧距离
        val marginEnd = 14f
        //滚动条拇指的垂直范围
        val extent = parent.computeVerticalScrollExtent()
        //可滚动的区域大小
        val range = parent.computeVerticalScrollRange()
        //当前偏移量（当前滚动的距离）
        val offset = parent.computeVerticalScrollOffset()
        //最大偏移量（最大可滚动的距离）
        val maxOffset = range - extent
        //可以滑动时，在绘制。如果数据不满一屏不能滑动则不会绘制显示
        if (maxOffset > 0) {
            val startX = parent.width - marginEnd - width
            val startY = parent.height / 2f - scrollBarHeight / 2f
            paint.isAntiAlias = true
            paint.strokeCap = Paint.Cap.ROUND
            paint.strokeWidth = width
            paint.color = Color.parseColor("#F0EEFF")
            c.drawLine(startX, startY, startX, startY + scrollBarHeight, paint)
            paint.color = Color.parseColor("#DBCFFF")
            val ratio = offset.toFloat() / maxOffset.toFloat()
            val offsetY = ratio * (scrollBarHeight - indicatorHeight)
            c.drawLine(
                startX,
                startY + offsetY,
                startX,
                startY + indicatorHeight + offsetY,
                paint
            )
        }
    }
}
