package com.ancda.rtsppusher.ext

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.drawable.LayerDrawable
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.ancda.base.appContext
import com.ancda.base.ext.util.toHtml
import com.ancda.rtsppusher.R
import com.ancda.rtsppusher.view.GradualLinePagerIndicator
import com.chad.library.adapter.base.BaseQuickAdapter
import net.lucode.hackware.magicindicator.MagicIndicator
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.UIUtil
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView



//绑定普通的Recyclerview
fun RecyclerView.init(
    layoutManger: RecyclerView.LayoutManager,
    bindAdapter: RecyclerView.Adapter<*>,
    isScroll: Boolean = true
): RecyclerView {
    layoutManager = layoutManger
    setHasFixedSize(true)
    adapter = bindAdapter
    isNestedScrollingEnabled = isScroll
    return this
}

//设置适配器的列表动画
fun BaseQuickAdapter<*, *>.setAdapterAnimation(mode: Int) {
    //等于0，关闭列表动画 否则开启
    if (mode == 0) {
        this.animationEnable = false
    } else {
        this.animationEnable = true
        this.setAnimationWithDefault(BaseQuickAdapter.AnimationType.values()[mode - 1])
    }
}

fun MagicIndicator.bindViewPager(
    viewPager: ViewPager,
    mStringList: List<String> = arrayListOf(),
    action: (index: Int) -> Unit = {}) {
    val commonNavigator = CommonNavigator(appContext)
    commonNavigator.adapter = object : CommonNavigatorAdapter() {

        override fun getCount(): Int {
            return  mStringList.size
        }
        override fun getTitleView(context: Context, index: Int): IPagerTitleView {
            return SimplePagerTitleView(appContext).apply {
                //设置文本
                text = mStringList[index].toHtml()
                //字体大小
                textSize = 14f
                //未选中颜色
                normalColor = getColor(R.color.white)
                //选中颜色
                selectedColor = getColor(R.color.white)
                //点击事件
                setOnClickListener {
                    viewPager.currentItem = index
                    action.invoke(index)
                }
            }
        }
        override fun getIndicator(context: Context): IPagerIndicator {
            return LinePagerIndicator(context).apply {
                mode = LinePagerIndicator.MODE_EXACTLY
                //线条的宽高度
                lineHeight = UIUtil.dip2px(appContext, 4.0).toFloat()
                lineWidth = UIUtil.dip2px(appContext, 28.0).toFloat()
                //线条的圆角
                roundRadius = UIUtil.dip2px(appContext, 6.0).toFloat()
                startInterpolator = AccelerateInterpolator()
                endInterpolator = DecelerateInterpolator(2.0f)
                //线条的颜色
                setColors(getColor(R.color.colorPrimary))
            }
        }
    }
    this.navigator = commonNavigator
    ViewPagerHelper.bind(this, viewPager)
}

fun MagicIndicator.bindViewPager2(
    viewPager: ViewPager2,
    mStringList: List<String> = arrayListOf(),
    isAdjustMode: Boolean = false,// 自适应模式，适用于数目固定的、少量的title，平分宽度
    isShowIndicator: Boolean = true,
    onTiTleViewInitListener: ((tv: SimplePagerTitleView, index: Int) -> Unit)? = null,
    onIndicatorInitListener: ((indicator: GradualLinePagerIndicator) -> Unit)? = null,
    onPagerChangeListener: ((tv: SimplePagerTitleView?, index: Int) -> Unit)? = null,
) {
    val commonNavigator = CommonNavigator(appContext)
    commonNavigator.isAdjustMode = isAdjustMode
    commonNavigator.adapter = object : CommonNavigatorAdapter() {

        override fun getCount(): Int {
            return  mStringList.size
        }
        override fun getTitleView(context: Context, index: Int): IPagerTitleView {
            return SimplePagerTitleView(appContext).apply {
                //设置文本
                text = mStringList[index].toHtml()
                //字体大小
                textSize = 21f.sp
                //未选中颜色
                normalColor = getColor(R.color.white)
                //选中颜色
                selectedColor = getColor(R.color.white)

                onTiTleViewInitListener?.invoke(this, index)
                //点击事件
                setOnClickListener {
                    viewPager.currentItem = index
                    onPagerChangeListener?.invoke(this, index)
                }
            }
        }
        override fun getIndicator(context: Context): IPagerIndicator? {
            return if (isShowIndicator) GradualLinePagerIndicator(context).apply {
                mode = LinePagerIndicator.MODE_EXACTLY
                //线条的宽高度
                lineHeight = UIUtil.dip2px(appContext, 6.0).toFloat()
                lineWidth = UIUtil.dip2px(appContext, 60.0).toFloat()
                //线条的圆角
                roundRadius = UIUtil.dip2px(appContext, 6.0).toFloat()
                startInterpolator = AccelerateInterpolator()
                endInterpolator = DecelerateInterpolator(2.0f)
                //线条的颜色
                setGradualChangeColors(getColor(R.color.DBCFFF), getColor(R.color.DBCFFF))
                onIndicatorInitListener?.invoke(this)
            } else null
        }
    }
    this.navigator = commonNavigator

    viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            this@bindViewPager2.onPageSelected(position)
            onPagerChangeListener?.invoke(null, position)
        }

        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            this@bindViewPager2.onPageScrolled(position, positionOffset, positionOffsetPixels)
        }

        override fun onPageScrollStateChanged(state: Int) {
            super.onPageScrollStateChanged(state)
            this@bindViewPager2.onPageScrollStateChanged(state)
        }
    })
}

fun ViewPager.init(
    manager: FragmentManager,
    fragments: ArrayList<out Fragment>
): ViewPager {
    //设置适配器
    offscreenPageLimit = 2
    adapter = object : FragmentPagerAdapter(manager) {
        override fun getCount(): Int = fragments.size
        override fun getItem(position: Int): Fragment = fragments[position]
    }
    return this
}

fun ViewPager2.init(
    activity: FragmentActivity,
    fragments: ArrayList<out Fragment>,
    isUserInputEnabled: Boolean = true
): ViewPager2 {
    //是否可滑动
    this.isUserInputEnabled = isUserInputEnabled
    //设置适配器
    adapter = object : FragmentStateAdapter(activity) {
        override fun createFragment(position: Int) = fragments[position]
        override fun getItemCount() = fragments.size
    }
    return this
}

/**
 * 隐藏软键盘
 */
fun hideSoftKeyboard(activity: Activity?) {
    activity?.let {
        it.currentFocus?.apply {
            val inputMethodManager = it.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }
}