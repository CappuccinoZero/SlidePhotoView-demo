package com.example.slidephotoview.photoview

import android.net.Uri
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.slidephotoview.R
import com.github.chrisbanes.photoview.PhotoView
import kotlinx.android.synthetic.main.item_photo_view.view.*

class ImagePreviewAdapter(private val uris:ArrayList<Uri>,val listener:((Int)->Unit)? = null):RecyclerView.Adapter<ImagePreviewAdapter.ViewHolder>(){

    private var backgroundView: View? = null

    fun setBackgroundView(view:View){
        if (backgroundView == null || backgroundView != view){
            backgroundView = view
        }
    }

    class ViewHolder(val rootView: View):RecyclerView.ViewHolder(rootView){
        val photoView:PhotoView = rootView.findViewById(R.id.photoView)
        val photoViewLayout:PhotoViewLayout = rootView.findViewById(R.id.photoViewLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_photo_view,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount() = uris.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.photoView.setImageURI(uris[position])
        holder.photoViewLayout.setBackgroundView(backgroundView)
        holder.photoViewLayout.listener = {
            mode ->
            when(mode){
                PhotoViewLayout.SCROLL_MOVE ->{

                }
                PhotoViewLayout.SCROLL_CANCEL ->{

                }
                PhotoViewLayout.SCROLL_EXIT ->{
                    listener?.invoke(mode)
                }
            }
        }
    }
}