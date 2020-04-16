package com.example.slidephotoview.photoview

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.ChangeImageTransform
import android.transition.ChangeTransform
import android.transition.TransitionSet
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.view.ViewCompat
import com.example.slidephotoview.ImageAdapter
import com.example.slidephotoview.R
import kotlinx.android.synthetic.main.activity_image_preview.*

class ImagePreviewActivity(): Activity() {
    companion object{
        @JvmStatic fun open(activity: Activity, view: View, flag: Int, uris:ArrayList<Uri>){
            val urisStr = ArrayList<String>()
            uris.forEach {
                urisStr.add(it.toString())
            }
            val intent = Intent(activity,ImagePreviewActivity::class.java).apply {
                putExtra("urisStr",urisStr)
                putExtra("flag",if (flag < uris.size) flag else 0)
            }

            val pair = Pair<View,String>(view,"shareView")
            val compat: ActivityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(activity, pair)

            activity.startActivity(intent,compat.toBundle())
        }
    }
    private var currentPage = 0
    private lateinit var adapter:ImagePreviewAdapter
    private val uris = ArrayList<Uri>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        setContentView(R.layout.activity_image_preview)
        initAnimation()
        initData()
        initView()
    }

    private fun initAnimation(){
        ViewCompat.setTransitionName(viewPager, "shareView")

        val set = TransitionSet()
        set.addTransition(ChangeBounds())
        set.addTransition(ChangeImageTransform())
        set.addTransition(ChangeTransform())

        window.sharedElementEnterTransition = set
        window.sharedElementExitTransition = set
    }

    private fun initData(){
        val urisStr = intent.getStringArrayListExtra("urisStr")?: ArrayList()
        currentPage = intent.getIntExtra("flag",0)
        urisStr.forEach {
            uris.add(Uri.parse(it))
        }
    }

    private fun initView(){
        adapter = ImagePreviewAdapter(uris){
            if (it == PhotoViewLayout.SCROLL_EXIT){
                onBackPressed()
            }
        }
        adapter.setBackgroundView(bgView)
        viewPager.adapter = adapter
        viewPager.setCurrentItem(currentPage,false)
    }
}
