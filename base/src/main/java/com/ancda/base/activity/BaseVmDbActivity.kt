package com.ancda.base.activity

import android.view.View
import androidx.databinding.ViewDataBinding
import com.ancda.base.ext.inflateBindingWithGeneric
import com.ancda.base.viewmodel.BaseViewModel

/**
 * 作者　: luoxx
 * 时间　: 2019/12/12
 * 描述　: 包含ViewModel 和Databind ViewModelActivity基类，把ViewModel 和Databind注入进来了
 * 需要使用Databind的清继承它
 */
abstract class BaseVmDbActivity<VM : BaseViewModel, DB : ViewDataBinding> : BaseVmActivity<VM>() {

    override fun layoutId() = 0

    lateinit var mBind: DB

    /**
     * 创建DataBinding
     */
    override fun initDataBind(): View? {
        mBind = inflateBindingWithGeneric(layoutInflater)
        return mBind.root
    }
}