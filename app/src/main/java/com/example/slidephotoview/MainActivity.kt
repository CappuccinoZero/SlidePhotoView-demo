package com.example.slidephotoview

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.slidephotoview.databinding.ActivityMainBinding
import com.example.slidephotoview.photoview.ImagePreviewActivity
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine

class MainActivity : AppCompatActivity() {
    companion object{
        const val REQUEST_OPEN_PHOTO = 25550
    }

    private val adapter by lazy { ImageAdapter{
        view,flag,uris->
        onClickImage(view,flag,uris)
    } }

    private lateinit var mBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        mBinding.lifecycleOwner = this
        mBinding.activity = this

        initView()
    }

    private fun initView(){
        mBinding.recyclerview.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        mBinding.recyclerview.adapter = adapter
    }

    private fun checkPermission():Boolean{
        val permission = ArrayList<String>().apply {
            add(Manifest.permission.READ_EXTERNAL_STORAGE)
            add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        for (index in permission.size -1 downTo 0){
            if (ContextCompat.checkSelfPermission(this,permission[index]) == PackageManager.PERMISSION_GRANTED)
                permission.remove(permission[index])
        }
        return if (permission.size == 0)
            true
        else{
            ActivityCompat.requestPermissions(this,permission.toTypedArray(),0)
            false
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            0 -> {
                for (index in permissions.indices){
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED){
                        return
                    }
                }
                openPhoto()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode){
            REQUEST_OPEN_PHOTO->{
                if (resultCode == Activity.RESULT_OK){
                    val uris = Matisse.obtainResult(data)
                    adapter.insert(uris)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun openPhoto() =
        Matisse.from(this)
            .choose(MimeType.ofImage())
            .countable(true)
            .maxSelectable(9)
            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
            .thumbnailScale(0.85f)
            .imageEngine(GlideEngine())
            .showPreview(false)
            .theme(R.style.Matisse_Dracula)
            .forResult(REQUEST_OPEN_PHOTO)

    private fun onClickImage(view:View,flag:Int,uris:ArrayList<Uri>){
        ImagePreviewActivity.open(this,view,flag,uris)
    }

    fun insertImage(view:View){
        if (checkPermission())
            openPhoto()
    }
}
