package com.example.slidephotoview.base

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

class BaseViewHolder<T:ViewDataBinding>(val mBinding: T) : RecyclerView.ViewHolder(mBinding.root)