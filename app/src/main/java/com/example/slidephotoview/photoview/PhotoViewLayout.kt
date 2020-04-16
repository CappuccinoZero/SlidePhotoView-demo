package com.example.slidephotoview.photoview

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import com.github.chrisbanes.photoview.PhotoView
import kotlin.math.abs


class PhotoViewLayout @JvmOverloads constructor(context: Context,attributeSet: AttributeSet? = null,style:Int = 0):FrameLayout(context,attributeSet,style){
    companion object{
        const val SCROLL_MOVE = 0
        const val SCROLL_CANCEL = 1
        const val SCROLL_EXIT = 2
    }
    var downX = 0f
    var downY = 0f

    // 0 未确定 1 拦截 2 不拦截
    private var intercept = 0

    private var photoView:PhotoView? = null

    private var backgroundView: View? = null

    var listener:((Int)->Unit)? = null


    //判断退出的最小距离
    var minRollDistance = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60f, context.resources.displayMetrics)
    //判断缩放的最大的距离
    var maxRollDistance = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300f, context.resources.displayMetrics)

    private fun obtainPhotoView():PhotoView?{
        if (photoView == null){
            for (index in 0 until childCount){
                val child = getChildAt(index)
                if (child is PhotoView){
                    photoView = child
                    break
                }
            }
        }
        return photoView
    }

    fun setBackgroundView(view:View?){
        if (backgroundView == null || backgroundView != view){
            backgroundView = view
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        ev?.apply {
            val photoView = obtainPhotoView()
            if (photoView?.scale == 1f){
                when(ev.action){
                    MotionEvent.ACTION_DOWN ->{
                        if (pointerCount != 1)
                            return@apply
                        downX = x
                        downY = y
                        intercept = 0
                    }
                    MotionEvent.ACTION_MOVE ->{
                        if (pointerCount != 1)
                            return@apply
                        val disX = x - downX
                        val disY = y - downY
                        if (intercept == 0){
                            if (abs(disY/disX) < 1f){ //45度
                                intercept = 2
                            }else{
                                intercept = 1
                            }
                        }
                        if (intercept == 1){
                            listener?.invoke(SCROLL_MOVE)
                            photoView.translationY = disY
                            photoView.translationX = disX
                            var scale = abs(disY) / maxRollDistance
                            if (scale > 1) scale = 1f
                            scale = 1f - 0.6f * scale
                            photoView.scaleX = scale
                            photoView.scaleY = scale
                            backgroundView?.alpha = scale
                        }
                    }
                    MotionEvent.ACTION_UP ->{
                        if (abs(y - downY) > minRollDistance){
                            listener?.invoke(SCROLL_EXIT)
                            photoView.apply {
                                if (backgroundView!= null){
                                    val animAlpha = ObjectAnimator.ofFloat(backgroundView!!,"alpha",alpha,1f)
                                    animAlpha.duration = 200
                                    animAlpha.start()
                                }
                            }
                        }else{
                            listener?.invoke(SCROLL_CANCEL)
                            photoView.apply {
                                val animTransY =
                                    ObjectAnimator.ofFloat(this,"translationY",translationY,1f)
                                val animTransX =
                                    ObjectAnimator.ofFloat(this,"translationX",translationX,1f)
                                val animScaleY =
                                    ObjectAnimator.ofFloat(this,"scaleY",scaleY,1f)
                                val animScaleX =
                                    ObjectAnimator.ofFloat(this,"scaleX",scaleX,1f)
                                val animSet = AnimatorSet()
                                animSet.duration = 200
                                if (backgroundView!= null){
                                    val animAlpha = ObjectAnimator.ofFloat(backgroundView!!,"alpha",alpha,1f)
                                    animSet.play(animTransY).with(animTransX).with(animScaleY).with(animScaleX).with(animAlpha)
                                }else{
                                    animSet.play(animTransY).with(animTransX).with(animScaleY).with(animScaleX)
                                }
                                animSet.start()
                            }
                        }
                    }
                }
            }
            if (intercept == 1){
                parent.requestDisallowInterceptTouchEvent(true)
                return true
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}