package com.example.slidephotoview.base

import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception
import kotlin.coroutines.CoroutineContext

fun launch(block:suspend ()->Unit,
           error:(suspend (T:Throwable)->Unit)? = null,
           last:(suspend ()->Unit)? = null)
        = GlobalScope.launch {
    try {
        block.invoke()
    }catch (e:Exception){
        error?.invoke(e)
    }finally {
        last?.invoke()
    }
}

fun launch(context: CoroutineContext,
           block:suspend ()->Unit,
           error:(suspend (T:Throwable)->Unit)? = null,
           last:(suspend ()->Unit)? = null)
        = GlobalScope.launch(context) {
    try {
        block.invoke()
    }catch (e:Exception){
        error?.invoke(e)
    }finally {
        last?.invoke()
    }
}

object BaseObjectClass {
    @JvmStatic
    @BindingAdapter("img")
    fun loadImageView(imageView: ImageView, uri:Uri?){
        uri?.apply {
            Glide.with(imageView).load(this).into(imageView)
        }
    }
}