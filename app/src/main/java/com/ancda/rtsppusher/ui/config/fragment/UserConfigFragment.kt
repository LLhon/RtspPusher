package com.ancda.rtsppusher.ui.config.fragment

import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.ancda.base.ext.util.setOnClick
import com.ancda.base.viewmodel.BaseViewModel
import com.ancda.rtsppusher.R
import com.ancda.rtsppusher.appViewModel
import com.ancda.rtsppusher.base.BaseFragment
import com.ancda.rtsppusher.databinding.FragmentUserConfigBinding
import com.ancda.rtsppusher.ui.config.adapter.ChannelListAdapter
import com.ancda.rtsppusher.ui.config.adapter.UserListAdapter

/**
 * author  : LLhon
 * date    : 2024/6/21 17:53.
 * des     :
 */
class UserConfigFragment : BaseFragment<BaseViewModel, FragmentUserConfigBinding>() {

    private lateinit var mAdapter: UserListAdapter

    companion object {
        const val TAG = "UserConfigFragment"
    }

    override fun initView(savedInstanceState: Bundle?) {
        Log.d(TAG, "initView()")
        mBind.rv.layoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false)
        mAdapter = UserListAdapter()
        mAdapter.run {
            addChildClickViewIds(R.id.ivEdit, R.id.ivDel)

            setOnItemChildClickListener { _, view, position ->
                when (view.id) {
                    R.id.ivEdit -> {
                        //编辑用户

                    }
                    R.id.ivDel -> {
                        //删除用户

                    }
                }
            }
        }

        mBind.rv.adapter = mAdapter

        mBind.tvAdd.setOnClickListener {
            //添加用户

        }
    }

    override fun initData() {

    }

    override fun lazyLoadData() {
        mAdapter.setList(appViewModel.mUserListData.value)
    }
}