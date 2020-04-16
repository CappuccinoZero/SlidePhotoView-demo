package com.example.slidephotoview

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.slidephotoview.base.BaseViewHolder
import com.example.slidephotoview.databinding.ItemViewBinding

class ImageAdapter(private val listener:(View,Int,ArrayList<Uri>)->Unit):RecyclerView.Adapter<BaseViewHolder<ItemViewBinding>>() {
    private val uris = ArrayList<Uri>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<ItemViewBinding> =
        BaseViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_view,
                parent,
                false
            )
        )

    override fun getItemCount() = uris.size

    override fun onBindViewHolder(holder: BaseViewHolder<ItemViewBinding>, position: Int) {
        val mBinding = holder.mBinding
        mBinding.uri = uris[position]
        mBinding.img.setOnClickListener {
            listener.invoke(mBinding.img,position,uris)
        }
        mBinding.executePendingBindings()
    }

    fun insert(uri:Uri){
        val count = itemCount
        uris.add(uri)
        notifyItemInserted(count)
    }

    fun insert(uris:List<Uri>) = uris.forEach {
        insert(it)
    }
}